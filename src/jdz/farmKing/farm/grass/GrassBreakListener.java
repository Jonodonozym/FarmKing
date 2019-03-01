
package jdz.farmKing.farm.grass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import jdz.bukkitUtils.events.Listener;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.PlayerFarms;

public class GrassBreakListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Farm farm = PlayerFarms.get(player);

		Block block = event.getBlock();

		if (block.getType() != Material.LONG_GRASS)
			return;

		if (farm == null || farm.isIn(block.getLocation())) {
			player.sendMessage(ChatColor.RED + "You can only harvest grass from your own farm!");
			return;
		}

		event.setDropItems(false);
		event.setCancelled(false);

		GrassBreakEvent newEvent = new GrassBreakEvent(farm, block, player);
		newEvent.call();
		if (!newEvent.isCancelled())
			farm.getGrass().onBreak(newEvent);
	}
}
