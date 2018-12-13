package jdz.farmKing.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.stats.EventFlag;
import net.md_5.bungee.api.ChatColor;

public class Items {
	public static final ItemStack buyTypeItem;
	public static final ItemStack returnHomeItem;
	public static final ItemStack gemResetItem;
	public static final ItemStack tutorialBook;
	public static final ItemStack alignmentItem;
	public static final ItemStack achievementsItem;
	static
	{
		// book
		tutorialBook = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) tutorialBook.getItemMeta();
		meta.setDisplayName("Game Guide");
		meta.setTitle("§lTutorial");
		meta.setAuthor("Jonodonozym");
		List<String> pages = new ArrayList<String>();
		pages.add("  §lIntroduction\n§rWelcome to Farm King! The aim of this game mode is to earn as much money as you can.\n\n"
				+ "To get started, right-click the ender pearl.");
		pages.add("  §lManual Income\n§rTo start, turn right and break the tall grass around your farm to earn money."
				+ "Once you've earnt enough, you can either buy upgrades for the grass or buy some crops.");
		pages.add("  §lAutomaitc Income\n§rOnce you have $10, go to the Wheat and right-click the sign in front of it"
				+ " Congratulations, you bought your first plant! Look at the wall to see how much that plant is earning you.");
		pages.add("  §lUpgrades\n§rYou can buy upgrades for grass or crops by right-clicking the item frames nearby. Each crop upgrade"
				+ " doubles your income per plant, while grass has two upgrade types.");
		pages.add("  §lOffline income\n§rWhile offline, you earn 25% of your automatic income, so don't worry if you can't play all day!");
		pages.add("  §lGems\n§rOnce you begin to slow down, you can reset your farm to gain some gems. Do this by right-clicking the diamond."
				+ " Each gem gives a +2% bonus to your production, so resetting is essential to winning!");
		pages.add("  §lFarm Level\n§rYou can also increase your farm level by right-clicking the emerald. It resets your farm and gems to 0,"
				+ " but it gives your farm new bonuses, upgrades and appearance for each level.");
		pages.add("§rThat's all I'm teaching you for now! There are a lot more things to do in Farm King as you increase your farm level,"
				+ " but you'll have to work that out for yourself. Good luck and have fun!");
		meta.setPages(pages);
		tutorialBook.setItemMeta(meta);
		
		//others
		buyTypeItem = new ItemStack(Material.NETHER_STAR);
		returnHomeItem = new ItemStack(Material.ENDER_PEARL);
		gemResetItem = new ItemStack(Material.DIAMOND);
		alignmentItem = new ItemStack(Material.EXP_BOTTLE);
		achievementsItem = new ItemStack(Material.GOLD_INGOT);
		ItemMeta im1 = buyTypeItem.getItemMeta();
		ItemMeta im2 = returnHomeItem.getItemMeta();
		ItemMeta im3 = gemResetItem.getItemMeta();
		ItemMeta im4 = alignmentItem.getItemMeta();
		ItemMeta im5 = alignmentItem.getItemMeta();
		im1.setDisplayName(ChatColor.GREEN+"Buy 1");
		im2.setDisplayName(ChatColor.GREEN+"Go to farm");
		im3.setDisplayName(ChatColor.YELLOW+"Gem reset");
		im4.setDisplayName(ChatColor.DARK_AQUA+"Alignment");
		im5.setDisplayName(ChatColor.GOLD+"Achievements");
		buyTypeItem.setItemMeta(im1);
		returnHomeItem.setItemMeta(im2);
		gemResetItem.setItemMeta(im3);
		alignmentItem.setItemMeta(im4);
		achievementsItem.setItemMeta(im5);
		buyTypeItem.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	}
	
	public static void give(Player player) {
		Inventory inv = player.getInventory();
		
		inv.clear();
		inv.setItem(0, returnHomeItem);
		inv.setItem(1, buyTypeItem);
		inv.setItem(2, achievementsItem);
		inv.setItem(8, tutorialBook);
		inv.setItem(7, gemResetItem);
		
		if (!PlayerFarms.hasFarm(player))
			return;

		Farm f = PlayerFarms.get(player);
		if (EventFlag.ALIGNMENTS_UNLOCKED.isComplete(f))
			inv.addItem(Items.alignmentItem);
		
	}
}
