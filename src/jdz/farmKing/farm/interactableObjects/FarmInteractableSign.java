
package jdz.farmKing.farm.interactableObjects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import jdz.farmKing.utils.Direction;

public abstract class FarmInteractableSign extends FarmInteractable {
	protected int direction;
	
	public FarmInteractableSign(Sign sign) {
		readMetadata(sign);
	}

	public FarmInteractableSign(int farmId, Direction direction) {
		this(farmId, 0, direction);
	}

	public FarmInteractableSign(int farmId, int cropId, Direction direction) {
		super(farmId, cropId);
		this.direction = direction.ordinal();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void generate() {
		Block signBlock = getLocation().getBlock();

		signBlock.setType(Material.WALL_SIGN);
		signBlock.setData(Direction.values()[direction].getBlockData());

		Sign sign = (Sign) signBlock.getState();
		generate(sign);
		writeMetadata(sign);
		update();
	}

	@Override
	public void update() {
		Sign sign = getSign();
		update(sign);
		sign.update(true);
	}

	@Override
	public void delete() {
		getLocation().getBlock().setType(Material.AIR);
	}
	
	protected Direction getDirection() {
		return Direction.values()[direction];
	}

	protected abstract void generate(Sign sign);

	protected abstract Location getLocation();

	protected abstract void update(Sign sign);

	protected Sign getSign() {
		return (Sign) getLocation().getBlock().getState();
	}

}
