/**
 * AchievementSeries.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 7, 2017 8:41:34 AM
 */

package farmKing.achievements;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import farmKing.crops.Farm;
import farmKing.upgrades.Upgrade;

/**
 * Another abstract Achievement type, instead for a series of achievements wrapped into a single
 * achievement icon
 *
 * @author Jaiden Baker
 */
public abstract class AchievementSeries extends Achievement{
	private List<Achievement> achievements = new ArrayList<Achievement>();
	private int tiersGained;
	
	/**
	 * Basic constructor, also calls the initAchievements method and sets the achievements field to the result
	 * @param icon
	 */
	public AchievementSeries(Material icon) {
		super(null, null, icon);
		this.achievements = initAchievements();
	}

	/**
	 * Checks all unachieved achievements in the series and increases the tiersGained field
	 * based on how many new ones are achieved
	 * 
	 * Only returns true once all achievements are obtained
	 * TODO 
	 */
	@Override
	protected boolean checkAchieved(Farm f) {
		while(tiersGained < achievements.size()){
			if (!achievements.get(tiersGained).checkAchieved(f))
				return false;
			tiersGained++;
		}
		return true;
	}
	
	// basic getters, each aimed at the latest achievement obtained
	// TODO add x/x to name / description, fix flag for when all tiers are gained
	@Override public String getName(){
		return achievements.get(tiersGained<achievements.size()?tiersGained:achievements.size()-1).getName();
		}
	@Override public String getDescription(){
		String name = tiersGained+"/"+achievements.size();
		return name+"\n"+achievements.get(tiersGained<achievements.size()?tiersGained:achievements.size()-1).getDescription(); }
	@Override public Upgrade getUpgrade(){
		return achievements.get(tiersGained<achievements.size()?tiersGained:achievements.size()-1).getUpgrade(); }
	
	/**
	 * Abstract method for initializing the achievements in the series
	 * @return the list of achievements
	 */
	public abstract List<Achievement> initAchievements();
}
