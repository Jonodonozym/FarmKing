package jdz.farmKing.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import com.jonodonozym.UPEconomy.UPEconomyAPI;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.FarmData;
import jdz.farmKing.upgrades.Upgrade;

public class ElementUpgradeInventory {
	public static Inventory alignSelectInventory;
	
	public Inventory inventory;
	public Element alignment;
	public boolean[] alignmentTiersBought = new boolean[Element.numTiers];
	public boolean[] alignmentUpgradesBought = new boolean[Element.numUpgrades];
	public final Farm farm;
	
	public ElementUpgradeInventory(Player p, Element alignment){
		farm = FarmData.playerToFarm.get(p);
		inventory = Bukkit.createInventory(p, Element.numTiers*9, "Alignment Upgrades");
		this.alignment = alignment;
		for (int i=0; i<Element.numTiers; i++){
			ItemStack t = new ItemStack(alignment.icon);
			ItemMeta tm = t.getItemMeta();
			tm.setDisplayName(ChatColor.RED+"Tier "+(i+1));
			tm.setLore(Arrays.asList(ChatColor.RED+"Costs "+Element.tierCost[i]+" "+alignment.name+" seeds"));
			t.setItemMeta(tm);
			inventory.setItem(i*9+2, t);
			
			for (int c=0; c<Element.upgradesPerTier; c++){
				t = new Wool(DyeColor.GRAY).toItemStack();
				tm = t.getItemMeta();
				tm.setDisplayName(ChatColor.GRAY+""+ChatColor.ITALIC+"Locked");
				t.setItemMeta(tm);
				inventory.setItem(i*9+4+c, t);
			}
		}
	}
	
	public ElementUpgradeInventory(Farm farm, Element alignment, boolean[] alignmentTiersBought, boolean[] alignmentUpgradesBought){
		this.farm = farm;
		this.alignment = alignment;
		this.alignmentTiersBought = alignmentTiersBought;
		this.alignmentUpgradesBought = alignmentUpgradesBought;
		inventory = Bukkit.createInventory(farm.owner.getPlayer(), Element.numTiers*9, "Alignment Upgrades");
		for (int i=0; i<Element.numTiers; i++){
			if (alignmentTiersBought[i]){
				ItemStack ts = new ItemStack(alignment.icon);
				ItemMeta tm = ts.getItemMeta();
				tm.setDisplayName(ChatColor.GREEN+"Tier "+(i+1)+" bought!");
				ts.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
				ts.setItemMeta(tm);
				inventory.setItem(i*9+2, ts);
				
				for (int a=0; a<Element.upgradesPerTier; a++){
					int index = a+4+i*9;
					ItemStack u = alignmentUpgradesBought[i*Element.upgradesPerTier+a]?
							new ItemStack(new Wool(DyeColor.GREEN).getItemType()) : new ItemStack(new Wool(DyeColor.RED).getItemType());
					ItemMeta um = u.getItemMeta();
					Upgrade upgrade = alignment.getUpgrade(index);
					if (alignmentUpgradesBought[i*Element.upgradesPerTier+a])
						um.setDisplayName(alignment.getColor()+upgrade.getName());
					else
						um.setDisplayName(alignment.getColor()+upgrade.getName()+" | Cost: $"+UPEconomyAPI.charFormat(Element.upgradeCost[index], 4));
					um.setLore(getUpgradeLore(index));
					u.setItemMeta(um);
					inventory.setItem(i*9+4+a, u);

				}
				
			}
			else{
				ItemStack t = new ItemStack(alignment.icon);
				ItemMeta tm = t.getItemMeta();
				tm.setDisplayName(ChatColor.RED+"Tier "+(i+1));
				tm.setLore(Arrays.asList(ChatColor.RED+"Costs "+Element.tierCost[i]+" "+alignment.name+" seeds"));
				t.setItemMeta(tm);
				inventory.setItem(i*9+2, t);
				
				for (int c=0; c<Element.upgradesPerTier; c++){
					t = new Wool(DyeColor.GRAY).toItemStack();
					tm = t.getItemMeta();
					tm.setDisplayName(ChatColor.GRAY+""+ChatColor.ITALIC+"Locked");
					t.setItemMeta(tm);
					inventory.setItem(i*9+4+c, t);
				}
			}
		}
	}
	
	public void updateInventory(Farm f){
		if (!inventory.getViewers().isEmpty())
			for (int i=0; i<Element.numTiers; i++)
				if (alignmentTiersBought[i])
					for (int c=0; c<Element.upgradesPerTier; c++){
						ItemStack is = inventory.getItem(i*9+4+c);
						ItemMeta im = is.getItemMeta();
						im.setLore(getUpgradeLore(i*Element.upgradesPerTier+c));
						is.setItemMeta(im);
					}
	}
	
	private List<String> getUpgradeLore(int upgrade){
		Upgrade u = alignment.getUpgrade(upgrade);
		int pos = (upgrade/Element.upgradesPerTier)*9+4+upgrade%Element.upgradesPerTier;
		ItemStack us = inventory.getItem(pos);
		ItemMeta im = us.getItemMeta();
		List<String> lore = new ArrayList<String>(im.getLore());
		String upgradeInfo = "";
		for (int i=0; i<u.getNumBonuses(); i++)
			if (u.isDisplayable(i)){
				double bonus = u.getBonus(i, farm);
				upgradeInfo = upgradeInfo+"("+UPEconomyAPI.charFormat(bonus, 4)+")   ";
			}
		lore.add(Upgrade.loreColor+upgradeInfo);
		return lore;
	}
	
	public static boolean onInventoryClick(InventoryClickEvent e){
		Player p = (Player)e.getWhoClicked();
		Inventory inv = p.getOpenInventory().getTopInventory();
		
		String name = inv.getName();
		Farm f = FarmData.playerToFarm.get(p.getName());
		int slot = e.getRawSlot();

        ItemStack stack = null;
        if (e.getCurrentItem() != null)
            stack = e.getCurrentItem();
        else if (e.getCursor() != null)
            stack = e.getCursor();
        
        if (stack == null || stack.getType() == null || stack.getType() == Material.AIR){
        	e.setCancelled(true);
        	return false;
        }
		
		if (name.equals("Choose your alignment")){
			String alignName = stack.getItemMeta().getDisplayName().substring(2);
			System.out.println(f);
			for (Element a: Element.align){
				if (a.name.equals(alignName)){
					f.elementInventory = new ElementUpgradeInventory(p,a);
					p.openInventory(f.elementInventory.inventory);
					return true;
				}
			}
		}
		if (name.equals(f.elementInventory.alignment.name)){
			int tier = slot/9;
			if (slot%9 == 2)
				if (!f.elementInventory.alignmentTiersBought[tier])
					if (f.getStat(f.elementInventory.alignment.getSeedStat()) >= Element.tierCost[tier]){
						f.addStat(f.elementInventory.alignment.getSeedStat(), -Element.tierCost[tier]);
						
						f.elementInventory.alignmentTiersBought[tier] = true;
						// update the tier icon
						ItemStack ts = inv.getItem(slot);
						ItemMeta tm = ts.getItemMeta();
						tm.setDisplayName(ChatColor.GREEN+"Tier "+(tier+1)+" bought!");
						tm.setLore(new ArrayList<String>());
						ts.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
						ts.setItemMeta(tm);
						inv.setItem(slot, ts);
						
						// update the upgrades
						for (int i=0; i<Element.upgradesPerTier; i++){
							int index = i+tier*Element.upgradesPerTier;
							ItemStack u = new ItemStack(new Wool(DyeColor.RED).getItemType());
							ItemMeta um = u.getItemMeta();
							Upgrade upgrade = f.elementInventory.alignment.getUpgrade(index);
							um.setDisplayName(f.elementInventory.alignment.getColor()+upgrade.getName()+" | Cost: $"+UPEconomyAPI.charFormat(Element.upgradeCost[index], 4));
							um.setLore(f.elementInventory.getUpgradeLore(index));
							u.setItemMeta(um);
							inv.setItem(slot+2+i, u);
						}
					}
			if (slot%9 > 3 && slot%9 < 7){
				int index = tier*Element.upgradesPerTier+slot%9-4;
				if (!f.elementInventory.alignmentUpgradesBought[index] && UPEconomyAPI.hasEnough(f.owner, Element.upgradeCost[index])){
					UPEconomyAPI.subBalance(f.owner, Element.upgradeCost[index]);
					f.elementInventory.alignmentUpgradesBought[index] = true;
					ItemStack u = e.getCursor();
					u.setType(new Wool(DyeColor.GREEN).getItemType());
					ItemMeta um = u.getItemMeta();
					Upgrade upgrade = f.elementInventory.alignment.getUpgrade(index);
					um.setDisplayName(f.elementInventory.alignment.getColor()+upgrade.getName());
					u.setItemMeta(um);
					f.elementInventory.inventory.setItem(slot, u);
				}
			}
			return true;
		}
		return false;
	}
	
	static{
		alignSelectInventory = Bukkit.createInventory(null, 9, "Choose your alignment");
		for (int i=0; i< Element.align.length; i++){
			Element a = Element.align[i];
			ItemStack is = new ItemStack(a.icon);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(a.getColor()+a.name);
			is.setItemMeta(im);
			alignSelectInventory.setItem(1+i*2, is);
		}
	}
	
	@Override
	public String toString(){
		String retString = alignment.alignmentIndex+"";
		for (boolean b: alignmentTiersBought)
			retString = retString + "," + b;
		for (boolean b: alignmentUpgradesBought)
			retString = retString + "," + b;
		return retString;
	}
	
	public static ElementUpgradeInventory fromString(String s, Farm f){
		String[] args = s.split(",");
		Element alignment = Element.align[Integer.parseInt(args[0])];
		boolean[] alignmentTiersBought = new boolean[Element.numTiers];
		boolean[] alignmentUpgradesBought = new boolean[Element.numUpgrades];
		for (int i=0; i<alignmentTiersBought.length; i++)
			alignmentTiersBought[i] = Boolean.parseBoolean(args[1+i]);
		for (int i=0; i<alignmentUpgradesBought.length; i++)
			alignmentUpgradesBought[i] = Boolean.parseBoolean(args[1+Element.numTiers+i]);
		return new ElementUpgradeInventory(f, alignment, alignmentTiersBought, alignmentUpgradesBought);
	}
}
