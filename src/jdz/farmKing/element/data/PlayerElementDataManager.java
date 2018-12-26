
package jdz.farmKing.element.data;

import java.util.Arrays;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.data.PlayerDataManager;
import jdz.farmKing.element.Element;
import jdz.farmKing.farm.data.FarmDBYML;
import lombok.Getter;

public class PlayerElementDataManager extends PlayerDataManager<PlayerElementData> {
	@Getter private static final PlayerElementDataManager instance = new PlayerElementDataManager();

	@Override
	protected PlayerElementData loadDefault(Player player) {
		return new PlayerElementData(player);
	}

	@Override
	protected PlayerElementData loadFromStorage(Player player) {
		ConfigurationSection section = FarmDBYML.getInstance().getSection(player);

		Element element = Element.valueOf(section.getString("element"));
		Boolean[] tiersBought = section.getBooleanList("tiersBought").toArray(new Boolean[0]);
		Boolean[] upgradesBought = section.getBooleanList("upgradesBought").toArray(new Boolean[0]);

		return new PlayerElementData(player, element, tiersBought, upgradesBought);
	}

	@Override
	protected void saveToStorage(Player player, PlayerElementData data) {
		ConfigurationSection section = FarmDBYML.getInstance().getSection(player);

		section.set("element", data.getElement().name);
		section.set("tiersBought", Arrays.asList(data.getTiersBought()));
		section.set("upgradesBought", Arrays.asList(data.getTiersBought()));
	}

	@Override
	protected boolean storageContains(Player player) {
		return FarmDBYML.getInstance().hasFarm(player);
	}
}
