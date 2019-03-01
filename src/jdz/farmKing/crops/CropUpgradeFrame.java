
package jdz.farmKing.crops;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.UEconomy.UEcoFormatter;
import jdz.UEconomy.data.UEcoBank;
import jdz.farmKing.crops.calculators.CropUpgradeCalculator;
import jdz.farmKing.farm.interactableObjects.FarmInteractableItemFrame;
import jdz.farmKing.utils.Direction;

public class CropUpgradeFrame extends FarmInteractableItemFrame {
	private int level;
	private boolean bought;

	public CropUpgradeFrame(ItemFrame frame) {
		readMetadata(frame);
	}

	public CropUpgradeFrame(Crop crop, Direction direction, int level, boolean bought) {
		super(crop.getFarm().getId(), crop.getType().getId(), direction);
		this.level = level;
		this.bought = bought;
	}

	@Override
	public void update(ItemFrame frame) {
		if (bought)
			frame.setItem(getBoughtItem());
		else
			frame.setItem(getBuyableItem());
	}

	private ItemStack getBuyableItem() {
		ItemStack itemStack = new ItemStack(Material.WOOL, 1, (short) 14);

		String nextQuan = CropUpgradeCalculator.getQuantityRequired(level) + "";
		String nextCost = UEcoFormatter.charFormat(CropUpgradeCalculator.getCost(getCrop(), level), 4);

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.RED + "" + nextQuan + " plants.  $" + nextCost);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	private ItemStack getBoughtItem() {
		ItemStack itemStack = new ItemStack(Material.WOOL, 1, (short) 13);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "Upgrade Purchased!");
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	@Override
	protected Location getLocation() {
		Direction direction = getDirection();
		Location cropLoc = getCrop().getLocation().clone();
		Location origin = cropLoc.add(3 * direction.getDx(), 4, 3 * direction.getDz());
		return origin.clone().add(direction.getDz() * (level % 5), level / 5, direction.getDx() * (level % 5));
	}

	@Override
	public void interact(Player player) {
		if (bought)
			return;

		Crop crop = getCrop();

		double reqQuan = CropUpgradeCalculator.getQuantityRequired(level);
		if (crop.getQuantity() < reqQuan) {
			player.sendMessage(ChatColor.RED + "You need " + reqQuan + " plants to purchase this upgrade!");
			return;
		}

		double upgradePrice = CropUpgradeCalculator.getCost(crop, level);
		if (!UEcoBank.has(player, upgradePrice)) {
			player.sendMessage(ChatColor.RED + "You don't have enough money to purchase this upgrade!");
			return;
		}

		UEcoBank.subtract(player, upgradePrice);

		bought = true;
		update();
		writeMetadata(getFrame());

		crop.levelUp();
	}
}
