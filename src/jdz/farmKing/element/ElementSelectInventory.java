
package jdz.farmKing.element;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;
import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;
import jdz.farmKing.FarmKing;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.PlayerFarms;
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
			setItem(new AlignmentItem(element), getSlot(rows, lastRowSize, i++), alignSelectInventory);
	}

	private int getSlot(int rows, int lastRowSize, int index) {
		if (index < (rows - 1) * 4)
			return ((index / 4 * 9) + 1 + 2 * (index % 4));
		return (index / 4 * 9) + (5 - lastRowSize + 2 * index % 4);
	}

	private class AlignmentItem extends ClickableStack {
		private final Element element;

		public AlignmentItem(Element element) {
			super(element.icon, element.color + element.name);
			this.element = element;
		}

		@Override
		public void onClick(GuiMenu menu, InventoryClickEvent event) {
			Player p = (Player) event.getWhoClicked();
			Farm farm = PlayerFarms.get(p);

			farm.elementInventory = new ElementUpgradeInventory(farm, element);
			p.openInventory(farm.elementInventory.inventory);
		}

	}

	@Override
	public void open(Player player) {
		player.openInventory(alignSelectInventory);
	}
}
