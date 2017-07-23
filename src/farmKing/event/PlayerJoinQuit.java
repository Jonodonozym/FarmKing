package farmKing.event;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.jonodonozym.UPEconomy.UPEconomyAPI;

import farmKing.crops.Farm;
import farmKing.main.Items;
import farmKing.main.Main;
import farmKing.utils.SimpleTime;
import net.md_5.bungee.api.ChatColor;

/**
 * Event handler class that listens for player join and quit events.
 * @author Jonodonozym
 * @version 1.0 Implemented the listener
 *
 */
public class PlayerJoinQuit implements Listener{
	private final Main plugin;
	
	public PlayerJoinQuit(Main plugin){
		this.plugin = plugin;
	}
	
	/**
	 * Activates when a player joins the game
	 * If this is their first time joining, gives them the default inventory and creates an account
	 * for them in the UPEconomy. If they have a farm, calculates offline time and earnings.
	 * Finally, initializes the scoreboard for that player.
	 * @param e
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		
		// first login
		if (!UPEconomyAPI.hasPlayer(player)){
			UPEconomyAPI.createAccount(player);

			player.getInventory().setItem(0,Items.returnHomeItem);
			player.getInventory().setItem(1,Items.buyTypeItem);
			player.getInventory().setItem(4,Items.tutorialBook);
			player.getInventory().setItem(7,Items.gemResetItem);
			player.getInventory().setHeldItemSlot(4);
			
		}
		
		// offline time and income
		if (Main.playerToFarm.containsKey(player.getName())){
			Farm f = Main.playerToFarm.get(player.getName());
			SimpleTime oldTime = Main.lastLogin.get(player.getName());
			double timeDifference = 60*SimpleTime.getCurrentTime().timeAfter(oldTime);
			if (timeDifference >= 60){
				f.offlineTimeMinutes += (int)(timeDifference/60);
				f.totalOfflineTimeMinutes += (int)(timeDifference/60);
				f.updateIncome();
				double offlineEarnings = timeDifference * f.currentIncome * f.offlineBonus;
				UPEconomyAPI.addBalance(player, offlineEarnings);
				f.earnings += offlineEarnings;
				player.sendMessage(ChatColor.GREEN+"While you were offline, you earnt $"+UPEconomyAPI.charFormat(offlineEarnings, 4));
			}
		}
		
		// scoreboard
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective title = sb.registerNewObjective("scoreboard", "dummy");
		
		title.setDisplayName(ChatColor.BOLD+""+ChatColor.YELLOW+"Farm Details");
		title.setDisplaySlot(DisplaySlot.SIDEBAR);
		title.getScore(" ").setScore(8);
		title.getScore(ChatColor.WHITE + "Balance").setScore(7);
		title.getScore(ChatColor.GREEN + "$0").setScore(6);
		title.getScore("  ").setScore(5);
		title.getScore(ChatColor.WHITE + "Income").setScore(4);
		title.getScore(ChatColor.GREEN + "$0/s").setScore(3);
		title.getScore("   ").setScore(2);
		title.getScore(ChatColor.WHITE + "Gems").setScore(1);
		title.getScore(ChatColor.GREEN + "0").setScore(0);
		
		player.setScoreboard(sb);
		
		plugin.sbBal.put(player.getName(), ChatColor.GREEN + "$0");
		plugin.sbInc.put(player.getName(), ChatColor.GREEN + "$0/s");
		plugin.sbGem.put(player.getName(), ChatColor.GREEN + "0");
		
		if (Main.playerToFarm.containsKey(player.getName())){
			Farm f = Main.playerToFarm.get(player.getName());
			sb.resetScores(plugin.sbGem.get(player.getName()));
			String gemsS = UPEconomyAPI.charFormat(f.gems, 4);
			title.getScore(ChatColor.GREEN + ""+gemsS).setScore(2);
			plugin.sbGem.put(player.getName(), ChatColor.GREEN + ""+gemsS);
			plugin.updateScoreboard(player);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		quitStuff(e.getPlayer().getName());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent e){
		quitStuff(e.getPlayer().getName());
	}
	
	private void quitStuff(String playerName){
		plugin.sbBal.remove(playerName);
		plugin.sbInc.remove(playerName);
		plugin.sbGem.remove(playerName);
		Main.lastLogin.put(playerName, SimpleTime.getCurrentTime());
	}
}
