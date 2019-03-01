
package jdz.farmKing.crops;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import jdz.farmKing.utils.Direction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CropVisualisationGenerator {
	private final Crop crop;

	public void generatePlants(int oldQuantity, int newQuantity) {
		int minRow = getRowFromQuantity(oldQuantity);
		int maxRow = getRowFromQuantity(newQuantity);
		int generatedRows = maxRow - minRow;

		if (generatedRows == 0)
			return;

		int x, z, width, length;
		Direction direction = crop.getDirection().opposite();

		if (crop.getDirection().getDx() == 0) {
			x = crop.getLocation().getBlockX() - 2;
			width = 5;
			z = crop.getLocation().getBlockZ() + direction.getDz() * (4 + minRow);
			length = generatedRows;
			if (direction.getDz() < 0)
				z -= length;
		}
		else {
			z = crop.getLocation().getBlockZ() - 2;
			length = 5;
			x = crop.getLocation().getBlockX() + direction.getDx() * (4 + minRow);
			width = generatedRows;
			if (crop.getDirection().opposite().getDx() < 0)
				x -= width;
		}

		setRegionToPlant(x, z, width, length);
	}

	private int getRowFromQuantity(int quantity) {
		return Math.min(quantity / 20, 12) + 1;
	}

	@SuppressWarnings("deprecation")
	private void setRegionToPlant(int x, int z, int width, int length) {
		int y = crop.getLocation().getBlockY();
		World world = crop.getLocation().getWorld();
		for (int xx = x; xx <= x + width; x += 1)
			for (int zz = z; zz <= z + length; z += 1) {
				Block block = world.getBlockAt(x, y, z);
				block.setType(crop.getType().getMaterial());
				block.setData(crop.getType().getData());
				world.getBlockAt(x, y - 1, z).setType(crop.getType().getBase());
				if (crop.getType().getMaterial() == Material.SUGAR_CANE_BLOCK) {
					if ((zz - z) % 2 == 0)
						world.getBlockAt(x, y + 1, z).setType(Material.SUGAR_CANE_BLOCK);
					else {
						block.setType(Material.AIR);
						world.getBlockAt(x, y - 1, z).setType(Material.STATIONARY_WATER);
					}
				}
				world.createExplosion(x, y, zz, 0, false, false);
			}
	}

}
