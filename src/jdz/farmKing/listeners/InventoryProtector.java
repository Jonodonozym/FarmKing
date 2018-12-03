
package jdz.farmKing.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.PlayerInventory;

import jdz.bukkitUtils.events.Listener;

public class InventoryProtector implements Listener {

	@EventHandler
	public void onSwapHandEvent(PlayerSwapHandItemsEvent e) {
		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDrop(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onInvMove(InventoryMoveItemEvent e) {
		PlayerInventory inv = null;
		if (e.getSource() instanceof PlayerInventory)
			inv = (PlayerInventory) e.getSource();
		else if (e.getDestination() instanceof PlayerInventory)
			inv = (PlayerInventory) e.getDestination();
		if (inv != null)
			e.setCancelled(true);
	}

}
