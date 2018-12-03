
package jdz.farmKing.element.data;

import org.bukkit.OfflinePlayer;

import jdz.farmKing.element.Element;
import jdz.farmKing.element.ElementMetaData;
import jdz.farmKing.farm.Farm;
import lombok.Data;

@Data
public class PlayerElementData {
	private final OfflinePlayer player;
	private final Farm farm;
	private Element element;
	private final boolean[] tiersBought = new boolean[ElementMetaData.numTiers];
	private final boolean[] upgradesBought = new boolean[ElementMetaData.numUpgrades];
	
	public boolean hasBoughtTier(int tier) {
		return tiersBought[tier];
	}

	public void setBoughtTier(int tier) {
		tiersBought[tier] = true;
	}
	
	public void setBoughtUpgrade(int index) {
		upgradesBought[index] = true;
	}
	
	public boolean hasBoughtUpgrade(int index) {
		return upgradesBought[index];
	}
}
