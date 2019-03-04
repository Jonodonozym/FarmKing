
package jdz.farmKing.achievements.achievements;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import jdz.UEconomy.UEcoFormatter;
import jdz.bukkitUtils.misc.RomanNumber;
import jdz.farmKing.achievements.FarmAchievementSeries;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.upgrades.Upgrade;
import jdz.statsTracker.achievement.Achievement;
import jdz.statsTracker.achievement.achievementTypes.StatAchievement;

public class EarningsAchievement extends FarmAchievementSeries {
	private static final double[] required = new double[] { 1e3, 1e6, 1e9, 1e15, 1e21, 1e27, 1e33, 1e39 };

	public EarningsAchievement() {
		super("Green Fingers", getAchievements());
	}

	private static List<Achievement> getAchievements() {
		List<Achievement> achievements = new ArrayList<Achievement>();
		for (int i = 0; i < required.length; i++) {
			Achievement achievement = new StatAchievement("Green Fingers " + RomanNumber.of(i), FarmStats.EARNINGS,
					required[i], Material.GOLD_INGOT, (short) 0,
					"Earn $" + UEcoFormatter.charFormat(required[i], 1) + " in a single game");
			achievements.add(achievement);
		}
		return achievements;
	}

	@Override
	public Upgrade getUpgrade(OfflinePlayer player) {
		return Upgrade.emptyUpgrade();
	}
}
