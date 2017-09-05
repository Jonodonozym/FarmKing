/**
 * Earth.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 7:23:43 PM
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
 * The Earth alignment focuses on mushroom production and is directed towards a long-term game-play style
 * 
 * @author Jaiden Baker
 */
class Earth extends Element{
	private double log2 = Math.log(2);
	Earth(){
		super("Earth", "The earth is eternal. It will outlast everything that lives for all eternity. "
				+ "Harness the limitless power of the planet to boost your farm's production. The earth's "
				+ "concept of time is vastly different from yours, so beware that it may be slow at first.", Material.STONE);

		upgrades[0] = Upgrade.fromStat("Sleeping Mountains", "Boosts offline production based on your online time this reset.",
				UpgradeType.ONLINE_PRODUCTION_PERCENT, StatType.FARM_OFFLINE_TIME).add(1).log().multiply(1/log2);
		
		upgrades[1] = Upgrade.fromConstant("Dark Roots", "Multiplies the production of mushrooms by 1000.",
				UpgradeType.ALIGNMENT_CROP_PERCENT, 1000);
		
		upgrades[2] = Upgrade.fromStat("Golemcraft", "Gain 3 golem workers instantly, and gain more golems over time.",
				UpgradeType.WORKER_COUNT_DIRECT, StatType.FARM_PLAY_TIME).multiply(1/30.0).power(0.8).add(3);
		
		upgrades[3] = Upgrade.fromStat("Creeping Mycellium", "Boosts the production of mushrooms based on your offline time this reset.",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_OFFLINE_TIME).power(0.8).multiply(1/60.0);
		
		upgrades[4] = Upgrade.fromStat("Deep Memory", "Boosts production based on your total offline time.",
				UpgradeType.ALL_CROPS_PERCENT, StatType.FARM_OFFLINE_TIME).multiply(1/60.0).add(1).log().multiply(1/log2);
		
		upgrades[5] = Upgrade.fromStat("Timeless Luck", "Increases your chance of finding seeds based on your total playtime this game.",
				UpgradeType.SEED_DIRECT, StatType.FARM_PLAY_TIME).multiply(1/60.0).power(0.75);
		
		upgrades[6] = Upgrade.fromStat("Earth's Bounty", "Automatically gain 3 seeds per second, and earn more based on your online time this reset.",
				UpgradeType.SEED_OVER_TIME, StatType.FARM_ONLINE_TIME).multiply(1/60.0).power(1.1).add(3);
		
		upgrades[7] = Upgrade.fromStat("Sporeling Army", "Boosts offline production based on your number of mushrooms.",
				UpgradeType.OFFLINE_PRODUCTION_PERCENT, StatType.FARM_CROP_QUANTITY_ALIGNMENT).multiply(1/1000.0);
		
		upgrades[8] = Upgrade.fromStat("Seed Force", "Boosts offline production based on the amount of Earth seeds found this game.",
				UpgradeType.OFFLINE_PRODUCTION_PERCENT, getSeedEarntStat()).multiply(1/1000.0).power(0.9);
		
		powerShard = Upgrade.fromStatMax("Earth Shard", "Gain workers based on your longest game",
				UpgradeType.WORKER_COUNT_DIRECT, StatType.FARM_PLAY_TIME).multiply(1/60.0).power(0.5);
	}

	private static final List<String> alignCrops = Arrays.asList(new String[]{"Red Mushroom","Brown Mushroom"});
	
	@Override
	public List<String> getAlignmentCrops() {
		return alignCrops;
	}
	
	@Override public ChatColor getColor(){ return ChatColor.GOLD; }
}
