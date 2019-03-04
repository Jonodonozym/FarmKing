
package jdz.farmKing.achievements.achievements;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;

import jdz.bukkitUtils.misc.RomanNumber;
import jdz.farmKing.achievements.FarmAchievementSeries;
import jdz.farmKing.crops.CropType;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.upgrades.Upgrade;
import jdz.statsTracker.achievement.Achievement;
import jdz.statsTracker.achievement.achievementTypes.StatAchievement;
import jdz.statsTracker.stats.StatType;

public class CropPlantsAchievement extends FarmAchievementSeries {
	private static final int[] required = new int[] { 1, 10, 100, 250, 500, 1000, 2500, 5000 };

	public CropPlantsAchievement(CropType type) {
		super(type.getName() + " Farmer", getAchievements(type));
	}

	private static List<Achievement> getAchievements(CropType type) {
		StatType stat = FarmStats.CROP_AMOUNT(type);
		List<Achievement> achievements = new ArrayList<Achievement>();
		for (int i = 0; i < required.length; i++) {
			Achievement achievement = new StatAchievement(type.getName() + " Farmer " + RomanNumber.of(i), stat,
					required[i], type.getIcon().getType(), type.getIcon().getDurability(),
					"Grow " + required[i] + " " + type.getName() + " plants");
			achievements.add(achievement);
		}
		return achievements;
	}

	@Override
	public Upgrade getUpgrade(OfflinePlayer player) {
		return Upgrade.emptyUpgrade();
	}
}
