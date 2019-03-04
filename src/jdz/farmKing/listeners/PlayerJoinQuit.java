package jdz.farmKing.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import jdz.bukkitUtils.events.Listener;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.FarmDB;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.utils.Items;

/**
 * Event handler class that listens for player join and quit events.
 * 
 * @author Jonodonozym
 * @version 1.0 Implemented the listener
 *
 */
public class PlayerJoinQuit implements Listener {

	/**
	 * Activates when a player joins the game If this is their first time
	 * joining, gives them the default inventory and creates an account for them
	 * in the UEconomy. If they have a farm, calculates offline time and
	 * earnings. Finally, initializes the scoreboard for that player.
	 * 
	 * @param e
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Items.give(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (PlayerFarms.hasFarm(player)) {
			Farm farm = PlayerFarms.get(player);
			farm.lastLogin = System.currentTimeMillis();
			FarmDB.getInstance().save(farm);
		}
	}
}
