package jdz.farmKing.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

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
