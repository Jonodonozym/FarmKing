
package jdz.farmKing.crops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import jdz.UEconomy.UEcoFormatter;
import jdz.farmKing.utils.Direction;

public class CropInfoSign {
	private final Crop crop;
	private final Direction direction;
	private final Location origin;

	private Sign sign;

	public CropInfoSign(Crop crop, Direction direction) {
		this.crop = crop;
		this.direction = direction;
		Location blockLoc = crop.getLocation().clone();
		origin = blockLoc.add(5 * direction.getDx(), 1, 5 * direction.getDz());
	}

	@SuppressWarnings("deprecation")
	public void generate() {
		Block signBlock = origin.getBlock();

		signBlock.setType(Material.WALL_SIGN);
		signBlock.setData(direction.opposite().getBlockData());

		sign = (Sign) signBlock.getState();
		sign.setLine(0, "$ per second:");
		update(0);
	}

	public void update(double incomePerPlant) {
		double totalIncome = incomePerPlant * crop.getQuantity();
		sign.setLine(1, "Plant: $" + UEcoFormatter.charFormat(incomePerPlant, 4));
		sign.setLine(2, "Total: $" + UEcoFormatter.charFormat(totalIncome, 4));
		sign.update(true);
	}

	public void delete() {
		sign.getBlock().setType(Material.AIR);
		sign = null;
	}
}
