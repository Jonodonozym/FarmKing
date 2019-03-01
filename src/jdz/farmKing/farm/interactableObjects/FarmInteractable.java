
package jdz.farmKing.farm;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.interactableObject.InteractableObject;
import jdz.farmKing.crops.Crop;
import jdz.farmKing.farm.data.PlayerFarms;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public abstract class FarmInteractable extends InteractableObject {
	private int farmId;
	private int cropId;

	protected Farm getFarm() {
		return PlayerFarms.getById(farmId);
	}

	protected Crop getCrop() {
		return getFarm().getCrops()[cropId];
	}

	public abstract void generate();

	public abstract void update();

	public abstract void delete();

	protected abstract void interact(Player player);

	@Override
	public void onInteract(Player player) {
		Farm farm = getFarm();
		if (!farm.getOwner().equals(player)) {
			player.sendMessage(ChatColor.RED + "You can only do that on your own farm");
			return;
		}

		interact(player);
	}

}
