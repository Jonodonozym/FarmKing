package jdz.farmKing.event;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.jonodonozym.UPEconomy.UPEconomyAPI;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.FarmData;
import jdz.farmKing.main.Main;

/**
 * Listener that prevents blocks from breaking or decaying
 * Also listens for tall grass being broken and applies the income from that
 * @author Jonodonozym
 * @version 1.0 implemented the listener
 */
public class BlockBreak implements Listener{
	private final World farmWorld;
	
	public BlockBreak(){
		this.farmWorld = Main.plugin.getServer().getWorlds().get(0);
	}

	@EventHandler
	public void blockSpread(BlockSpreadEvent event){
		if (event.getBlock().getWorld() == farmWorld)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void blockPhysics(BlockPhysicsEvent event){
		if (event.getBlock().getType() == Material.CACTUS || event.getBlock().getType() == Material.SUGAR_CANE_BLOCK)
			event.setCancelled(true);
	}

	@EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent event) {
		if (event.getMaterial() == Material.CACTUS || event.getMaterial() == Material.SUGAR_CANE_BLOCK)
			event.setBuildable(true);
    }
	
	/**
	 * Disables breaking blocks other than tall grass from one's own farm while in the farm world.
	 * When tall grass is broken, runs the farm click method, creates a hologram and sets the grass to respawn later
	 * @param event
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Farm farm = FarmData.playerToFarm.get(player.getName());
		Block block = event.getBlock();
		Material material = block.getType();
		
		if (material == Material.SUGAR_CANE_BLOCK || material == Material.CACTUS){
			event.setCancelled(true);
			return;
		}
		
		//  handles breaking grass
		if (material.equals(Material.LONG_GRASS)) {
			Location l = block.getLocation();
			if (farm != null && farm.isIn(l)){
				
				farm.doManualClick(1);
				
				//  sets the grass to respawn later
				new BukkitRunnable(){
					@SuppressWarnings("deprecation")
					@Override
					public void run(){
						block.setType(Material.LONG_GRASS);
						block.setData((byte)1);
					}
				}.runTaskLater(Main.plugin, Farm.GRASS_RESPAWN_TIMER);
				
				//  creates the money bonus hologram
				Hologram temp = HologramsAPI.createHologram(Main.plugin,l.add(0.5,1+Math.random()/2.0,0.5));
				temp.appendTextLine(ChatColor.BOLD+""+ChatColor.YELLOW+"$"+UPEconomyAPI.charFormat(farm.grassValue, 4));
				new BukkitRunnable(){
					@Override
					public void run(){
						temp.delete();
					}
				}.runTaskLater(Main.plugin, 10);
				
			}
			else {
				player.sendMessage(ChatColor.RED+"You can only harvest grass from your own farm!");
				event.setCancelled(true);
			}
		}
		else
			event.setCancelled(true);
	}
	
	@EventHandler
    public void onFrameBrake(HangingBreakEvent e) {
        e.setCancelled(true);
    }
}
