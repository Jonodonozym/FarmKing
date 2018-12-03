
package jdz.farmKing.element.data;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import jdz.farmKing.farm.data.FarmDBYML;
import lombok.Getter;

public class ElementDBYML implements ElementDB {
	@Getter private static final ElementDBYML instance = new ElementDBYML();

	@Override
	public void save(PlayerElementData data) {
		ConfigurationSection section = FarmDBYML.getInstance().getSection(data.getFarm());
		
		section.set("element", data.getElement().name);
		section.set("tiersBought", Arrays.asList(data.getTiersBought()));
		section.set("upgradesBought", Arrays.asList(data.getTiersBought()));
	}

	@Override
	public PlayerElementData load(OfflinePlayer player) {
		ConfigurationSection section = FarmDBYML.getInstance().getSection(player);
		
		// TODO Auto-generated method stub
		return null;
	}

}
