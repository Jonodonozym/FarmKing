
package jdz.farmKing.command;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.PlayerFarms;

@CommandLabel("go")
public class FarmGoCommand extends SubCommand {

	@CommandMethod
	public void warpToOwnFarm(Player player) {
		Farm farm = PlayerFarms.get(player);
		player.teleport(farm.spawn);
		player.getWorld().playSound(farm.spawn, Sound.ENTITY_BAT_TAKEOFF, 1, 1);
	}

}
