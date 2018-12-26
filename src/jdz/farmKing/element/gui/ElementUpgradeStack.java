
package jdz.farmKing.element.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.UEconomy.UEcoFormatter;
import jdz.bukkitUtils.guiMenu.guis.GuiMenu;
import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;
import jdz.farmKing.element.Element;
import jdz.farmKing.element.ElementMetaData;
import jdz.farmKing.element.data.PlayerElementData;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.upgrades.Upgrade;

public class ElementUpgradeStack extends ClickableStack {
	private final PlayerElementData data;
	private final Element element;
	private final int tier;
	private final int upgradeIndex;
	private final Upgrade upgrade;

	public ElementUpgradeStack(PlayerElementData data, int tier, int upgrade) {
		super(new ItemStack(Material.WOOL));
		this.data = data;
		this.element = data.getElement();
		this.tier = tier;
		this.upgradeIndex = upgrade;
		this.upgrade = element.getUpgrade(tier, upgrade);
		update();
	}

	@Override
	public void onClick(Player player, GuiMenu menu, InventoryClickEvent event) {
		if (!data.hasBoughtTier(tier))
			return;

		if (data.hasBoughtUpgrade(tier, upgradeIndex))
			return;


		Farm farm = PlayerFarms.get(player);
		double upgradeCost = ElementMetaData.getUpgradeCost(tier, upgradeIndex);

		if (FarmStats.SEEDS(data.getElement()).get(player) < upgradeCost)
			return;

		FarmStats.SEEDS(data.getElement()).subtract(farm, upgradeCost);
		data.setBoughtUpgrade(tier, upgradeIndex);
		menu.updateItems(event.getClickedInventory());
	}

	@Override
	@SuppressWarnings("deprecation")
	public void update() {
		if (!data.hasBoughtTier(tier)) {
			setData(DyeColor.GRAY.getWoolData());
			setName(ChatColor.GRAY + "" + ChatColor.ITALIC + "Locked");
		}

		else if (!data.hasBoughtUpgrade(tier, upgradeIndex)) {
			setData(DyeColor.RED.getWoolData());
			setName(element.color + element.getUpgrade(tier, upgradeIndex).getName() + " | $"
					+ UEcoFormatter.charFormat(ElementMetaData.getUpgradeCost(tier, upgradeIndex), 4));
			setLore(getUpgradeLore());
		}

		else {
			setData(DyeColor.GREEN.getWoolData());
			setName(element.color + element.getUpgrade(tier, upgradeIndex).getName());
			setLore(getUpgradeLore());
		}
	}

	private List<String> getUpgradeLore() {
		List<String> lore = new ArrayList<String>();

		Farm farm = PlayerFarms.get(data.getPlayer());

		lore.add(Upgrade.loreColor + upgrade.getDescription());

		String bonuses = "";
		for (int i = 0; i < upgrade.getNumBonuses(); i++)
			if (upgrade.isDisplayable(i)) {
				double bonus = upgrade.getBonus(i, farm);
				String formattedBonus = upgrade.getType(i).valueToString(bonus);
				bonuses = bonuses + "(" + formattedBonus + ")  ";
			}

		if (bonuses != "")
			lore.add(ChatColor.WHITE + bonuses);

		return lore;
	}
}
