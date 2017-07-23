package farmKing.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jonodonozym.UPEconomy.UPEconomyAPI;

import farmKing.alignment.AlignmentUpgradeInventory;
import farmKing.crops.Farm;
import net.md_5.bungee.api.ChatColor;


/**
 * Command executor for the plugin
 * @author Jonodonozym
 * @version 1.0 implemented the FarmCommands
 */
public class FarmCommands implements CommandExecutor {
	private int gemResetTry = 0;
	private final Main plugin;

	public FarmCommands(Main plugin) {
		this.plugin = plugin; // Store the plug-in in situations where you need it.
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//player commands
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Farm farm = Main.playerToFarm.get(player.getName());
			if (args.length > 0)
			switch (args[0].toLowerCase()){
			case "go":
				//assigns a new farm to the player if they don't already have one
				if (!Main.playerToFarm.keySet().contains(player.getName())){
					Main.playerToFarm.put(player.getName(), plugin.emptyFarms.remove(plugin.emptyFarms.size()-1));
					Main.playerToFarm.get(player.getName()).crops[0].updatePrice();
					Main.playerToFarm.get(player.getName()).crops[0].updateHologram();
					if (plugin.emptyFarms.size() < plugin.MIN_FARM_BUFFER)
						plugin.generateFarm(plugin.currentFarm++);
				}

				// teleports the player to the farm's spawn location
				farm = Main.playerToFarm.get(player.getName());
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

					new BukkitRunnable(){ @Override public void run() { gemResetTry = 0; } }.runTaskLater(plugin, 100);
					
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
				if (farm == null){ player.sendMessage(ChatColor.RED+"You need a farm to do that!"); break; }
				if (farm.gems < 2e9 && farm.isLevel == 0){player.sendMessage(ChatColor.RED+"You need 2B gems to do that!"); break; }
				if (farm.AUI == null) player.openInventory(AlignmentUpgradeInventory.alignSelectInventory);
				else player.openInventory(farm.AUI.inventory);
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