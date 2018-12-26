
package jdz.farmKing.element.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;
import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;
import jdz.farmKing.element.Element;
import jdz.farmKing.element.data.PlayerElementDataManager;

public class ElementSelectItem extends ClickableStack {
	private final Element element;

	public ElementSelectItem(Element element) {
		super(element.icon, element.color + element.name);
		this.element = element;
	}

	@Override
	public void onClick(Player player, GuiMenu menu, InventoryClickEvent event) {
		PlayerElementDataManager.getInstance().get(player).setElement(element);
		ElementUpgradeInventory.getInstance().open(player);
	}
}
