package jdz.farmKing.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

public class ItemSpawn implements Listener {

	@EventHandler
	public void itemSpawn(ItemSpawnEvent event){
		event.setCancelled(true);
	}
}
