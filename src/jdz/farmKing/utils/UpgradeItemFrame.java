
package jdz.farmKing.utils;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import jdz.farmKing.FarmKing;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.PlayerFarms;
import lombok.Getter;

public abstract class UpgradeItemFrame {
	private final UUID id;
	@Getter private boolean hasBought = false;
	@Getter private double cost;

	public UpgradeItemFrame(ItemFrame frame) {
		this.hasBought = hasBought;
		this.cost = cost;

		if (frame.getMetadata("frameUpgrade").isEmpty()) {
			id = UUID.randomUUID();
			frame.setMetadata("frameUpgrade", new FixedMetadataValue(FarmKing.getInstance(), id));
		}
		else
			id = (UUID) frame.getMetadata("frameUpgrade").get(0).value();
	}

	private boolean is(ItemFrame frame) {
		return !frame.getMetadata("frameUpgrade").isEmpty()
				&& id.equals(frame.getMetadata("frameUpgrade").get(0).value());
	}

	public abstract boolean canBuy(Player player);

	public abstract void onBuy(Player player);

	@EventHandler
	public void itemFrameClick(PlayerInteractEntityEvent event) {

		if ((event.getRightClicked() instanceof ItemFrame)) {
			ItemFrame i = (ItemFrame) event.getRightClicked();

			Player player = event.getPlayer();
			Farm farm = PlayerFarms.get(player);

			if (farm != null && farm.isIn(i.getLocation()))
				farm.clickItemFrame(i);
			else
				player.sendMessage(ChatColor.RED + "You must be on your island to do that");
		}
		event.setCancelled(true);
	}
}
