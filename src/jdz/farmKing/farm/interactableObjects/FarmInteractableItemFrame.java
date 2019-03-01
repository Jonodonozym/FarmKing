
package jdz.farmKing.farm.interactableObjects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import jdz.farmKing.utils.Direction;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class FarmInteractableItemFrame extends FarmInteractable {
	private int direction;

	public FarmInteractableItemFrame(ItemFrame frame) {
		readMetadata(frame);
	}

	public FarmInteractableItemFrame(int farmId, Direction direction) {
		this(farmId, 0, direction);
	}

	public FarmInteractableItemFrame(int farmId, int cropId, Direction direction) {
		super(farmId, cropId);
		this.direction = direction.ordinal();
	}

	@Override
	public void generate() {
		try {
			Location loc = getLocation();
			Direction direction = Direction.values()[this.direction];
			ItemFrame itemFrame = loc.getWorld().spawn(loc, ItemFrame.class);
			itemFrame.setFacingDirection(direction.opposite().getFace());
			writeMetadata(itemFrame);
		}
		catch (IllegalArgumentException e) { /* already generated */ }
		update();
	}

	@Override
	public void update() {
		update(getFrame());
	}

	@Override
	public void delete() {
		getFrame().remove();
	}

	protected ItemFrame getFrame() {
		Location location = getLocation();
		for (Entity entity : location.getChunk().getEntities())
			if (entity.getLocation().distance(location) < 0.1 && entity instanceof ItemFrame)
				return (ItemFrame) entity;
		return null;
	}

	protected Direction getDirection() {
		return Direction.values()[direction];
	}

	protected abstract Location getLocation();

	protected abstract void update(ItemFrame frame);

}
