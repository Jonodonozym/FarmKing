
package jdz.farmKing.element;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import jdz.farmKing.upgrades.Upgrade;
import jdz.farmKing.upgrades.UpgradeParser;

public class ElementParser {

	public static Element parse(ConfigurationSection config, String name) {
		try {
			String description = config.getString("description");
			List<String> crops = config.getStringList("crops");
			Material icon = Material.valueOf(config.getString("icon").toUpperCase());
			ChatColor color = ChatColor.valueOf(config.getString("color").toUpperCase());

			List<Upgrade> upgrades = new ArrayList<Upgrade>();
			int i = 1;
			while (config.contains("Upgrade_" + i)) {
				upgrades.add(parseUpgrade(config.getConfigurationSection("Upgrade_" + i)));
				i++;
			}

			Upgrade powerShard = parseUpgrade(config.getConfigurationSection("PowerShard"));

			return new Element(name, description, icon, crops, color, upgrades, powerShard);
		}
		catch (Exception e) {
			Bukkit.getLogger().info("Error parsing Element " + name);
			Bukkit.getLogger().info("	" + e.toString());
			return null;
		}
	}

	private static Upgrade parseUpgrade(ConfigurationSection section) {
		String name = section.getString("name");
		String description = section.getString("description");
		List<String> expressions = section.getStringList("upgrades");

		try {
			return UpgradeParser.parse(name, description, expressions);
		}
		catch (Exception e) {
			Bukkit.getLogger().info("Error parsing upgrade \"" + section.getCurrentPath() + "\":");
			Bukkit.getLogger().info("	" + e.toString());
		}

		return Upgrade.emptyUpgrade();
	}

}
