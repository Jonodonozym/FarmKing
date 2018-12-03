
package jdz.farmKing.element.data;

import org.bukkit.OfflinePlayer;

public interface ElementDB {
	public static ElementDB getInstance() {
		return ElementDBYML.getInstance();
	}

	public void save(PlayerElementData data);
	public PlayerElementData load(OfflinePlayer player);
}
