
package jdz.farmKing.achievements;

import java.util.List;

import org.bukkit.OfflinePlayer;

import jdz.farmKing.upgrades.Upgrade;
import jdz.statsTracker.achievement.Achievement;
import jdz.statsTracker.achievement.achievementTypes.AchievementSeries;

public abstract class FarmAchievementSeries extends AchievementSeries {	
	public FarmAchievementSeries(String name, List<Achievement> achievements) {
		super(name, achievements);
	}

	public abstract Upgrade getUpgrade(OfflinePlayer player);
}
