
package jdz.farmKing.farm;

import static jdz.UEconomy.UEcoFormatter.charFormat;
import static jdz.UEconomy.UEcoFormatter.makeWhole;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import jdz.farmKing.HologramManager;

public class FarmInfoHologram {
	private Farm farm;
	private Hologram hologram;
	private TextLine gemMultiplierLine, gemsFromResetLine;

	public FarmInfoHologram(Farm farm) {
		this.farm = farm;

		hologram = HologramManager.make(farm.getOwner(), getLocation());
		hologram.appendTextLine(YELLOW + "FARM DETAILS");
		hologram.appendTextLine("");
		gemMultiplierLine = hologram.appendTextLine("");
		gemsFromResetLine = hologram.appendTextLine("");
		hologram.appendTextLine("");
		hologram.appendTextLine(GREEN + "Bonuses from farm level " + farm.getLevel() + ":");
		hologram.appendTextLine(RED + "None");
		hologram.appendTextLine("");
		hologram.appendTextLine(
				GREEN + "Gems required for level up: " + makeWhole(charFormat(farm.getLevelupGemRequirement(farm.getLevel() + 1), 4)));
	}

	public void update() {
		gemMultiplierLine
				.setText(GREEN + "Multiplier from gems: " + YELLOW + "x" + charFormat(farm.getGemMultiplier(), 4));
		gemsFromResetLine.setText(GREEN + "Gems gained from resetting: " + YELLOW
				+ makeWhole(charFormat(farm.getGemsFromResetting(), 4)));
	}

	public Location getLocation() {
		return farm.getSpawn().clone().add(0, 3, 3);
	}

}
