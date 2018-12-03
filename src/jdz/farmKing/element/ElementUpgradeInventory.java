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

import jdz.bukkitUtils.guiMenu.guis.GuiMenuPlayer;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.upgrades.Upgrade;
import lombok.Getter;

public class ElementUpgradeInventory extends GuiMenuPlayer {
	@Getter private static final ElementUpgradeInventory instance = new ElementUpgradeInventory();

	public final Farm farm;
	public Inventory inventory;
	public Element element;
	public boolean[] tiersBought = new boolean[ElementMetaData.numTiers];
	public boolean[] upgradesBought = new boolean[ElementMetaData.numUpgrades];
	
	@Override
	protected void reloadInv(Player player, Inventory inv) {
		// TODO Auto-generated method stub
		
	}

	private static int locationFromIndex(int index) {
		return index / 3 * 9 + (index % 3) + 4;
	}

	@SuppressWarnings("deprecation")
	public ElementUpgradeInventory(Farm farm, Element alignment) {
		this.farm = farm;
		inventory = Bukkit.createInventory(farm.getOwner().getPlayer(), ElementMetaData.numTiers * 9,
				alignment.color + alignment.name);
		this.element = alignment;
		for (int i = 0; i < Element.numTiers; i++) {
			ItemStack tierStack = new ItemStack(alignment.icon);
			ItemMeta tierMeta = tierStack.getItemMeta();
			tierMeta.setDisplayName(ChatColor.RED + "Tier " + (i + 1));
			tierMeta.setLore(Arrays
					.asList(ChatColor.RED + "Costs " + Element.tierCost.get(i) + " " + alignment.name + " seeds"));
			tierStack.setItemMeta(tierMeta);
			inventory.setItem(i * 9 + 2, tierStack);

			for (int c = 0; c < Element.upgradesPerTier; c++) {
				ItemStack upgradeStack = new ItemStack(Material.WOOL);
				upgradeStack.setDurability(DyeColor.GRAY.getWoolData());
				ItemMeta upgradeMeta = upgradeStack.getItemMeta();
				upgradeMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + "Locked");
				upgradeStack.setItemMeta(upgradeMeta);
				inventory.setItem(i * 9 + 4 + c, upgradeStack);
			}
		}
	}

	public ElementUpgradeInventory(Farm farm, Element alignment, boolean[] alignmentTiersBought,
			boolean[] alignmentUpgradesBought) {
		this(farm, alignment);
		for (int i = 0; i < Element.numTiers; i++) {
			if (alignmentTiersBought[i]) {
				setTierPurchased(i);
				for (int a = 0; a < Element.upgradesPerTier; a++) {
					int index = i * Element.upgradesPerTier + a;
					if (alignmentUpgradesBought[index])
						setUpgradePurchased(index);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void setTierPurchased(int tier) {
		tiersBought[tier] = true;

		int slot = tier * 9 + 2;

		ItemStack tierStack = inventory.getItem(slot);
		ItemMeta tierMeta = tierStack.getItemMeta();
		tierMeta.setDisplayName(ChatColor.GREEN + "Tier " + (tier + 1) + " bought!");
		tierMeta.setLore(new ArrayList<String>());
		tierStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		tierStack.setItemMeta(tierMeta);
		inventory.setItem(slot, tierStack);

		for (int i = 0; i < Element.upgradesPerTier; i++) {
			int upgradeIndex = tier * Element.upgradesPerTier + i;
			int loc = locationFromIndex(upgradeIndex);
			ItemStack upgradeStack = inventory.getItem(loc);

			upgradeStack.setType(Material.WOOL);
			upgradeStack.setDurability(DyeColor.RED.getWoolData());
			ItemMeta upgradeMeta = upgradeStack.getItemMeta();
			upgradeMeta.setDisplayName(element.color + element.getUpgrade(upgradeIndex).getName() + " | $"
					+ UEconomyAPI.charFormat(Element.upgradeCost.get(upgradeIndex), 4));
			upgradeMeta.setLore(getUpgradeLore(upgradeIndex));
			upgradeStack.setItemMeta(upgradeMeta);

			inventory.setItem(loc, upgradeStack);
		}

	}

	@SuppressWarnings("deprecation")
	public void setUpgradePurchased(int upgradeIndex) {
		int loc = locationFromIndex(upgradeIndex);
		ItemStack is = inventory.getItem(loc);

		is.setType(Material.WOOL);
		is.setDurability(DyeColor.GREEN.getWoolData());
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(element.color + element.upgrades[upgradeIndex].getName());
		im.setLore(getUpgradeLore(upgradeIndex));

		inventory.setItem(loc, is);

		upgradesBought[upgradeIndex] = true;
	}

	public static void updateOpenInventories() {
		for (Farm f : PlayerFarms.playerToFarm.values()) {
			if (f.elementInventory != null && !f.elementInventory.inventory.getViewers().isEmpty())
				f.elementInventory.update();
		}
	}

	public void update() {
		for (int i = 0; i < Element.numTiers; i++)
			if (tiersBought[i])
				for (int c = 0; c < Element.upgradesPerTier; c++) {
					int index = i * Element.numTiers + c;
					ItemStack is = inventory.getItem(locationFromIndex(index));
					ItemMeta im = is.getItemMeta();
					im.setLore(getUpgradeLore(i * Element.upgradesPerTier + c));
					is.setItemMeta(im);
				}
	}

	private List<String> getUpgradeLore(int upgradeIndex) {
		Upgrade u = element.getUpgrade(upgradeIndex);
		List<String> lore = new ArrayList<String>();

		String upgradeInfo = "";
		for (int i = 0; i < u.getNumBonuses(); i++)
			if (u.isDisplayable(i)) {
				double bonus = u.getBonus(i, farm);
				upgradeInfo = upgradeInfo + "(" + u.getType(i).valueToString(bonus) + ")  ";
			}
		lore.add(Upgrade.loreColor + u.getDescription());
		if (upgradeInfo != "")
			lore.add(ChatColor.WHITE + upgradeInfo);
		return lore;
	}

	public static void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory inv = p.getOpenInventory().getTopInventory();

		String name = inv.getName();
		Farm f = PlayerFarms.playerToFarm.get(p);

		int slot = e.getRawSlot();

		ItemStack stack = null;
		if (e.getCurrentItem() != null)
			stack = e.getCurrentItem();
		else if (e.getCursor() != null)
			stack = e.getCursor();

		if (f.elementInventory != null
				&& name.equals(f.elementInventory.element.color + f.elementInventory.element.name)) {

			if (stack == null || stack.getType() == null || stack.getType() == Material.AIR)
				return;

			int tier = slot / 9;
			if (slot % 9 == 2)
				if (!f.elementInventory.tiersBought[tier])
					if (f.getSeeds(f.elementInventory.element) >= Element.tierCost.get(tier)) {
						f.addSeeds(f.elementInventory.element, -Element.tierCost.get(tier));
						f.elementInventory.setTierPurchased(tier);
					}
			if (slot % 9 > 3 && slot % 9 < 7) {
				int index = tier * Element.upgradesPerTier + slot % 9 - 4;
				if (f.elementInventory.tiersBought[tier] && !f.elementInventory.upgradesBought[index]
						&& UEconomyAPI.hasEnough(f.owner, Element.upgradeCost.get(index))) {
					UEconomyAPI.subBalance(f.owner, Element.upgradeCost.get(index));
					f.elementInventory.setUpgradePurchased(index);
				}
			}
			e.setCancelled(true);
		}
	}

	@Override
	public String toString() {
		String retString = element.name + "";
		for (boolean b : tiersBought)
			retString = retString + "," + b;
		for (boolean b : upgradesBought)
			retString = retString + "," + b;
		return retString;
	}

	public static ElementUpgradeInventory fromString(String s, Farm f) {
		String[] args = s.split(",");
		Element alignment = Element.elements.get(args[0]);
		boolean[] alignmentTiersBought = new boolean[Element.numTiers];
		boolean[] alignmentUpgradesBought = new boolean[Element.numUpgrades];
		for (int i = 0; i < alignmentTiersBought.length; i++)
			alignmentTiersBought[i] = Boolean.parseBoolean(args[1 + i]);
		for (int i = 0; i < alignmentUpgradesBought.length; i++)
			alignmentUpgradesBought[i] = Boolean.parseBoolean(args[1 + Element.numTiers + i]);
		return new ElementUpgradeInventory(f, alignment, alignmentTiersBought, alignmentUpgradesBought);
	}
}
