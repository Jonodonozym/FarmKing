package jdz.farmKing.achievements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.farmKing.upgrades.Upgrade;

public class AchievementInventories implements Listener {
	private static Map<Achievement, ItemStack> achievementToStack;
	public static Map<Player, Integer> page = new HashMap<Player, Integer>();
	public static String title = ChatColor.DARK_GREEN+"Achievments";

	public static void reload() {
		achievementToStack = createDefaultItemStacks();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player)e.getWhoClicked();
		Inventory inv = p.getOpenInventory().getTopInventory();

        ItemStack stack = null;
        if (e.getCurrentItem() != null)
            stack = e.getCurrentItem();
        else if (e.getCursor() != null)
            stack = e.getCursor();
        
        if (inv != null && inv.getName().startsWith(title)){
        	if (stack != null && stack.getItemMeta() != null && stack.getItemMeta().getDisplayName() != null){
				if (stack.getItemMeta().getDisplayName().equals(ArrowType.NEXT.toString()))
					openPage(p, page.get(p)+1);
				if (stack.getItemMeta().getDisplayName().equals(ArrowType.PREVIOUS.toString()))
					openPage(p, page.get(p)-1);
        	}
			e.setCancelled(true);
		}
	}

	private static Map<Achievement, ItemStack> createDefaultItemStacks() {
		Map<Achievement, ItemStack> achToItem = new HashMap<Achievement, ItemStack>();
		for (Achievement a : AchievementData.achievements)
			if (!(a instanceof AchievementSeries))
				achToItem.put(a, defaultStack(a));
		for (Achievement a: AchievementData.subAchievements)
			achToItem.put(a, defaultStack(a));
		return achToItem;
	}
	
	private static ItemStack defaultStack(Achievement a){
		ItemStack itemStack = new ItemStack(a.icon, 1, a.iconDamage);
		ItemMeta itemMeta = itemStack.getItemMeta();

		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + a.description);

		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	private static void openPage(Player p, int number){
		page.put(p, number);
		p.openInventory(getPageInventory(p, number));
		
	}

	private static Inventory getPageInventory(Player player, int page) {
		Inventory pageInventory = Bukkit.createInventory(null, 54, title);
		int i = 0;
		for (int achIndex = page * 36; achIndex < Math.min((page + 1) * 36, AchievementData.achievements.size()); achIndex++) {
			Achievement achievement = AchievementData.achievements.get(achIndex);

			ItemStack itemStack = getPlayerStack(player, achievement);

			pageInventory.setItem(i++, itemStack);
		}
		
		if ((page + 1) * 36 < AchievementData.achievements.size())
			pageInventory.setItem(53, new ChangePageArrow(ArrowType.NEXT));
		if (page > 0)
			pageInventory.setItem(45, new ChangePageArrow(ArrowType.PREVIOUS));
		
		return pageInventory;
	}

	private static ItemStack getPlayerStack(Player player, Achievement achievement) {
		if (achievement instanceof AchievementSeries)
			return getPlayerStack(player, ((AchievementSeries)achievement).getCurrentAchievement(player));
		
		boolean isAchieved = AchievementData.isAchieved.get(player).get(achievement);
		
		ItemStack newStack = new ItemStack(achievementToStack.get(achievement));
		ItemMeta itemMeta = newStack.getItemMeta();
		List<String> lore = itemMeta.getLore();

		Upgrade u = achievement.getUpgrade();
		int prevLength = lore.size();
		if (u != null){
			lore.add("" + ChatColor.GRAY + u.getName() + ": " + ChatColor.ITALIC + u.getDescription());
			for (int i=0; i<u.getNumBonuses(); i++)
				lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC);
		}
		
		if (isAchieved) {
			itemMeta.setDisplayName(ChatColor.GREEN+achievement.name.replace('_', ' '));
			for (int i=prevLength; i<lore.size(); i++)
				lore.set(i,lore.get(i).replaceAll(ChatColor.GRAY.toString(), ChatColor.WHITE.toString()));
			newStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
			lore.add(ChatColor.GREEN + "Achievement Unlocked!");
		} else {
			itemMeta.setDisplayName(ChatColor.RED+achievement.name.replace('_', ' '));
		}
		itemMeta.setLore(lore);
		newStack.setItemMeta(itemMeta);
		return newStack;
	}
	
	private static class ChangePageArrow extends ItemStack{
		
		public ChangePageArrow(ArrowType type){
			super(Material.ARROW);
			ItemMeta im = getItemMeta();
			im.setDisplayName(type.toString());
			setItemMeta(im);
			setType(Material.ARROW);
		}
	}
	
	private static enum ArrowType{
		PREVIOUS,
		NEXT;
		
		@Override
		public String toString(){
			switch(this){
			case NEXT: return ChatColor.GREEN+"Next Page";
			case PREVIOUS: return ChatColor.GREEN+"Previous Page";
			}
			return "";
		}
	}
}
