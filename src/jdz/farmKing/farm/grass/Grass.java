
package jdz.farmKing.farm.grass;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.YELLOW;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import jdz.UEconomy.UEcoFormatter;
import jdz.UEconomy.data.UEcoBank;
import jdz.farmKing.FarmKing;
import jdz.farmKing.element.Element;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.gen.FarmGenerator;
import jdz.farmKing.farm.gen.FarmSchema;
import jdz.farmKing.stats.EventFlag;
import jdz.farmKing.stats.FarmStats;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Grass {
	public static final int GRASS_RESPAWN_TIMER = 30;
	@Getter @Setter private double baseSeedChance = 0.05;
	@Getter @Setter private double baseSeedChanceMultiplier = 1;
	@Getter @Setter private double baseGrassValue = 1;

	private final Farm farm;
	@Getter private int directLevel = 0;
	@Getter private int percentLevel = 0;
	private double incomePerClick = 0;

	public double updateIncome(Farm farm) {
		incomePerClick = baseGrassValue + GrassData.getDirectBonus(directLevel)
				+ farm.currentIncome * GrassData.getPercentBonus(percentLevel);
		return incomePerClick;
	}

	public void onBreak(GrassBreakEvent event) {
		UEcoBank.add(event.getPlayer(), incomePerClick);
		FarmStats.CLICKS_MANUAL.add(event.getFarm(), 1);

		Map<Element, Double> seeds = getRandomClickSeeds(event.getFarm(), 1);

		Hologram hologram = createHologram(event, incomePerClick, seeds);
		Bukkit.getScheduler().runTaskLater(FarmKing.getInstance(), () -> {
			hologram.delete();
		}, 10);

		respawnLater(event.getBlock());
	}

	private static Hologram createHologram(GrassBreakEvent event, double income, Map<Element, Double> seeds) {
		Location l = event.getBlock().getLocation().add(0.5, 1.25 + Math.random() / 2.0, 0.5);
		Hologram hologram = HologramsAPI.createHologram(FarmKing.getInstance(), l);

		hologram.appendTextLine(BOLD + "" + YELLOW + "+$" + UEcoFormatter.charFormat(income, 4));

		for (Element e : Element.values())
			if (seeds.get(e) > 0)
				hologram.appendTextLine(e.color + "+" + UEcoFormatter.charFormat(seeds.get(e), 4));
		return hologram;
	}

	private Map<Element, Double> getRandomClickSeeds(Farm farm, int numClicks) {
		if (!EventFlag.ALIGNMENTS_UNLOCKED.isComplete(farm))
			return new HashMap<>();

		Map<Element, Double> seeds = new HashMap<>();

		double totalChance = baseSeedChance * numClicks * baseSeedChanceMultiplier;

		double baseSeeds = (int) totalChance;
		double extraSeedChance = totalChance - baseSeeds;

		for (Element e : Element.values()) {
			double seedsGained = baseSeeds;
			if (extraSeedChance > Math.random())
				seedsGained++;

			FarmStats.SEEDS(e).add(farm, seedsGained);
			seeds.put(e, seedsGained);
		}

		return seeds;
	}

	@SuppressWarnings("deprecation")
	private static void respawnLater(Block block) {
		Bukkit.getScheduler().runTaskLater(FarmKing.getInstance(), () -> {
			block.setType(Material.LONG_GRASS);
			block.setData((byte) 1);
		}, GRASS_RESPAWN_TIMER);
	}

	@SuppressWarnings("deprecation")
	public void respawn() {
		FarmSchema schema = FarmGenerator.getSchematicForLevel(farm.level);
		World world = farm.spawn.getWorld();

		int xMax = farm.x + schema.getSchematic().getWidth();
		int zMax = farm.z + schema.getSchematic().getLength();
		int y = schema.getGrassHeight();
		for (int x = farm.x; x < xMax; x++)
			for (int z = farm.z; z < zMax; z++)
				if (world.getBlockAt(x, y, z).getType() == Material.GRASS) {
					Block b = world.getBlockAt(x, y + 1, z);
					b.setType(Material.LONG_GRASS);
					b.setData((byte) 1);
				}
	}
}
