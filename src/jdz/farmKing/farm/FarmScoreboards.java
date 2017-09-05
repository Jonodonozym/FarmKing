
package jdz.farmKing.farm;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.jonodonozym.UPEconomy.UPEconomyAPI;

import net.md_5.bungee.api.ChatColor;

public class FarmScoreboards {
	public static Map<Player,String> sbBal = new HashMap<Player,String>();
	public static Map<Player,String> sbInc = new HashMap<Player,String>();
	public static Map<Player,String> sbGem = new HashMap<Player,String>();
	
	public static void updateScoreboard(Player player){
		Scoreboard scoreboard = player.getScoreboard();
		
		Objective sb = scoreboard.getObjective("scoreboard");

		scoreboard.resetScores(sbBal.get(player));
		scoreboard.resetScores(sbInc.get(player));
		
		String balance = ChatColor.GREEN + "$"+UPEconomyAPI.charFormat(UPEconomyAPI.getBalance(player), 4);
		String income = ChatColor.GREEN + "$"+UPEconomyAPI.charFormat(FarmData.playerToFarm.get(player).currentIncome, 4)+"/s";
		
		sb.getScore(balance).setScore(6);
		sb.getScore(income).setScore(3);
		
		sbBal.put(player, balance);
		sbInc.put(player, income);
	}
	
	public static void addPlayer(Player player){
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
		
		sbBal.put(player, ChatColor.GREEN + "$0");
		sbInc.put(player, ChatColor.GREEN + "$0/s");
		sbGem.put(player, ChatColor.GREEN + "0");
		
		if (FarmData.playerToFarm.containsKey(player)){
			Farm f = FarmData.playerToFarm.get(player);
			sb.resetScores(sbGem.get(player));
			String gemsS = UPEconomyAPI.charFormat(f.getStat(StatType.FARM_GEMS), 4);
			title.getScore(ChatColor.GREEN + ""+gemsS).setScore(0);
			sbGem.put(player, ChatColor.GREEN + ""+gemsS);
			updateScoreboard(player);
		}
	}
	
	public static void removePlayer(Player player){
		sbBal.remove(player);
		sbInc.remove(player);
		sbGem.remove(player);
	}
}
