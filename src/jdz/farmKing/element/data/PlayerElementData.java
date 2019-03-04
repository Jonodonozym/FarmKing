
package jdz.farmKing.element.data;

import org.bukkit.entity.Player;

import jdz.farmKing.element.Element;
import static jdz.farmKing.element.ElementMetaData.*;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class PlayerElementData {
	private final Player player;
	@Setter private Element element;
	private final Boolean[] tiersBought;
	private final Boolean[] upgradesBought;

	public PlayerElementData(Player player) {
		this(player, null, new Boolean[numTiers], new Boolean[upgradesPerTier * numTiers]);
	}

	public void setBoughtTier(int tier) {
		tiersBought[tier] = true;
	}

	public boolean hasBoughtTier(int tier) {
		return tiersBought[tier];
	}

	public void setBoughtUpgrade(int tier, int index) {
		upgradesBought[tier * upgradesPerTier + index] = true;
	}

	public boolean hasBoughtUpgrade(int tier, int index) {
		return upgradesBought[tier * upgradesPerTier + index];
	}
	
	public void reset() {
		element = null;
		Arrays.fill(tiersBought, false);
		Arrays.fill(upgradesBought, false);
	}
}
