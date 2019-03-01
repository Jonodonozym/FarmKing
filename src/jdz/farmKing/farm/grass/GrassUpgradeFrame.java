
package jdz.farmKing.farm.grass;

import static jdz.UEconomy.UEcoFormatter.charFormat;
import static org.bukkit.ChatColor.RED;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.farmKing.farm.interactableObjects.FarmInteractableItemFrame;

public class GrassUpgradeFrame extends FarmInteractableItemFrame {
	private boolean isDirect;
	private int level;

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
		// TODO Auto-generated method stub
		Grass grass = getFarm().getGrass();
	}


}
