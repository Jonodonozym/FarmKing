package jdz.farmKing.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.jonodonozym.UPEconomy.UPEconomyAPI;

import jdz.farmKing.achievements.AchievementData;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.FarmData;
import jdz.farmKing.farm.FarmIO;
import jdz.farmKing.farm.FarmScoreboards;
import jdz.farmKing.farm.StatType;
import jdz.farmKing.utils.Items;
import jdz.farmKing.utils.SimpleTime;
import net.md_5.bungee.api.ChatColor;

/**
 * Event handler class that listens for player join and quit events.
 * @author Jonodonozym
 * @version 1.0 Implemented the listener
 *
 */
public class PlayerJoinQuit implements Listener{
	
	/**
	 * Activates when a player joins the game
	 * If this is their first time joining, gives them the default inventory and creates an account
	 * for them in the UPEconomy. If they have a farm, calculates offline time and earnings.
	 * Finally, initializes the scoreboard for that player.
	 * @param e
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		playerJoinSetup(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		quitStuff(e.getPlayer());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent e){
		quitStuff(e.getPlayer());
	}
	
	public static void playerJoinSetup(Player player){
		// first login
				if (!UPEconomyAPI.hasPlayer(player)){
					UPEconomyAPI.createAccount(player);

					player.getInventory().setItem(0,Items.returnHomeItem);
					player.getInventory().setItem(1,Items.buyTypeItem);
					player.getInventory().setItem(2,Items.achievementsItem);
					player.getInventory().setItem(6,Items.tutorialBook);
					player.getInventory().setItem(7,Items.gemResetItem);
					player.getInventory().setHeldItemSlot(4);
					
				}
				
				// offline time and income
				FarmIO.loadFarm(player);
				if (FarmData.playerToFarm.containsKey(player.getName())){
					Farm f = FarmData.playerToFarm.get(player.getName());
					SimpleTime oldTime = FarmData.lastLogin.get(player.getName());
					double timeDifference = 60*SimpleTime.getCurrentTime().timeAfter(oldTime);
					if (timeDifference >= 60){
						f.addStat(StatType.FARM_OFFLINE_TIME, (int)timeDifference/60.0);
						
						f.updateIncome();
						double offlineEarnings = timeDifference * f.currentIncome * f.getStat(StatType.FARM_OFFLINE_BONUS);
						
						UPEconomyAPI.addBalance(player, offlineEarnings);
						f.addStat(StatType.FARM_EARNINGS, offlineEarnings);
						player.sendMessage(ChatColor.GREEN+"While you were offline, you earnt $"+UPEconomyAPI.charFormat(offlineEarnings, 4));
					}
				}
				
				FarmScoreboards.addPlayer(player);
				AchievementData.addPlayer(player);
	}
	
	private static void quitStuff(Player player){
		FarmIO.saveFarm(FarmData.playerToFarm.get(player));
		FarmScoreboards.removePlayer(player);
		FarmData.lastLogin.put(player.getName(), SimpleTime.getCurrentTime());
		AchievementData.removePlayer(player);
	}
}
