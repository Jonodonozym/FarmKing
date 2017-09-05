/**
 * Alignment.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 7:22:04 PM
 */


package jdz.farmKing.element;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import jdz.farmKing.farm.StatType;
import jdz.farmKing.upgrades.Upgrade;

/**
 * Abstract class for Alignments. There are 4 default alignments, and if you want to add more alignments then copy
 * one of the existing alignments, modify it as you see fit and add it to the 'align' array.
 * 
 * Each alignment has a name, description, icon, index, 3 upgrade tiers with 3 upgrades each and a 'power shard' upgrade.
 * The power shard upgrade is unlocked after buying all the upgrades, and once unlocked can be re-purchased
 * regardless of alignment
 * 
 * Extending classes (Alignments) are pretty much just complex data structures and don't need any extra methods
 * 
 * @author Jonodonozym
 */
public abstract class Element {
	
	// static constants
	public static final int[] tierCost = new int[]{20,100,500};
	public static final double[] upgradeCost = new double[]{20,100,500};
	public static final int numTiers = 3;
	public static final int upgradesPerTier = 3;
	public static final int numUpgrades = numTiers * upgradesPerTier;
	
	// static array of Alignment constants
	public static final Element[] align = new Element[]{
			new Earth(),
			new Air(),
			new Fire(),
			new Water()
	};
	
	// static variable for a UID
	private static int AlignmentUID = 0;

	// instance variables and methods
	public final String name;
	public final String description;
	public final Material icon;
	public final int alignmentIndex;
	protected Upgrade[] upgrades = new Upgrade[numUpgrades];
	protected Upgrade powerShard;
	
	/**
	 * Self-explanatory constructor for the Alignment abstract class
	 * @param name
	 * @param description
	 * @param icon
	 */
	public Element(String name, String description, Material icon){
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.alignmentIndex = AlignmentUID++;
	}
	
	// getters
	public Upgrade[] getUpgrades() { return upgrades; }
	public Upgrade getUpgrade(int i) { return upgrades[i]; }
	public Upgrade getPowerShard() { return powerShard; }
	
	// abstract methods
	public abstract List<String> getAlignmentCrops();
	public abstract ChatColor getColor();

	public StatType getSeedEarntStat(){
		return StatType.valueOf("FARM_SEEDS_"+alignmentIndex+"_EARNT");
	}
	public StatType getSeedStat(){
		return StatType.valueOf("FARM_SEEDS_"+alignmentIndex);
	}
}