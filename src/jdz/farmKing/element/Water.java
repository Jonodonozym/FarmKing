/**
 * Water.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 7:26:52 PM
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
 * The Water alignment is yet to be complete it seems
 *
 * @author Jaiden Baker
 */
class Water extends Element{
	Water(){
		super("Water", "An unquenchable fire blazes within your soul. In your burning rage, you vow to destroy, "
				+ "to earn money at the destruction of plants instead of their growth. Boost the amount of income "
				+ "you earn from breaking grass.", Material.STATIONARY_WATER);

		upgrades[0] = Upgrade.fromStat("Irrigation", "Multiply the production of farmland crops by the number of farmland crops you have squared",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_CROP_QUANTITY_ALIGNMENT).power(2);
		
		upgrades[1] = Upgrade.fromConstant("Hydroponics", "Reduce the cost multiplier for farmland crops", UpgradeType.MAIN_CROP_COST_MULTIPLIER, 1);
		
		upgrades[2] = Upgrade.fromStat("Recycling", "Increase production of farmland crops based on the total number of crops you have",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_CROP_QUANTITY_TOTAL).multiply(1/500.0).power(0.7);

		
		// TODO
		upgrades[3] = Upgrade.fromStat("", "",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_CROP_QUANTITY_TOTAL);

		upgrades[4] = Upgrade.fromStat("", "",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_CROP_QUANTITY_TOTAL);

		upgrades[5] = Upgrade.fromStat("", "",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_CROP_QUANTITY_TOTAL);

		upgrades[6] = Upgrade.fromStat("", "",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_CROP_QUANTITY_TOTAL);

		upgrades[7] = Upgrade.fromStat("", "",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_CROP_QUANTITY_TOTAL);

		upgrades[8] = Upgrade.fromStat("", "",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_CROP_QUANTITY_TOTAL);
		
		powerShard = Upgrade.fromStat("Water Shard", "Boost your production based on the number of farmland crops you have",
				UpgradeType.ALL_CROPS_PERCENT, StatType.FARM_CROP_QUANTITY_ALIGNMENT).multiply(1/400.0).power(0.5);
	}

	private static final List<String> alignCrops = Arrays.asList(new String[]{"Wheat","Carrot","Potato","Beetroot"});
	
	@Override
	public List<String> getAlignmentCrops() {
		return alignCrops;
	}
	
	@Override public ChatColor getColor(){ return ChatColor.AQUA; }
}
