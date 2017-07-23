/**
 * Water.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 7:26:52 PM
 */

package farmKing.alignment;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import farmKing.crops.Farm;
import farmKing.upgrades.Upgrade;

/**
 * The Water alignment is yet to be complete it seems
 *
 * @author Jaiden Baker
 */
class Water extends Alignment{
	public Water(){
		super("Water", "An unquenchable fire blazes within your soul. In your burning rage, you vow to destroy, "
				+ "to earn money at the destruction of plants instead of their growth. Boost the amount of income "
				+ "you earn from breaking grass.", Material.WATER);

		upgrades[0] = new Upgrade("Irrigation", "Multiply the production of farmland crops by the number of farmland crops you have squared", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.MAIN_CROP_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow(getCropQuantity(f),2); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[1] = new Upgrade("Hydroponics", "Reduce the cost multiplier for farmland crops", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.MAIN_CROP_COST_MULTIPLIER; }
			@Override public double getBonus(int i, Farm f) { return 1; }
			@Override public boolean isDisplayable(int i){ return false; }
		};
		
		upgrades[2] = new Upgrade("Recycling", "Increase production of farmland crops based on the total number of crops you have", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.MAIN_CROP_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return Math.pow(f.numPlants/500.0, 0.7); }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[3] = new Upgrade("", "", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return 1e0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[4] = new Upgrade("", "", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return 1e0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[5] = new Upgrade("", "", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return 1e0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[6] = new Upgrade("", "", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return 1e0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[7] = new Upgrade("", "", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return 1e0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};
		
		upgrades[8] = new Upgrade("", "", 1) {			
			@Override public upgradeType getType(int i) { return upgradeType.CLICK_DIRECT; }
			@Override public double getBonus(int i, Farm f) { return 1e0; }
			@Override public boolean isDisplayable(int i){ return true; }
		};

		powerShard = new Upgrade("Water Shard", "Boost your production based on the number of farmland crops you have",1){
			@Override public upgradeType getType(int i) { return upgradeType.ALL_CROPS_PERCENT; }
			@Override public double getBonus(int i, Farm f) { return (int)Math.sqrt( getCropQuantity(f) / 400.0 ); }	
			@Override public boolean isDisplayable(int i){ return true; }		
		};
		
	}

	@Override
	public String[] getAlignmentCrops() {
		return new String[]{"Wheat","Carrot","Potato","Beetroot"};
	}
	
	private int getCropQuantity(Farm f){
		return f.crops[0].getQuantity() +
				  f.crops[1].getQuantity() +
				  f.crops[2].getQuantity() +
				  f.crops[3].getQuantity();
	}
	
	@Override public ChatColor getColor(){ return ChatColor.AQUA; }
}
