package jdz.farmKing.achievements;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

import jdz.farmKing.achievements.achievements.CropPlantsAchievement;
import jdz.farmKing.crops.CropType;
import lombok.Getter;

public abstract class FarmAchievements {
	@Getter private static List<FarmAchievementSeries> allAchievements = new ArrayList<>();

	static {
		for (CropType type : CropType.values())
			allAchievements.add(new CropPlantsAchievement(type));
	}

	public static void registerAll(Plugin plugin) {
		for (FarmAchievementSeries series : allAchievements)
			series.register(plugin);
	}
}
