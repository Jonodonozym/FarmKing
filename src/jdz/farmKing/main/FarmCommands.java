package jdz.farmKing.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jonodonozym.UPEconomy.UPEconomyAPI;

import jdz.farmKing.element.ElementUpgradeInventory;
import jdz.farmKing.farm.EventFlags;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.FarmData;
import net.md_5.bungee.api.ChatColor;


/**
 * Command executor for the plugin
 * @author Jonodonozym
 * @version 1.0 implemented the FarmCommands
 */
public class FarmCommands implements CommandExecutor {
	private int gemResetTry = 0;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//player commands
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Farm farm = FarmData.playerToFarm.get(player.getName());
			if (args.length > 0)
			switch (args[0].toLowerCase()){
			case "go":
				//assigns a new farm to the player if they don't already have one
				if (!FarmData.playerToFarm.keySet().contains(player.getName())){
					FarmData.playerToFarm.put(player.getName(), FarmData.emptyFarms.remove(FarmData.emptyFarms.size()-1));
					FarmData.playerToFarm.get(player.getName()).crops[0].updatePrice();
					FarmData.playerToFarm.get(player.getName()).crops[0].updateHologram();
					if (FarmData.emptyFarms.size() < FarmData.MIN_FARM_BUFFER)
						FarmData.generateFarm(FarmData.currentFarm++);
				}

				// teleports the player to the farm's spawn location
				farm = FarmData.playerToFarm.get(player.getName());
				farm.owner = player;
				player.teleport(farm.spawn);
				player.sendMessage(ChatColor.GREEN+"Sending you to your farm.");
				break;
			case "gemreset":
			case "gm":
			case "reset":
				if (farm == null){ player.sendMessage(ChatColor.RED+"You need a farm to do that!"); break; }
				if (gemResetTry == 0){
					gemResetTry = 1;
					player.sendMessage(ChatColor.YELLOW+"Are you sure you want to do that? You will gain "
					+UPEconomyAPI.charFormat(farm.gemResetAmount, 4)+" gems by resetting.");

					new BukkitRunnable(){ @Override public void run() { gemResetTry = 0; } }.runTaskLater(Main.plugin, 100);
					
				}
				else {
					farm.gemReset();
					gemResetTry = 0;
				}
				break;
				
			case "rankup":
			case "levelup":
				player.sendMessage(ChatColor.RED+"Leveling up is not implemented yet, sorry!");
				break;

			case "alignment":
			case "alignments":
			case "a":
				if (farm == null)
					player.sendMessage(ChatColor.RED+"You need a farm to do that!");
				else if (!farm.eventIsComplete.get(EventFlags.ALIGNMENTS_UNLOCKED))
					player.sendMessage(ChatColor.RED+"You need 2B gems to do that!");
				else if (farm.elementInventory == null) 
					player.openInventory(ElementUpgradeInventory.alignSelectInventory);
				else 
					player.openInventory(farm.elementInventory.inventory);
				break;
				
			default: return false;
			}
		}
		
		//server commands
		else if (args.length > 0)
			switch (args[0]) {
			case "purge": 
			default: return false;
			}
		
		return true;
	}
}