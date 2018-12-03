
package jdz.farmKing.farm.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.gen.FarmBuffer;

public class PlayerFarms {
	private static final Map<Player, Farm> playerToFarm = new HashMap<Player, Farm>();

	public static boolean hasFarm(Player player) {
		return playerToFarm.containsKey(player);
	}

	public static Farm get(Player player) {
		if (!hasFarm(player)) {
			Farm farm = FarmDB.getInstance().hasFarm(player) ? FarmDB.getInstance().load(player)
					: FarmBuffer.removeFirst();
			farm.setOwner(player);
			playerToFarm.put(player, farm);
		}
		return playerToFarm.get(player);
	}

	public static Collection<Farm> getPlayerFarms() {
		return playerToFarm.values();
	}

	public static void respawnTallGrass() {
		for (Farm farm : getPlayerFarms())
			farm.getGrass().respawn();
	}
}
