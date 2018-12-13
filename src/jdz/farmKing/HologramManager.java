
package jdz.farmKing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import jdz.bukkitUtils.events.Listener;

public class HologramManager implements Listener {
	private static Map<OfflinePlayer, Set<Hologram>> holograms = new HashMap<OfflinePlayer, Set<Hologram>>();
	private static Map<Hologram, OfflinePlayer> holoToPlayer = new HashMap<Hologram, OfflinePlayer>();

	public static Hologram make(OfflinePlayer player, Location loc) {
		Hologram holo = HologramsAPI.createHologram(FarmKing.getInstance(), loc);
		
		if (!holograms.containsKey(player))
			holograms.put(player, new HashSet<Hologram>());
		
		holograms.get(player).add(holo);
		holoToPlayer.put(holo, player);
		return holo;
	}
	
	public static void delete(Hologram hologram) {
		holograms.get(holoToPlayer.remove(hologram)).remove(hologram);
		hologram.delete();
	}
	
	@EventHandler
	public void onDisable(PluginDisableEvent event) {
		if (!event.getPlugin().equals(FarmKing.getInstance()))
			return;

		for (Set<Hologram> holos : holograms.values())
			for (Hologram h : holos)
				h.delete();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		holograms.put(event.getPlayer(), new HashSet<Hologram>());
	}
}
