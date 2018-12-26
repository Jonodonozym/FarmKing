
package jdz.farmKing.element.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;
import jdz.farmKing.FarmKing;
import jdz.farmKing.element.Element;
import lombok.Getter;

public class ElementSelectInventory extends GuiMenu {
	@Getter private static final ElementSelectInventory instance = new ElementSelectInventory();
	private final Inventory alignSelectInventory;

	private ElementSelectInventory() {
		super(FarmKing.getInstance());

		int rows = Math.max(1, (Element.values().size() + 3) / 4);

		alignSelectInventory = Bukkit.createInventory(null, rows, "Choose your alignment");

		int lastRowSize = Element.values().size() % 4;
		if (lastRowSize == 0)
			lastRowSize = 4;

		int i = 0;
		for (Element element : Element.values())
			setItem(new ElementSelectItem(element), getSlot(rows, lastRowSize, i++), alignSelectInventory);
	}

	private int getSlot(int rows, int lastRowSize, int index) {
		if (index < (rows - 1) * 4)
			return ((index / 4 * 9) + 1 + 2 * (index % 4));
		return (index / 4 * 9) + (5 - lastRowSize + 2 * index % 4);
	}

	@Override
	public void open(Player player) {
		player.openInventory(alignSelectInventory);
	}
}
