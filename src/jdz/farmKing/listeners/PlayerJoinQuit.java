package jdz.farmKing.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import jdz.UEconomy.UEcoFormatter;
import jdz.UEconomy.data.UEcoBank;
import jdz.farmKing.HologramManager;
import jdz.farmKing.achievements.AchievementData;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.FarmScoreboards;
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
		player.getInventory().clear();
		player.getInventory().setItem(0, Items.returnHomeItem);
		player.getInventory().setItem(1, Items.buyTypeItem);
		player.getInventory().setItem(2, Items.achievementsItem);
		player.getInventory().setItem(8, Items.tutorialBook);
		player.getInventory().setItem(7, Items.gemResetItem);

		AchievementData.addPlayer(player);

		// offline time and income
		Farm f = PlayerFarms.get(player);

		if (EventFlag.ALIGNMENTS_UNLOCKED.isComplete(f))
			player.getInventory().addItem(Items.alignmentItem);

		double timeDifference = (System.currentTimeMillis() - f.lastLogin) / 1000.0;
		if (timeDifference >= 60) {
			FarmStats.OFFLINE_TIME.add(f, (int) (timeDifference / 60));

			double avgIncome = (f.currentIncome + f.updateIncome()) / 2;

			double offlineEarnings = timeDifference * avgIncome * FarmStats.OFFLINE_BONUS.get(f);

			UEcoBank.add(player, offlineEarnings);
			player.sendMessage(ChatColor.GREEN + "While you were offline for "
					+ FarmStatTime.timeFromSeconds((int) (timeDifference / 60)) + ", you earnt $"
					+ UEcoFormatter.charFormat(offlineEarnings));
		}

		FarmScoreboards.addPlayer(player);

	}

	private static void quitStuff(Player player) {
		PlayerFarms.get(player).lastLogin = System.currentTimeMillis();
		FarmIO.save(PlayerFarms.get(player));
		FarmScoreboards.removePlayer(player);
		AchievementData.removePlayer(player);
	}
}
