/**
 * Air.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 7:21:55 PM
 */

package farmKing.alignment;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import farmKing.crops.Farm;
import farmKing.upgrades.Upgrade;

/**
 * The Air alignment focuses on production from saplings and workers with a balanced gameplay focus.
 * @author Jaiden Baker
 */
class Air extends Alignment{
	public Air(){
		super("Air", "Air is the essence of life; all living things need it. By harnessing it, the weather "
				+ "systems will be at your beck and call. Rain or shine, hot or cold, that is for you to decide. "
				+ "Upgrades focus on production from saplings and workers.", Material.GLASS);

		upgrades[0] = new Upgrade("Breath of life", "Your crops feed your workforce, giving you "
				+ "workers based on the amount of plants you have", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_COUNT_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return f.numPlants / 1200.0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[1] = new Upgrade("Energized workforce", "Workers produce twice as many seeds.", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_SEED_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return 2; }
			@Override public boolean isDisplayable(int i){ return false; }
		};
		
		upgrades[2] = new Upgrade("Encouragement", "Worker production is increased based on the amount of plants you have", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_PRODUCTION_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return f.numPlants / 1200.0 + 1; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[3] = new Upgrade("Wind Sprites", "Wind Sprites join your workforce, doubling the amount of workers you have", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_COUNT_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return 2; }
			@Override public boolean isDisplayable(int i){ return false; }
		};
		
		upgrades[4] = new Upgrade("Delicate Touch", "Boosts sapling production based on the amount of workers you have", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.MAIN_CROP_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow(f.workers/10,0.8); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[5] = new Upgrade("Call of the Wind", "Gain workers based on the amount of air seeds you've found this reset", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_COUNT_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return (int)Math.pow(f.seedsEarnt[alignmentIndex], 0.7); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[6] = new Upgrade("Experienced Workers", "Worker production is boosted based on online time this game", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_PRODUCTION_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow(f.onlineTimeMinutes/5,0.9); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[7] = new Upgrade("Synergy", "Workers gain more seeds based on how many workers you have", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_SEED_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow(f.workers, 0.8)/100; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[8] = new Upgrade("Tree of life", "Increase the production of Saplings based on how many workers you have", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.MAIN_CROP_COST_MULTIPLIER; }
			@Override public double getBonus(int i, Farm f) { return Math.pow(f.workers, 0.75)*2; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		powerShard = new Upgrade("Air Shard", "Boost production based on the maximum number of workers you've had",1){
			@Override public upgradeType getType(int i) { return upgradeType.ALL_CROPS_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.sqrt(f.maxWorkers/10); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
	}

	@Override
	public String[] getAlignmentCrops() {
		return new String[]{"Sapling"};
	}
	
	@Override public ChatColor getColor(){ return ChatColor.GREEN; }
}
