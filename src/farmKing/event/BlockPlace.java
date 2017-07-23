package farmKing.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/** 
 * Prevents blocks from being placed in the main world
 * @author Jonodonozym
 * @version 1.0 implemented the listener
 */
public class BlockPlace implements Listener{

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		event.setCancelled(true);
	}
}
