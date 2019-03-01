
package jdz.farmKing.farm.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.generation.FarmBuffer;

public class PlayerFarms {
	private static final Map<Player, Farm> playerToFarm = new HashMap<Player, Farm>();
	private static final Map<Integer, Farm> idToFarm = new HashMap<Integer, Farm>();

	public static boolean hasFarm(Player player) {
		return playerToFarm.containsKey(player);
	}

	public static Farm get(OfflinePlayer player) {
		if (!player.isOnline())
			return FarmDB.getInstance().load(player);

		if (!hasFarm(player.getPlayer())) {
			Farm farm = FarmDB.getInstance().hasFarm(player) ? FarmDB.getInstance().load(player)
					: FarmBuffer.removeFirst();
			farm.setOwner(player);
			playerToFarm.put(player.getPlayer(), farm);
			idToFarm.put(farm.getId(), farm);
		}
		return playerToFarm.get(player);
	}

	public static Farm getById(int id) {
		if (idToFarm.containsKey(id))
			return idToFarm.get(id);
		return null;
	}

	public static Collection<Farm> getPlayerFarms() {
		return playerToFarm.values();
	}

	public static void respawnTallGrass() {
		for (Farm farm : getPlayerFarms())
			farm.getGrass().respawn();
	}
}
