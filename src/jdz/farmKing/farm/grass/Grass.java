
package jdz.farmKing.farm.grass;

import static jdz.farmKing.upgrades.UpgradeBonus.SEED_MULTIPLIER;
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
import jdz.farmKing.farm.generation.FarmSchema;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.stats.OneTimeEvent;
import jdz.farmKing.upgrades.UpgradeBonus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Grass {
	public static final int GRASS_RESPAWN_TIMER = 30;
	private double baseSeedChance = 0.05;
	private double baseGrassValue = 1;

	private final Farm farm;
	@Setter @Getter private int directLevel = 0;
	@Setter @Getter private int percentLevel = 0;
	@Getter private double incomePerClick = 0;

	public double updateIncome() {
		incomePerClick = baseGrassValue + GrassData.getDirectBonus(directLevel)
				+ farm.getCropIncome() * GrassData.getPercentBonus(percentLevel);
		return incomePerClick;
	}

	public void onBreak(GrassBreakEvent event) {
		UEcoBank.add(event.getPlayer(), incomePerClick);
		FarmStats.CLICKS_MANUAL.add(event.getFarm(), 1);

		Map<Element, Double> seeds = getRandomSeeds(1);
		for (Element element : seeds.keySet())
			FarmStats.SEEDS(element).add(farm, seeds.get(element));

		Hologram hologram = createHologram(event, incomePerClick, seeds);
		Bukkit.getScheduler().runTaskLater(FarmKing.getInstance(), () -> {
			hologram.delete();
		}, 10);

		respawnLater(event.getBlock());
	}

	public void autoClick(double amount) {
		UEcoBank.add(farm.getOwner(), incomePerClick * amount);
		getRandomSeeds(amount);
		FarmStats.CLICKS_AUTO.add(farm, amount);
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

	public Map<Element, Double> getRandomSeeds(double numClicks) {
		return getRandomSeeds(numClicks, 1, 0);
	}

	public Map<Element, Double> getRandomSeeds(double numClicks, double extraMultiplier, double extraFlat) {
		if (!OneTimeEvent.ALIGNMENTS_UNLOCKED.isComplete(farm))
			return new HashMap<>();

		Map<Element, Double> seeds = new HashMap<>();

		double totalChance = numClicks * (baseSeedChance * farm.getUpgradeBonus(SEED_MULTIPLIER) * extraMultiplier
				+ farm.getUpgradeBonus(UpgradeBonus.SEED_FLAT) + extraFlat);

		double baseSeeds = (int) totalChance;
		double extraSeedChance = totalChance - baseSeeds;

		for (Element e : Element.values()) {
			double seedsGained = baseSeeds;
			if (extraSeedChance > Math.random())
				seedsGained++;
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
		FarmSchema schema = farm.getSchematic();
		World world = farm.getSpawn().getWorld();

		int xMax = farm.getOrigin().getBlockX() + schema.getSize().getBlockX();
		int zMax = farm.getOrigin().getBlockZ() + schema.getSize().getBlockZ();
		int y = schema.getGrassHeight();
		for (int x = farm.getOrigin().getBlockX(); x < xMax; x++)
			for (int z = farm.getOrigin().getBlockZ(); z < zMax; z++)
				if (world.getBlockAt(x, y, z).getType() == Material.GRASS) {
					Block b = world.getBlockAt(x, y + 1, z);
					b.setType(Material.LONG_GRASS);
					b.setData((byte) 1);
				}

		for (int i = 0; i <= Math.min(4, directLevel); i++)
			new GrassUpgradeFrame(farm, directLevel, true).generate();
		for (int i = 0; i <= Math.min(4, percentLevel); i++)
			new GrassUpgradeFrame(farm, percentLevel, false).generate();
	}

	public void reset() {
		for (int i = 0; i <= Math.min(4, directLevel); i++)
			new GrassUpgradeFrame(farm, directLevel, true).delete();
		for (int i = 0; i <= Math.min(4, percentLevel); i++)
			new GrassUpgradeFrame(farm, percentLevel, false).delete();
		directLevel = 0;
		percentLevel = 0;
	}
}
