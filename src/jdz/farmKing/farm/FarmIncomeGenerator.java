
package jdz.farmKing.farm;

import static jdz.farmKing.upgrades.UpgradeBonus.AUTO_CLICKS;
import static jdz.farmKing.upgrades.UpgradeBonus.OFFLINE_INCOME;
import static jdz.farmKing.upgrades.UpgradeBonus.ONLINE_INCOME;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import jdz.UEconomy.UEcoFormatter;
import jdz.UEconomy.data.UEcoBank;
import jdz.bukkitUtils.events.Listener;
import jdz.farmKing.FarmKing;
import jdz.farmKing.element.gui.ElementUpgradeInventory;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.stats.types.FarmStatTime;
import net.md_5.bungee.api.ChatColor;

public class FarmIncomeGenerator implements Listener {
	static {
		Bukkit.getScheduler().runTaskTimer(FarmKing.getInstance(), () -> {
			ElementUpgradeInventory.getInstance().updateOpen();
			for (Player player : Bukkit.getOnlinePlayers())
				if (PlayerFarms.hasFarm(player)) {
					PlayerFarms.get(player).onSecond();
					FarmScoreboards.updateScoreboard(player);
				}
		}, 20, 20);
	}

	@EventHandler
	public void giveOfflineEarnings(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		if (!PlayerFarms.hasFarm(player))
			return;

		Farm farm = PlayerFarms.get(player);

		farm.updateIncome();
		double oldIncome = farm.getIncome();

		long secondsPassed = (System.currentTimeMillis() - farm.lastLogin) / 1000L;
		farm.lastLogin = System.currentTimeMillis();
		FarmStats.OFFLINE_TIME.add(farm, secondsPassed);

		if (secondsPassed <= 60)
			return;

		farm.updateIncome();
		double newIncome = farm.getIncome();

		double avgIncome = (newIncome + oldIncome) / 2 / farm.getUpgradeBonus(ONLINE_INCOME);
		double offlineEarnings = secondsPassed * avgIncome * farm.getUpgradeBonus(OFFLINE_INCOME);

		farm.getGrass().autoClick(farm.getUpgradeBonus(AUTO_CLICKS) * secondsPassed);
		farm.addWorkerSeedsForSeconds(secondsPassed);

		UEcoBank.add(player, offlineEarnings);
		player.sendMessage(ChatColor.GREEN + "While you were offline for " + FarmStatTime.timeFromSeconds(secondsPassed)
				+ ", you earnt $" + UEcoFormatter.charFormat(offlineEarnings));

	}
}
