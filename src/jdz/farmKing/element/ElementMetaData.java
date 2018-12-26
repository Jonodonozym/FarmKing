
package jdz.farmKing.element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jdz.bukkitUtils.fileIO.FileExporter;
import jdz.farmKing.FarmKing;
import lombok.Getter;

public class ElementMetaData {
	public static final int numTiers = 3, upgradesPerTier = 3;
	public static final int numUpgrades = numTiers * upgradesPerTier;
	
	@Getter private static final List<Element> elements = new ArrayList<>();

	private static List<Double> tierCost;
	private static List<Double> upgradeCost;
	@Getter private static double powerShardCost;
	
	public static double getTierCost(int tier) {
		return tierCost.get(tier);
	}

	public static double getUpgradeCost(int tier, int upgrade) {
		return upgradeCost.get(tier * upgradesPerTier + upgrade);
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
			elements.add(ElementParser.parse(section, key));
		}
	}
}
