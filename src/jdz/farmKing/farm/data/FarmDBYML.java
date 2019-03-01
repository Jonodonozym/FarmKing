
package jdz.farmKing.farm.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import jdz.farmKing.FarmKing;
import jdz.farmKing.farm.Farm;
import lombok.Getter;

public class FarmDBYML implements FarmDB {
	@Getter public static final FarmDBYML instance = new FarmDBYML();
	private YamlConfiguration config = YamlConfiguration.loadConfiguration(getConfigFile());

	private File getConfigFile() {
		if (!FarmKing.getInstance().getDataFolder().exists())
			FarmKing.getInstance().getDataFolder().mkdir();

		File file = new File(FarmKing.getInstance().getDataFolder(), "Farm Data.yml");
		if (!file.exists())
			try {
				file.createNewFile();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		return file;
	}

	public void saveFile(Farm farm) {
		Bukkit.getScheduler().runTaskAsynchronously(FarmKing.getInstance(), () -> {
			try {
				config.save(getConfigFile());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void save(Farm farm) {
		ConfigurationSection section = getSection(farm);
		section.set("x", farm.getOrigin().getBlockX());
		section.set("z", farm.getOrigin().getBlockZ());
		setOwner(farm, farm.getOwner());
		saveFile(farm);
	}

	@Override
	public void setOwner(Farm farm, OfflinePlayer owner) {
		ConfigurationSection section = getSection(farm);
		ConfigurationSection ownershipSection = getOwnershipSection();

		String oldOwner = section.getString("owner");
		if (oldOwner != null)
			ownershipSection.set(oldOwner, null);

		if (owner != null)
			section.set("owner", owner.getUniqueId());
		else
			section.set("owner", null);

		saveFile(farm);
	}

	@Override
	public boolean hasFarm(OfflinePlayer player) {
		return getOwnershipSection().contains(player.getUniqueId() + "");
	}

	@Override
	public Farm load(OfflinePlayer player) {
		ConfigurationSection config = getSection(player);
		int id = Integer.parseInt(config.getName());
		return new Farm(id, config.getInt("x"), config.getInt("z"), player);
	}

	@Override
	public List<Farm> loadUnownedFarms() {
		List<Farm> farms = new ArrayList<Farm>();

		for (String s : config.getKeys(false)) {
			if (!config.contains(s + "." + "owner"))
				continue;

			farms.add(new Farm(Integer.parseInt(s), config.getInt(s + "x"), config.getInt(s + "z"), false));
		}

		return farms;
	}

	public ConfigurationSection getSection(Farm farm) {
		if (!config.contains(farm.getId() + ""))
			config.createSection(farm.getId() + "");
		return config.getConfigurationSection(farm.getId() + "");
	}

	public ConfigurationSection getSection(OfflinePlayer player) {
		ConfigurationSection ownershipSection = getOwnershipSection();
		if (!ownershipSection.contains(player.getUniqueId() + ""))
			return null;

		int farmID = ownershipSection.getInt(player.getUniqueId() + "");
		return config.getConfigurationSection(farmID + "");
	}

	public ConfigurationSection getOwnershipSection() {
		if (!config.contains("ownership"))
			config.createSection("ownership");
		return config.getConfigurationSection("ownership");
	}
}
