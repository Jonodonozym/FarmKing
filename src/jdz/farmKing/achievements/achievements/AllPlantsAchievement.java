
package jdz.farmKing.achievements.achievements;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import jdz.bukkitUtils.misc.RomanNumber;
import jdz.farmKing.achievements.FarmAchievementSeries;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.upgrades.Upgrade;
import jdz.statsTracker.achievement.Achievement;
import jdz.statsTracker.achievement.achievementTypes.StatAchievement;

public class AllPlantsAchievement extends FarmAchievementSeries {
	private static final int[] required = new int[] { 1, 10, 100, 250, 500, 1000, 2500, 5000 };

	public AllPlantsAchievement() {
		super("Farm King", getAchievements());
	}

	private static List<Achievement> getAchievements() {
		List<Achievement> achievements = new ArrayList<Achievement>();
		for (int i = 0; i < required.length; i++) {
			Achievement achievement = new StatAchievement("Farm King " + RomanNumber.of(i), FarmStats.TOTAL_CROPS,
					required[i], Material.DIAMOND_HOE, (short) 0, "Grow " + required[i] + " total plants");
			achievements.add(achievement);
		}
		return achievements;
	}

	@Override
	public Upgrade getUpgrade(OfflinePlayer player) {
		return Upgrade.emptyUpgrade();
	}
}
