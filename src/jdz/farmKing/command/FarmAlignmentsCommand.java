
package jdz.farmKing.command;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.farmKing.element.ElementSelectInventory;
import jdz.farmKing.farm.EventFlag;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.PlayerFarms;
import net.md_5.bungee.api.ChatColor;

@CommandLabel("alignment")
@CommandLabel("alignments")
@CommandLabel("a")
public class FarmAlignmentsCommand {

	@CommandMethod
	public void openAlignmentsGUI(Player player) {
		Farm farm = PlayerFarms.get(player);

		if (farm == null) {
			player.sendMessage(ChatColor.RED + "You need a farm to do that!");
			return;
		}

		if (!farm.eventIsComplete.get(EventFlag.ALIGNMENTS_UNLOCKED)) {
			player.sendMessage(ChatColor.RED + "You need 2B gems to do that!");
			return;
		}

		if (farm.elementInventory == null)
			player.openInventory(ElementSelectInventory.alignSelectInventory);
		else {
			farm.elementInventory.update();
			player.openInventory(farm.elementInventory.inventory);
		}
	}
}
