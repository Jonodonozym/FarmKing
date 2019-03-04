
package jdz.farmKing.command;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.farmKing.element.data.PlayerElementData;
import jdz.farmKing.element.data.PlayerElementDataManager;
import jdz.farmKing.element.gui.ElementSelectInventory;
import jdz.farmKing.element.gui.ElementUpgradeInventory;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.stats.OneTimeEvent;
import net.md_5.bungee.api.ChatColor;

@CommandLabel("alignment")
@CommandLabel("alignments")
@CommandLabel("a")
public class FarmAlignmentsCommand extends SubCommand {

	@CommandMethod
	public void openAlignmentsGUI(Player player) {
		Farm farm = PlayerFarms.get(player);

		if (farm == null) {
			player.sendMessage(ChatColor.RED + "You need a farm to do that!");
			return;
		}

		if (!OneTimeEvent.ALIGNMENTS_UNLOCKED.isComplete(farm)) {
			player.sendMessage(ChatColor.RED + "You need 2B gems to do that!");
			return;
		}

		PlayerElementData data = PlayerElementDataManager.getInstance().get(player);
		if (data.getElement() == null)
			ElementSelectInventory.getInstance().open(player);
		else
			ElementUpgradeInventory.getInstance().open(player);
	}
}
