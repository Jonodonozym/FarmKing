
package jdz.farmKing.crops;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import jdz.UEconomy.UEcoFormatter;
import jdz.UEconomy.data.UEcoBank;
import jdz.farmKing.HologramManager;
import jdz.farmKing.farm.Farm;

public class CropInfoHologram {
	private final Crop crop;
	private final Farm farm;
	private final Location loc;

	private Hologram hologram;
	private TextLine l1, l2;

	public CropInfoHologram(Crop crop) {
		this.crop = crop;
		farm = crop.getFarm();
		loc = crop.getLocation();
	}

	public void generate() {
		Location l = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY() + 2.5, loc.getBlockZ() + 0.5);
		hologram = HologramManager.make(farm.getOwner(), l);
		l1 = hologram.appendTextLine("");
		l2 = hologram.appendTextLine("");
		update();
	}

	public void update() {
		double buyCost = crop.getBuyCost(farm.buyQuantity);

		ChatColor colorCode = ChatColor.RED;
		if (UEcoBank.has(farm.getOwner(), buyCost))
			colorCode = ChatColor.GREEN;

		l1.setText(crop.getQuantity() + " Plants");
		l2.setText(colorCode + "Buy " + farm.buyQuantity + ": $" + UEcoFormatter.charFormat(buyCost, 4));
	}

	public void delete() {
		HologramManager.delete(hologram);
	}
}
