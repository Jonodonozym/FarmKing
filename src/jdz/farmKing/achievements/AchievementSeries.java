/**
 * AchievementSeries.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 7, 2017 8:41:34 AM
 */

package jdz.farmKing.achievements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.upgrades.Upgrade;

/**
 * Another abstract Achievement type, instead for a series of achievements
 * wrapped into a single achievement icon
 *
 * @author Jaiden Baker
 */
public abstract class AchievementSeries extends Achievement {
	private List<Achievement> achievements = new ArrayList<Achievement>();
	private Map<OfflinePlayer, Integer> tiersGained;

	/**
	 * Basic constructor, also calls the initAchievements method and sets the
	 * achievements field to the result
	 * 
	 * @param icon
	 */
	public AchievementSeries(Material icon) {
		super(null, null, icon);
		this.achievements = initAchievements();
	}

	/**
	 * Checks all unachieved achievements in the series and increases the
	 * tiersGained field based on how many new ones are achieved
	 * 
	 * Only returns true once all achievements are obtained TODO
	 */
	@Override
	protected boolean checkAchieved(Farm f) {
		OfflinePlayer player = f.owner;
		while (tiersGained.get(player) < achievements.size()) {
			if (!achievements.get(tiersGained.get(player)).checkAchieved(f))
				return false;
			achievements.get(tiersGained.get(player)).doFirework(f);
			tiersGained.put(player, tiersGained.get(player) + 1);
		}
		return true;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public Achievement getCurrentAchievement(Player player) {
		return achievements
				.get(tiersGained.get(player) < achievements.size() ? tiersGained.get(player) : achievements.size() - 1);
	}

	// basic getters, each aimed at the latest achievement obtained
	// TODO add x/x to name / description, fix flag for when all tiers are
	// gained
	public String getName(Player player) {
		return achievements
				.get(tiersGained.get(player) < achievements.size() ? tiersGained.get(player) : achievements.size() - 1)
				.getName();
	}

	public String getDescription(Player player) {
		String name = tiersGained + "/" + achievements.size();
		return name + "\n" + achievements
				.get(tiersGained.get(player) < achievements.size() ? tiersGained.get(player) : achievements.size() - 1)
				.getDescription();
	}

	public Upgrade getUpgrade(Player player) {
		return achievements
				.get(tiersGained.get(player) < achievements.size() ? tiersGained.get(player) : achievements.size() - 1)
				.getUpgrade();
	}

	/**
	 * Abstract method for initializing the achievements in the series
	 * 
	 * @return the list of achievements
	 */
	public abstract List<Achievement> initAchievements();
}
