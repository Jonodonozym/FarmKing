package jdz.farmKing.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import jdz.UEconomy.UEcoFormatter;
import jdz.UEconomy.data.UEcoBank;
import jdz.bukkitUtils.events.Listener;
import jdz.farmKing.HologramManager;
import jdz.farmKing.achievements.AchievementData;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.FarmScoreboards;
import jdz.farmKing.farm.FarmIncomeGenerator;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.stats.EventFlag;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.stats.types.FarmStatTime;
import jdz.farmKing.utils.Items;
import jdz.statsTracker.stats.StatType;
import net.md_5.bungee.api.ChatColor;

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
		playerJoinSetup(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		quitStuff(e.getPlayer());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		quitStuff(e.getPlayer());
	}

	public static void playerJoinSetup(Player player) {
		Farm f = PlayerFarms.get(player);

		Items.give(player);
		
		AchievementData.addPlayer(player);
	}

	private static void quitStuff(Player player) {
		PlayerFarms.get(player).lastLogin = System.currentTimeMillis();
		FarmIO.save(PlayerFarms.get(player));
		AchievementData.removePlayer(player);
	}
}
