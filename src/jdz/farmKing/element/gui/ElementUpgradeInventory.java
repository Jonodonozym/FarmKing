package jdz.farmKing.element.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import jdz.bukkitUtils.guiMenu.guis.GuiMenuPlayer;
import jdz.farmKing.FarmKing;
import jdz.farmKing.element.Element;
import jdz.farmKing.element.ElementMetaData;
import jdz.farmKing.element.data.PlayerElementData;
import jdz.farmKing.element.data.PlayerElementDataManager;
import lombok.Getter;

public class ElementUpgradeInventory extends GuiMenuPlayer {
	@Getter private static final ElementUpgradeInventory instance = new ElementUpgradeInventory();

	public ElementUpgradeInventory() {
		super(FarmKing.getInstance());
	}

	@Override
	protected Inventory createInventory(Player player) {
		PlayerElementData data = PlayerElementDataManager.getInstance().get(player);
		Element element = data.getElement();

		Inventory inv = Bukkit.createInventory(player, 54, element.color + element.name + " element");

		for (int i = 0; i < ElementMetaData.numTiers; i++) {
			setItem(new ElementTierStack(data, i), i * 9 + 2, inv);
			for (int j = 0; j < ElementMetaData.upgradesPerTier; j++)
				setItem(new ElementUpgradeStack(data, i, j), i * 9 + 4 + j, inv);
		}

		return inv;
	}
}
