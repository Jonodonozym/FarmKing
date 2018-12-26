
package jdz.farmKing.element.gui;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;
import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;
import jdz.farmKing.element.ElementMetaData;
import jdz.farmKing.element.data.PlayerElementData;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.stats.FarmStats;

public class ElementTierStack extends ClickableStack {
	private final PlayerElementData data;
	private final int tier;

	public ElementTierStack(PlayerElementData data, int tier) {
		super(data.getElement().icon, "");
		this.data = data;
		this.tier = tier;
		update();
	}

	@Override
	public void update() {
		if (data.hasBoughtTier(tier)) {
			setName(ChatColor.GREEN + "Tier " + (tier + 1) + " bought!");
			setLore(new ArrayList<String>());
			if (getStack().getEnchantments().isEmpty())
				getStack().addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		}
		else {
			setName(ChatColor.RED + "Tier " + (tier + 1));
			setLore(Arrays.asList(ChatColor.RED + "Costs " + ElementMetaData.getTierCost(tier) + " "
					+ data.getElement().name + " seeds"));
		}
	}

	@Override
	public void onClick(Player player, GuiMenu menu, InventoryClickEvent event) {
		if (data.hasBoughtTier(tier))
			return;

		Farm farm = PlayerFarms.get(player);
		if (FarmStats.SEEDS(data.getElement()).get(player) < ElementMetaData.getTierCost(tier))
			return;

		FarmStats.SEEDS(data.getElement()).subtract(farm, ElementMetaData.getTierCost(tier));
		data.setBoughtTier(tier);
		menu.updateItems(event.getClickedInventory());
	}

}
