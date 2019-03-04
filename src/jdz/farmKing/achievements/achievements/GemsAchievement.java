
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

public class GemsAchievement extends FarmAchievementSeries {
	private static final double[] required = new double[] { 100, 500, 5e3, 1e6, 1e9, 1e12, 1e15, 1e21 };

	public GemsAchievement(String name, List<Achievement> achievements) {
		super("Gem Hoarder", getAchievements());
	}

	private static List<Achievement> getAchievements() {
		List<Achievement> achievements = new ArrayList<Achievement>();
		for (int i = 0; i < required.length; i++) {
			Achievement achievement = new StatAchievement("Gem Hoarder " + RomanNumber.of(i), FarmStats.GEMS,
					required[i], Material.DIAMOND, (short) 0,
					"Gather " + UEcoFormatter.charFormat(required[i], 1) + " gems");
			achievements.add(achievement);
		}
		return achievements;
	}

	@Override
	public Upgrade getUpgrade(OfflinePlayer player) {
		return Upgrade.emptyUpgrade();
	}
}
