package farmKing.alignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import com.jonodonozym.UPEconomy.UPEconomyAPI;

import farmKing.crops.Farm;
import farmKing.main.Main;
import farmKing.upgrades.Upgrade;

public class AlignmentUpgradeInventory {
	public static Inventory alignSelectInventory;
	
	public Inventory inventory;
	public Alignment alignment;
	public boolean[] alignmentTiersBought = new boolean[Alignment.numTiers];
	public boolean[] alignmentUpgradesBought = new boolean[Alignment.numUpgrades];
	public final Farm farm;
	
	public AlignmentUpgradeInventory(Player p, Alignment alignment){
		farm = Main.playerToFarm.get(p);
		inventory = Bukkit.createInventory(p, Alignment.numTiers*9, "Alignment Upgrades");
		this.alignment = alignment;
		for (int i=0; i<Alignment.numTiers; i++){
			ItemStack t = new ItemStack(alignment.icon);
			ItemMeta tm = t.getItemMeta();
			tm.setDisplayName(ChatColor.RED+"Tier "+(i+1));
			tm.setLore(Arrays.asList(ChatColor.RED+"Costs "+Alignment.tierCost[i]+" "+alignment.name+" seeds"));
			t.setItemMeta(tm);
			inventory.setItem(i*9+2, t);
			
			for (int c=0; c<Alignment.upgradesPerTier; c++){
				t = new Wool(DyeColor.GRAY).toItemStack();
				tm = t.getItemMeta();
				tm.setDisplayName(ChatColor.GRAY+""+ChatColor.ITALIC+"Locked");
				t.setItemMeta(tm);
				inventory.setItem(i*9+4+c, t);
			}
		}
	}
	
	public AlignmentUpgradeInventory(Farm farm, Alignment alignment, boolean[] alignmentTiersBought, boolean[] alignmentUpgradesBought){
		this.farm = farm;
		this.alignment = alignment;
		this.alignmentTiersBought = alignmentTiersBought;
		this.alignmentUpgradesBought = alignmentUpgradesBought;
		inventory = Bukkit.createInventory(farm.owner.getPlayer(), Alignment.numTiers*9, "Alignment Upgrades");
		for (int i=0; i<Alignment.numTiers; i++){
			if (alignmentTiersBought[i]){
				ItemStack ts = new ItemStack(alignment.icon);
				ItemMeta tm = ts.getItemMeta();
				tm.setDisplayName(ChatColor.GREEN+"Tier "+(i+1)+" bought!");
				ts.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
				ts.setItemMeta(tm);
				inventory.setItem(i*9+2, ts);
				
				for (int a=0; a<Alignment.upgradesPerTier; a++){
					int index = a+4+i*9;
					ItemStack u = alignmentUpgradesBought[i*Alignment.upgradesPerTier+a]?
							new ItemStack(new Wool(DyeColor.GREEN).getItemType()) : new ItemStack(new Wool(DyeColor.RED).getItemType());
					ItemMeta um = u.getItemMeta();
					Upgrade upgrade = alignment.getUpgrade(index);
					if (alignmentUpgradesBought[i*Alignment.upgradesPerTier+a])
						um.setDisplayName(alignment.getColor()+upgrade.getName());
					else
						um.setDisplayName(alignment.getColor()+upgrade.getName()+" | Cost: $"+UPEconomyAPI.charFormat(Alignment.upgradeCost[index], 4));
					um.setLore(getUpgradeLore(index));
					u.setItemMeta(um);
					inventory.setItem(i*9+4+a, u);

				}
				
			}
			else{
				ItemStack t = new ItemStack(alignment.icon);
				ItemMeta tm = t.getItemMeta();
				tm.setDisplayName(ChatColor.RED+"Tier "+(i+1));
				tm.setLore(Arrays.asList(ChatColor.RED+"Costs "+Alignment.tierCost[i]+" "+alignment.name+" seeds"));
				t.setItemMeta(tm);
				inventory.setItem(i*9+2, t);
				
				for (int c=0; c<Alignment.upgradesPerTier; c++){
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
			for (int i=0; i<Alignment.numTiers; i++)
				if (alignmentTiersBought[i])
					for (int c=0; c<Alignment.upgradesPerTier; c++){
						ItemStack is = inventory.getItem(i*9+4+c);
						ItemMeta im = is.getItemMeta();
						im.setLore(getUpgradeLore(i*Alignment.upgradesPerTier+c));
						is.setItemMeta(im);
					}
	}
	
	private List<String> getUpgradeLore(int upgrade){
		Upgrade u = alignment.getUpgrade(upgrade);
		int pos = (upgrade/Alignment.upgradesPerTier)*9+4+upgrade%Alignment.upgradesPerTier;
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
		Inventory inv = e.getInventory();
		String name = inv.getName();
		Player p = (Player)e.getWhoClicked();
		Farm f = Main.playerToFarm.get(p);
		int slot = e.getRawSlot();
		
		if (name.equals("Choose your alignment")){
			f.AUI = new AlignmentUpgradeInventory(p, Alignment.align[(slot-1)/2]);
			p.openInventory(f.AUI.inventory);
			return true;
		}
		if (name.equals(f.AUI.alignment.name)){
			int tier = slot/9;
			if (slot%9 == 2)
				if (!f.AUI.alignmentTiersBought[tier])
					if (f.seeds[f.AUI.alignment.alignmentIndex] >= Alignment.tierCost[tier]){
						f.seeds[f.AUI.alignment.alignmentIndex] -= Alignment.tierCost[tier];
						
						f.AUI.alignmentTiersBought[tier] = true;
						// update the tier icon
						ItemStack ts = inv.getItem(slot);
						ItemMeta tm = ts.getItemMeta();
						tm.setDisplayName(ChatColor.GREEN+"Tier "+(tier+1)+" bought!");
						tm.setLore(new ArrayList<String>());
						ts.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
						ts.setItemMeta(tm);
						inv.setItem(slot, ts);
						
						// update the upgrades
						for (int i=0; i<Alignment.upgradesPerTier; i++){
							int index = i+tier*Alignment.upgradesPerTier;
							ItemStack u = new ItemStack(new Wool(DyeColor.RED).getItemType());
							ItemMeta um = u.getItemMeta();
							Upgrade upgrade = f.AUI.alignment.getUpgrade(index);
							um.setDisplayName(f.AUI.alignment.getColor()+upgrade.getName()+" | Cost: $"+UPEconomyAPI.charFormat(Alignment.upgradeCost[index], 4));
							um.setLore(f.AUI.getUpgradeLore(index));
							u.setItemMeta(um);
							inv.setItem(slot+2+i, u);
						}
					}
			if (slot%9 > 3 && slot%9 < 7){
				int index = tier*Alignment.upgradesPerTier+slot%9-4;
				if (!f.AUI.alignmentUpgradesBought[index] && UPEconomyAPI.hasEnough(f.owner, Alignment.upgradeCost[index])){
					UPEconomyAPI.subBalance(f.owner, Alignment.upgradeCost[index]);
					f.AUI.alignmentUpgradesBought[index] = true;
					ItemStack u = e.getCursor();
					u.setType(new Wool(DyeColor.GREEN).getItemType());
					ItemMeta um = u.getItemMeta();
					Upgrade upgrade = f.AUI.alignment.getUpgrade(index);
					um.setDisplayName(f.AUI.alignment.getColor()+upgrade.getName());
					u.setItemMeta(um);
					f.AUI.inventory.setItem(slot, u);
				}
			}
			return true;
		}
		return false;
	}
	
	static{
		alignSelectInventory = Bukkit.createInventory(null, 9, "Choose your alignment");
		for (int i=0; i< Alignment.align.length; i++){
			Alignment a = Alignment.align[i];
			alignSelectInventory.setItem(1+i*2, new ItemStack(a.icon, 1));
		}
	}
	
	public String toString(){
		String retString = alignment.alignmentIndex+"";
		for (boolean b: alignmentTiersBought)
			retString = retString + "," + b;
		for (boolean b: alignmentUpgradesBought)
			retString = retString + "," + b;
		return retString;
	}
	
	public static AlignmentUpgradeInventory fromString(String s, Farm f){
		String[] args = s.split(",");
		Alignment alignment = Alignment.align[Integer.parseInt(args[0])];
		boolean[] alignmentTiersBought = new boolean[Alignment.numTiers];
		boolean[] alignmentUpgradesBought = new boolean[Alignment.numUpgrades];
		for (int i=0; i<alignmentTiersBought.length; i++)
			alignmentTiersBought[i] = Boolean.parseBoolean(args[1+i]);
		for (int i=0; i<alignmentUpgradesBought.length; i++)
			alignmentUpgradesBought[i] = Boolean.parseBoolean(args[1+Alignment.numTiers+i]);
		return new AlignmentUpgradeInventory(f, alignment, alignmentTiersBought, alignmentUpgradesBought);
	}
}
