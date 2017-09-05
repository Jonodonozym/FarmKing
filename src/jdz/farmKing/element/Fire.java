/**
 * Fire.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 7:26:38 PM
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
 * The Fire alignment focuses on short-term growth, harvesting long grass / seeds and nether warts
 *
 * @author Jaiden Baker
 *
 */
class Fire extends Element{
	Fire(){
		super("Fire", "An unquenchable fire blazes within your soul. In your burning rage, you vow to destroy, "
				+ "to earn money at the destruction of plants instead of their growth. Boost the amount of income "
				+ "you earn from breaking grass.", Material.BLAZE_POWDER);

		upgrades[0] = Upgrade.fromConstant("Fists of Fury", "Boosts the money you earn from clicking by 1Sx",
				UpgradeType.CLICK_DIRECT, 1e18);
		
		upgrades[1] = Upgrade.fromStat("Taunting", "Multiplies your earnings per click based on the amount of plants you have",
				UpgradeType.CLICK_PERCENT, StatType.FARM_CROP_QUANTITY_TOTAL);
		
		upgrades[2] = Upgrade.fromConstant("Blazing Greed", "Increases your chance of finding elemental seeds by 10%",
				UpgradeType.SEED_DIRECT, 0.1);
		
		upgrades[3] = Upgrade.fromStat("Rage", "Multiplies your click reward based on the number of clicks you have made this reset",
				UpgradeType.CLICK_PERCENT, StatType.FARM_CLICKS).multiply(1/1000.0);

		upgrades[4] = Upgrade.fromStat("Greed Drive", "Multiples your income based on the amount of fire seeds found this reset",
				UpgradeType.ALL_CROPS_PERCENT, getSeedEarntStat()).power(0.5);

		upgrades[5] = Upgrade.fromStatCumulative("Fire Sprites", "Fire sprites join your workforce, increasing your workers based on your total clicks",
				UpgradeType.WORKER_COUNT_DIRECT, StatType.FARM_CLICKS).power(0.9).multiply(1/500.0);
		
		upgrades[6] = Upgrade.fromStat("Parasitic Warts", "Boosts your netherwart production based on the amount of clicks you've made this reset",
				UpgradeType.ALIGNMENT_CROP_PERCENT, StatType.FARM_CLICKS).multiply(1/666.00);
		
		upgrades[7] = Upgrade.fromStat("Parasitic Punch", "Inceases your click reward by your number of netherwarts",
				UpgradeType.CLICK_DIRECT, StatType.FARM_CROP_QUANTITY_15);
		
		upgrades[8] = Upgrade.fromConstant("Summon Demon", "Call forth a demon from hell to click 10 times per second for you",
				UpgradeType.AUTO_CLICKS, 10);

		powerShard = 
				Upgrade.fromConstant("Fire Shard", "Increase your seed find chance by 5% and boost your production based on fire seeds found this game",
						UpgradeType.SEED_DIRECT, 0.05)
				.addUpgrade(Upgrade.fromStat("", "", UpgradeType.ALL_CROPS_PERCENT,getSeedEarntStat()).multiply(1/1000.0));
	}

	private static final List<String> alignCrops = Arrays.asList(new String[]{"Netherwart"});
	
	@Override
	public List<String> getAlignmentCrops() {
		return alignCrops;
	}
	
	@Override public ChatColor getColor(){ return ChatColor.RED; }
}
