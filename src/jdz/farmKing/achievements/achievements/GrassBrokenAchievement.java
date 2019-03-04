
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

public class GrassBrokenAchievement extends FarmAchievementSeries {
	private static final double[] required = new double[] { 100, 2500, 10e3, 100e3, 500e3, 1e4 };

	public GrassBrokenAchievement() {
		super("Lawnmower", getAchievements());
	}

	private static List<Achievement> getAchievements() {
		List<Achievement> achievements = new ArrayList<Achievement>();
		for (int i = 0; i < required.length; i++) {
			Achievement achievement = new StatAchievement("Lawnmower " + RomanNumber.of(i), FarmStats.CLICKS_MANUAL,
					required[i], Material.GRASS, (short) 0, "Break " + required[i] + " tall grass plants");
			achievements.add(achievement);
		}
		return achievements;
	}

	@Override
	public Upgrade getUpgrade(OfflinePlayer player) {
		return Upgrade.emptyUpgrade();
	}
}
