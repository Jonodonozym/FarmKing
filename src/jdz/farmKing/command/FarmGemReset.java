
package jdz.farmKing.command;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import jdz.UEconomy.UEcoFormatter;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.farmKing.FarmKing;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.PlayerFarms;
import net.md_5.bungee.api.ChatColor;

@CommandLabel("reset")
@CommandLabel("gemReset")
@CommandLabel("gm")
public class FarmGemReset {
	private Set<Player> hasPerformedCheck = new HashSet<Player>();

	@CommandMethod
	public void execute(Player player) {
		Farm farm = PlayerFarms.get(player);

		if (farm == null) {
			player.sendMessage(ChatColor.RED + "You need a farm to do that!");
			return;
		}

		if (hasPerformedCheck.remove(player)) {
			farm.gemReset();
			return;
		}

		hasPerformedCheck.add(player);

		if (farm.gemResetAmount > 0)
			player.sendMessage(ChatColor.YELLOW + "Are you sure you want to do that? You will gain "
					+ UEcoFormatter.charFormat(farm.gemResetAmount, 4) + " gem" + (farm.gemResetAmount == 1 ? "" : "s")
					+ " by resetting.");
		else
			player.sendMessage(ChatColor.YELLOW
					+ "Are you sure you want to do that? You wont gain any gems by resetting. You can reset for"
					+ " your first gem after earning $" + UEcoFormatter.charFormat(farm.getNextGemAmount(), 4));

		Bukkit.getScheduler().runTaskLater(FarmKing.getInstance(), () -> {
			hasPerformedCheck.remove(player);
		}, 100);
	}

}
