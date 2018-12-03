
package jdz.farmKing.farm.data;

import java.util.List;

import org.bukkit.OfflinePlayer;

import jdz.farmKing.farm.Farm;

public interface FarmDB {
	public static FarmDB getInstance() {
		return FarmDBYML.getInstance();
	}

	public void save(Farm farm);

	public void setOwner(Farm farm, OfflinePlayer player);

	public boolean hasFarm(OfflinePlayer player);

	public Farm load(OfflinePlayer player);

	public List<Farm> loadUnownedFarms();
}
