
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

public class OnlineTimeAchievements extends FarmAchievementSeries {
	private static final long[] requiredMinutes = new long[] { 5, 15, 30, 45, 60, 120, 240, 1440 };

	public OnlineTimeAchievements() {
		super("Omnipresent", getAchievements());
	}

	private static List<Achievement> getAchievements() {
		List<Achievement> achievements = new ArrayList<Achievement>();
		for (int i = 0; i < requiredMinutes.length; i++) {
			Achievement achievement = new StatAchievement("Omnipresent " + RomanNumber.of(i), FarmStats.OFFLINE_TIME,
					requiredMinutes[i] * 60, Material.WATCH, (short) 0,
					"Be online for " + timeFromMinutes(requiredMinutes[i]) + " in a single game");
			achievements.add(achievement);
		}
		return achievements;
	}

	@Override
	public Upgrade getUpgrade(OfflinePlayer player) {
		return Upgrade.emptyUpgrade();
	}

	private static String timeFromMinutes(long totalMinutes) {
		long days = totalMinutes / 86400;
		long hours = (totalMinutes % 86400) / 3600;
		long minutes = ((totalMinutes % 86400) % 3600) / 60;

		String rs = "";
		if (days > 0)
			rs = rs + days + " days, ";
		if (hours > 0)
			rs = rs + hours + " hours, ";
		if (minutes > 0)
			rs = rs + minutes + " minutes, ";

		if (rs.equals(""))
			rs = "0 minutes";
		else
			rs = rs.substring(0, rs.length() - 2);

		return rs;
	}
}
