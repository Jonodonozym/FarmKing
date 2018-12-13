package jdz.farmKing.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import jdz.bukkitUtils.events.Listener;

public class Invincibility implements Listener {
	@EventHandler
	public void stopDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void stopHunger(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

}
