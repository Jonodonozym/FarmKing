
package jdz.farmKing.farm.grass;

import static jdz.UEconomy.UEcoFormatter.charFormat;
import static org.bukkit.ChatColor.RED;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.UEconomy.data.UEcoBank;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.interactableObjects.FarmInteractableItemFrame;

public class GrassUpgradeFrame extends FarmInteractableItemFrame {
	private boolean isDirect;
	private int level;
	private boolean bought;

	public GrassUpgradeFrame(Farm farm, int level, boolean isDirect) {

	}

	public void update(ItemFrame frame) {
		frame.setItem(getItemStack());
	}

	private ItemStack getItemStack() {
		ItemStack item = new ItemStack(Material.WOOL, 1, (short) 14);
		ItemMeta itemMeta = item.getItemMeta();
		if (isDirect)
			itemMeta.setDisplayName(RED + "+" + charFormat(GrassData.getDirectBonus(level) + 1, 4) + "  $"
					+ charFormat(GrassData.getDirectCost(level), 4));
		else
			itemMeta.setDisplayName(RED + "+" + charFormat(GrassData.getPercentBonus(level) * 100, 4) + "%  $"
					+ charFormat(GrassData.getPercentCost(level), 4));
		item.setItemMeta(itemMeta);
		return item;
	}

	@Override
	protected Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void interact(Player player) {
		if (bought)
			return;

		Grass grass = getFarm().getGrass();

		double cost = isDirect ? GrassData.getDirectCost(level) : GrassData.getPercentCost(level);
		if (!UEcoBank.has(player, cost)) {
			player.sendMessage(ChatColor.RED + "You don't have enough money to purchase that upgrade!");
			return;
		}
		UEcoBank.subtract(player, cost);

		bought = true;
		writeMetadata(getFrame());
		update();

		if (level < 5)
			new GrassUpgradeFrame(getFarm(), level + 1, isDirect).generate();

		if (isDirect)
			grass.setDirectLevel(level + 1);
		else
			grass.setPercentLevel(level + 1);
	}
}
