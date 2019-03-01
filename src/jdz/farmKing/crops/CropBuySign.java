
package jdz.farmKing.crops;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import jdz.farmKing.farm.interactableObjects.FarmInteractableSign;
import jdz.farmKing.utils.Direction;

public class CropBuySign extends FarmInteractableSign {
	public CropBuySign(Sign sign) {
		super(sign);
	}

	public CropBuySign(Crop crop, Direction direction) {
		super(crop.getFarm().getId(), crop.getType().getId(), direction);
	}

	@Override
	public void generate(Sign sign) {
		sign.setLine(1, getCrop().getType().getName());
	}

	@Override
	public void update(Sign sign) {
		sign.setLine(2, "Buy " + getFarm().buyQuantity);
	}

	@Override
	protected Location getLocation() {
		Direction direction = Direction.values()[this.direction];
		return getCrop().getLocation().clone().add(direction.getDx(), 0, direction.getDz());
	}

	@Override
	public void interact(Player player) {
		getCrop().buy(getFarm().buyQuantity);
	}
}