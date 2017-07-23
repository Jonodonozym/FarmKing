/**
 * Fire.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 7:26:38 PM
 */

package farmKing.alignment;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import farmKing.crops.Farm;
import farmKing.upgrades.Upgrade;

/**
 * The Fire alignment focuses on short-term growth, harvesting long grass / seeds and nether warts
 *
 * @author Jaiden Baker
 *
 */
class Fire extends Alignment{
	public Fire(){
		super("Fire", "An unquenchable fire blazes within your soul. In your burning rage, you vow to destroy, "
				+ "to earn money at the destruction of plants instead of their growth. Boost the amount of income "
				+ "you earn from breaking grass.", Material.BLAZE_POWDER);

		upgrades[0] = new Upgrade("Fists of Fury", "Boosts the money you earn from clicking by 1Sx", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return 1e18; }
			@Override public boolean isDisplayable(int i){ return false; }
		};
		
		upgrades[1] = new Upgrade("Taunting", "Multiplies your earnings per click based on the amount of plants you have", 1) {
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return f.numPlants / 5000.0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[2] = new Upgrade("Blazing Greed", "Increases your chance of finding elemental seeds by 10%", 1) {
			@Override public upgradeType getType(int i) { return upgradeType.SEED_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return 0.1; }
			@Override public boolean isDisplayable(int i){ return false; }
		};
		
		upgrades[3] = new Upgrade("Rage", "Multiplies your click reward based on the number of clicks you have made this reset", 1) {
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return f.clicks / 1000.0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};

		upgrades[4] = new Upgrade("Greed Drive", "Multiples your income based on the amount of fire seeds found this reset", 1) {
			@Override public upgradeType getType(int i) { return upgradeType.ALL_CROPS_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.sqrt(f.seedsEarnt[2]); }
			@Override public boolean isDisplayable(int i){ return true; }
		};

		upgrades[5] = new Upgrade("Fire Sprites", "Fire sprites join your workforce, increasing your workers based on your total clicks", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.WORKER_COUNT_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow(f.totalClicks,0.9) / 500.0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};

		upgrades[6] = new Upgrade("Parasitic Warts", "Boosts your netherwart production based on the amount of clicks you've made this reset", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.MAIN_CROP_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return f.clicks / 666.00; }
			@Override public boolean isDisplayable(int i){ return true; }
		};

		upgrades[7] = new Upgrade("Parasitic Punch", "Inceases your click reward by the production of your netherwarts", 1) {
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_DIRECT; }
			@Override public double getBonus(int i, Farm f) {
				return f.crops[15].getType().baseIncome *
						f.crops[15].getQuantity() *
						f.globalIncomeMultiplier;
				}
			@Override public boolean isDisplayable(int i){ return true; }
		};

		upgrades[8] = new Upgrade("Summon Demon", "Call forth a demon from hell to click 10 times per second for you", 1) {
			@Override public upgradeType getType(int i) { return upgradeType.AUTO_CLICKS; }
			@Override public double getBonus(int i, Farm f) { return 10; }
			@Override public boolean isDisplayable(int i){ return false; }
		};
		
		powerShard = new Upgrade("Fire Shard", "Increase your seed find chance by 5% and boost your production based on fire seeds found this game",2){
			@Override public upgradeType getType(int i) {
				if (i == 0) return upgradeType.SEED_DIRECT; 
				return upgradeType.ALL_CROPS_PERCENT;
				}
			
			@Override public double getBonus(int i, Farm f) {
				if (i == 0) return 0.05;
				return Math.sqrt(f.seedsEarnt[alignmentIndex]/1000);
				}

			@Override public boolean isDisplayable(int i){ return (i == 1); }
		};
	}

	@Override
	public String[] getAlignmentCrops() {
		return new String[]{"Netherwart"};
	}
	
	@Override public ChatColor getColor(){ return ChatColor.RED; }
}
