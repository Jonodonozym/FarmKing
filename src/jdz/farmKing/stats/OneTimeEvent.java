
package jdz.farmKing.stats;

import java.util.function.Predicate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.FarmScoreboards;
import jdz.farmKing.utils.Items;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OneTimeEvent {
	ALIGNMENTS_UNLOCKED((farm) -> {
		return farm.getLevel() > 1 || FarmStats.GEMS.get(farm) > 2e9;
	}, (farm, player) -> {
		String[] messages = new String[] {
				ChatColor.DARK_GREEN + "============[ " + ChatColor.DARK_AQUA + "Elements Unlocked "
						+ ChatColor.DARK_GREEN + "]============",
				ChatColor.WHITE + "Now that you've reached 2B gems, you can align with one of the elements!",
				ChatColor.WHITE + "Right-click the bottle to chose an element and see what it does!",
				ChatColor.WHITE + "You can pick a different element every gem reset!",
				ChatColor.DARK_GREEN + "=============================================" };

		player.sendMessage(messages);
		player.getInventory().addItem(Items.alignmentItem);
		FarmScoreboards.addSeedSection(player);
	}),

	WORKERS_UNLOCKED((farm) -> {
		return farm.getLevel() > 1 || FarmStats.WORKERS.get(farm) > 0;
	}, (farm, player) -> {
		String[] messages = new String[] {
				ChatColor.DARK_GREEN + "============[ " + ChatColor.DARK_AQUA + "Workers Unlocked " + ChatColor.DARK_GREEN
						+ "]============",
				ChatColor.WHITE + "You gained a worker! These little fellas will help you earn money and seeds",
				ChatColor.WHITE + "Each worker automatically breaks tall grass once every ten seconds",
				ChatColor.WHITE + "You gain 1 worker for each crop type that has 100 plants",
				ChatColor.DARK_GREEN + "=============================================" };
		
		player.sendMessage(messages);
		FarmScoreboards.addWorkerSection(player);
	});

	private final Predicate<Farm> condition;
	private final FarmFunction onUnlock;

	public boolean isComplete(Farm f) {
		return condition.test(f);
	}

	public void onUnlock(Farm farm) {
		onUnlock.apply(farm, farm.getOwner().getPlayer());
	}

	private static interface FarmFunction {
		public void apply(Farm farm, Player owner);
	}
}
