package farmKing.event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class CropTrample implements Listener {
	@EventHandler
	public void onEntityChangeBLock(EntityChangeBlockEvent event) {
		if (event.getTo() == Material.DIRT && event.getBlock().getType() == Material.SOIL)
			event.setCancelled(true);
		}
	
}
