package farmKing.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class CropGrowth implements Listener {
	
	@EventHandler
	public void stopPlantGrowth(BlockGrowEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void stopTreeGrowth(StructureGrowEvent e){
		e.setCancelled(true);
	}
}
