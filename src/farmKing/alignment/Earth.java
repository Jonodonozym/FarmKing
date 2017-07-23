/**
 * Earth.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 7:23:43 PM
 */

package farmKing.alignment;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import farmKing.crops.Farm;
import farmKing.upgrades.Upgrade;

/**
 * The Earth alignment focuses on mushroom production and is directed towards a long-term game-play style
 * 
 * @author Jaiden Baker
 */
class Earth extends Alignment{
	private double log2 = Math.log(2);
	public Earth(){
		super("Earth", "The earth is eternal. It will outlast everything that lives for all eternity. "
				+ "Harness the limitless power of the planet to boost your farm's production. The earth's "
				+ "concept of time is vastly different from yours, so beware that it may be slow at first.", Material.STONE);

		upgrades[0] = new Upgrade("Sleeping Mountains", "Boosts offline production based on your online time this reset.", 1) {
			@Override public upgradeType getType(int i) { return upgradeType.ONLINE_PRODUCTION_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.log(f.onlineTimeMinutes + 1) / log2; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[1] = new Upgrade("Dark Roots", "Multiplies the production of mushrooms by 1000.", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.MAIN_CROP_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return 1000; }
			@Override public boolean isDisplayable(int i){ return false; }
		};
		
		upgrades[2] = new Upgrade("Golemcraft", "Gain 3 golem workers instantly, and gain more golems over time.", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_COUNT_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow(f.onlineTimeMinutes/30,0.8) + 3; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[3] = new Upgrade("Creeping Mycellium", "Boosts the production of mushrooms based on your offline time this reset.", 1) {
			@Override public upgradeType getType(int i) { return upgradeType.MAIN_CROP_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow(f.offlineTimeMinutes, 0.8)/60.0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[4] = new Upgrade("Deep Memory", "Boosts production based on your total offline time.", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.ALL_CROPS_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.log(f.offlineTimeMinutes/60.0 + 1) / log2; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[5] = new Upgrade("Timeless Luck", "Increases your chance of finding seeds based on your total playtime this game.", 1) {
			@Override public upgradeType getType(int i) { return upgradeType.SEED_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow((f.offlineTimeMinutes + f.offlineTimeMinutes)/60.0,0.75); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[6] = new Upgrade("Earth's Bounty", "Automatically gain 3 seeds per second, and earn more based on your online time this reset.", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.SEED_OVER_TIME; }
			@Override public double getBonus(int i, Farm f) { return Math.pow((f.onlineTimeMinutes)/60.0,1.1) + 3; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[7] = new Upgrade("Sporeling Army", "Boosts offline production based on your number of mushrooms.", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.ALL_CROPS_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return ((f.crops[8].getQuantity() + f.crops[9].getQuantity())/1000.0); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[8] = new Upgrade("Seed Force", "Boosts offline production based on the amount of Earth seeds found this game.", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.OFFLINE_PRODUCTION_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow((f.seedsEarnt[alignmentIndex])/1000.0, 0.9); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		powerShard = new Upgrade("Earth Shard", "Gain workers based on your longest game",1){
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_COUNT_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return (int)Math.sqrt(f.longestPlayTimeMinutes/60); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
	}

	@Override
	public String[] getAlignmentCrops() {
		return new String[]{"Red Mushroom","Brown Mushroom"};
	}
	
	@Override public ChatColor getColor(){ return ChatColor.GOLD; }
}
