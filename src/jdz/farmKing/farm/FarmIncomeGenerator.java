
package jdz.farmKing.farm;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import jdz.UEconomy.UEcoFormatter;
import jdz.UEconomy.data.UEcoBank;
import jdz.bukkitUtils.events.Listener;
import jdz.farmKing.FarmKing;
import jdz.farmKing.achievements.AchievementData;
import jdz.farmKing.element.ElementUpgradeInventory;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.stats.types.FarmStatTime;
import net.md_5.bungee.api.ChatColor;

public class FarmIncomeGenerator implements Listener {
	static {
		Bukkit.getScheduler().runTaskTimer(FarmKing.getInstance(), () -> {
			for (Player p : Bukkit.getOnlinePlayers())
				if (PlayerFarms.hasFarm(p))
					onSecond(p, PlayerFarms.get(p));
		}, 20, 20);
	}

	private static void onSecond(Player player, Farm farm) {
		farm.update();

		UEcoBank.add(player, farm.currentIncome);
		
		farm.doManualClick(farm.autoClicksPerSecond);
		
		FarmScoreboards.updateScoreboard(player);
		ElementUpgradeInventory.updateOpenInventories();
		AchievementData.updateAchievements(farm);
	}

	@EventHandler
	public void giveOfflineEarnings(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		if (!PlayerFarms.hasFarm(player))
			return;

		Farm farm = PlayerFarms.get(player);

		int timeDifference = (int) ((System.currentTimeMillis() - farm.lastLogin) / 1000.0);
		farm.lastLogin = System.currentTimeMillis();
		FarmStats.OFFLINE_TIME.add(farm, timeDifference);

		if (timeDifference <= 60)
			return;

		double avgIncome = (farm.currentIncome + farm.update()) / 2;
		double offlineEarnings = timeDifference * avgIncome * FarmStats.OFFLINE_BONUS.get(farm);

		UEcoBank.add(player, offlineEarnings);
		player.sendMessage(
				ChatColor.GREEN + "While you were offline for " + FarmStatTime.timeFromSeconds(timeDifference)
						+ ", you earnt $" + UEcoFormatter.charFormat(offlineEarnings));
	}
}
