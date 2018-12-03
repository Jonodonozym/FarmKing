
package jdz.farmKing.element;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jdz.bukkitUtils.fileIO.FileExporter;
import jdz.farmKing.FarmKing;

public class ElementMetaData {
	public static final int numTiers = 3, upgradesPerTier = 3;
	public static final int numUpgrades = numTiers * upgradesPerTier;

	public static List<Double> tierCost;
	public static List<Double> upgradeCost;
	public static double powerShardCost;

	private static final Map<String, Element> elements = new HashMap<String, Element>();;

	public static Collection<Element> values() {
		return elements.values();
	}

	public static void load(FarmKing plugin) {
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdir();

		String location = plugin.getDataFolder().getPath() + File.separator + "elements.yml";
		File elementFile = new File(location);
		if (!elementFile.exists())
			new FileExporter(plugin).ExportResource("/elements.yml", location);

		FileConfiguration config = YamlConfiguration.loadConfiguration(elementFile);

		elements.clear();

		tierCost = config.getDoubleList("MetaData.tierSeedCosts");
		upgradeCost = config.getDoubleList("MetaData.upgradeCosts");

		for (String key : config.getKeys(false)) {
			if (key.equals("MetaData"))
				continue;

			ConfigurationSection section = config.getConfigurationSection(key);
			elements.put(key, ElementParser.parse(section, key));
		}
	}

}
