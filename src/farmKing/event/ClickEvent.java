package farmKing.event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import farmKing.alignment.AlignmentUpgradeInventory;
import farmKing.crops.Crop;
import farmKing.crops.CropType;
import farmKing.crops.Farm;
import farmKing.main.Main;

/**
 * Prevents the user from manipulating their inventory while in the farm world
 * Also executes code when right-clicking an item in the hand, a sign or an itemframe
 * @author Jonodonozym
 *
 */
public class ClickEvent implements Listener{
	Main plugin;
	
	public ClickEvent(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event){
		event.setCancelled(true);
		}
	
	@EventHandler
	public void invDrag(InventoryDragEvent event){
		event.setCancelled(true);
		}
	
	@EventHandler
	public void invClick(InventoryClickEvent event){
		AlignmentUpgradeInventory.onInventoryClick(event);
		event.setCancelled(true);
		}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_BLOCK)){
	        Material clickedBlock = event.getClickedBlock().getType();
	        
	        // Clicking a buy sign
	        if(clickedBlock==Material.WALL_SIGN) {
	            Farm f = Main.playerToFarm.get(player.getName());
	            if (f != null && f.isIn(event.getClickedBlock().getLocation())){
		            Sign a = (Sign)event.getClickedBlock().getState();
		            
		            // Ensures that it is actually a buy sign
		            if (CropType.cropToIndex.containsKey(a.getLine(1)))
		            {
		            	int index = CropType.cropToIndex.get(a.getLine(1));
		            	Crop c = f.crops[index];
		            	if (c.buy(f.buyQuantity)){
							c.updateHologram();
		            		f.generateCrop(index+1);
		            		f.updateCropPlants(index);
		            	}
		            	else
		            		player.sendMessage(ChatColor.RED+"You don't have enough money to buy that!");
		            	return;
		        	}
	            }
	        }
        }
        
        // using items
        if ((action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))){
        	Farm f = Main.playerToFarm.get(player.getName());
            ItemStack is = event.getItem();
            if (is == null) return;
            Material material = is.getType();
            switch(material){
            case NETHER_STAR:
        		if (f == null) return;
	        	ItemMeta i = is.getItemMeta();
	        	
	        	switch(i.getDisplayName().substring(i.getDisplayName().indexOf(' ')+1)){
	        	case "1":	f.buyQuantity = 10; break;
	        	case "10":	f.buyQuantity = 100; break;
	        	case "100":	f.buyQuantity = 1000; break;
	        	case "1000":	f.buyQuantity = 1; break;
	        	}
	        	
	        	i.setDisplayName(ChatColor.GREEN+"Buy "+f.buyQuantity); 
	        	is.setItemMeta(i);
	        	player.setItemInHand(is);
	        	for (Crop c: f.crops)
	        		c.updateHologram();
	        	break;
            case ENDER_PEARL: player.performCommand("f go"); break;
            case DIAMOND: player.performCommand("f gemreset"); break;
            case EMERALD: player.performCommand("f rankup"); break;
            case EXP_BOTTLE: player.performCommand("f alignment"); break;
            default: break;
            }
        }
    }
	
	//for item frames
	@EventHandler
	public void itemFrameClick(PlayerInteractEntityEvent event){

		if ((event.getRightClicked() instanceof ItemFrame)){
			ItemFrame i = (ItemFrame)event.getRightClicked();
			
			Player player = event.getPlayer();
			Farm farm = Main.playerToFarm.get(player.getName());
			
			if (farm != null && farm.isIn(i.getLocation()))
				farm.clickItemFrame(i);
			else player.sendMessage(ChatColor.RED+"You must be on your island to do that");
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void epearlDisable(ProjectileLaunchEvent event){
		event.setCancelled(true);
	}
}
