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

import jdz.farmKing.upgrades.Upgrade;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class for Alignments. There are 4 default alignments, and if you want to add
 * more simply edit the elements.yml file
 * 
 * Each alignment has a name, description, icon, index, 3 upgrade tiers with 3
 * upgrades each and a 'power shard' upgrade. The power shard upgrade is
 * unlocked after buying all the upgrades, and once unlocked can be re-purchased
 * regardless of alignment
 * 
 * @author Jonodonozym
 */
@AllArgsConstructor
public class Element {
	public static List<Element> values() {
		return ElementMetaData.getElements();
	}

	public static Element valueOf(String string) {
		for (Element element : values())
			if (element.name.equals(string))
				return element;
		return null;
	}

	public final String name;
	public final String description;
	public final Material icon;
	public final List<String> crops;
	public final ChatColor color;

	@Getter public final List<Upgrade> upgrades;
	@Getter public final Upgrade powerShard;

	public Upgrade getUpgrade(int tier, int index) {
		return upgrades.get(tier * ElementMetaData.upgradesPerTier + index);
	}
	
	public boolean hasCrop(Material material) {
		return crops.contains(material.toString());
	}
}