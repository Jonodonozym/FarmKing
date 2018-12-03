
package jdz.farmKing.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.world.StructureGrowEvent;

import jdz.bukkitUtils.events.custom.CropTrampleEvent;

public class WorldGuard {
	private static final World farmWorld = Bukkit.getWorlds().get(0);

	@EventHandler
	public void stopItemEntities(ItemSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void stopPlace(BlockPlaceEvent event) {
		if (event.getBlock().getWorld() == farmWorld)
			event.setCancelled(true);
	}

	@EventHandler
	public void stopSpread(BlockSpreadEvent event) {
		if (event.getBlock().getWorld() == farmWorld)
			event.setCancelled(true);
	}

	@EventHandler
	public void stopPhysics(BlockPhysicsEvent event) {
		if (event.getBlock().getWorld() == farmWorld)
			if (event.getBlock().getType() == Material.CACTUS
					|| event.getBlock().getType() == Material.SUGAR_CANE_BLOCK)
				event.setCancelled(true);
	}

	@EventHandler
	public void stopGrowth(BlockCanBuildEvent event) {
		if (event.getBlock().getWorld() == farmWorld)
			if (event.getMaterial() == Material.CACTUS || event.getMaterial() == Material.SUGAR_CANE_BLOCK)
				event.setBuildable(true);
	}

	@EventHandler
	public void stopPlantGrowth(BlockGrowEvent event) {
		if (event.getBlock().getWorld() == farmWorld)
			event.setCancelled(true);
	}

	@EventHandler
	public void stopTreeGrowth(StructureGrowEvent event) {
		if (event.getWorld() == farmWorld)
			event.setCancelled(true);
	}

	@EventHandler
	public void stopBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getWorld() == farmWorld)
			event.setCancelled(true);
	}

	@EventHandler
	public void stopCropTrample(CropTrampleEvent event) {
		if (event.getBlock().getWorld() == farmWorld)
			event.setCancelled(true);
	}
}
