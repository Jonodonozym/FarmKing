/**
 * Air.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 7:21:55 PM
 */

package jdz.farmKing.element;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import jdz.farmKing.farm.StatType;
import jdz.farmKing.upgrades.Upgrade;
import jdz.farmKing.upgrades.UpgradeType;

/**
 * The Air alignment focuses on production from saplings and workers with a balanced gameplay focus.
 * @author Jaiden Baker
 */
class Air extends Element{
	Air(){
		super("Air", "Air is the essence of life; all living things need it. By harnessing it, the weather "
				+ "systems will be at your beck and call. Rain or shine, hot or cold, that is for you to decide. "
				+ "Upgrades focus on production from saplings and workers.", Material.GLASS);

		
		upgrades[0] = Upgrade.fromStat("Breath of life", "Your crops feed your workforce, giving you workers based on "
				+ "the amount of plants you have", UpgradeType.WORKER_COUNT_DIRECT, StatType.FARM_CROP_QUANTITY_TOTAL).multiply(1/1200.0);
		
		upgrades[1] = Upgrade.fromConstant("Energized workforce", "Workers produce twice as many seeds.",
				UpgradeType.WORKER_SEED_PERCENT, 2);
		
		upgrades[2] = Upgrade.fromStat("Encouragement", "Worker production is increased based on the amount of plants you have",
				UpgradeType.WORKER_PRODUCTION_PERCENT, StatType.FARM_CROP_QUANTITY_TOTAL).multiply(1/1200.0);
		
		upgrades[3] = Upgrade.fromConstant("Wind Sprites", "Wind Sprites join your workforce, doubling the amount of workers you have",
				UpgradeType.WORKER_COUNT_PERCENT, 2);
		
		upgrades[4] = Upgrade.fromStat("Delicate Touch", "Boosts sapling production based on the amount of workers you have",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_WORKERS).multiply(1/10.0).power(0.8);

		upgrades[5] = Upgrade.fromStat("Call of the Wind", "Gain workers based on the amount of air seeds you've found this reset",
				UpgradeType.WORKER_COUNT_DIRECT, getSeedEarntStat()).power(0.7);

		upgrades[6] = Upgrade.fromStat("Experienced Workers", "Worker production is boosted based on online time this game",
				UpgradeType.WORKER_PRODUCTION_PERCENT, StatType.FARM_ONLINE_TIME).multiply(1/5.0).power(0.9);
		
		upgrades[7] = Upgrade.fromStat("Synergy", "Workers gain more seeds based on how many workers you have",
				UpgradeType.WORKER_SEED_PERCENT, StatType.FARM_WORKERS).power(0.8).multiply(1/100.0);
		
		upgrades[8] = Upgrade.fromStat("Tree of life", "Increase the production of Saplings based on how many workers you have",
				UpgradeType.MAIN_CROP_COST_MULTIPLIER, StatType.FARM_WORKERS).power(0.75).multiply(2);
		
		powerShard = Upgrade.fromStatMax("Air Shard", "Boost production based on the maximum number of workers you've had",
				UpgradeType.ALL_CROPS_PERCENT, StatType.FARM_WORKERS).multiply(1/10.0).power(0.5);
	}

	private static final List<String> alignCrops = Arrays.asList(new String[]{"Sapling"});
	
	@Override
	public List<String> getAlignmentCrops() {
		return alignCrops;
	}
	
	@Override public ChatColor getColor(){ return ChatColor.GREEN; }
}
