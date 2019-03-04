package jdz.farmKing.farm;

import static jdz.farmKing.stats.FarmStats.*;
import static jdz.farmKing.upgrades.UpgradeBonus.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import jdz.UEconomy.UEcoFormatter;
import jdz.UEconomy.data.UEcoBank;
import jdz.farmKing.FarmKing;
import jdz.farmKing.achievements.FarmAchievementSeries;
import jdz.farmKing.achievements.FarmAchievements;
import jdz.farmKing.crops.Crop;
import jdz.farmKing.crops.CropType;
import jdz.farmKing.element.Element;
import jdz.farmKing.element.ElementMetaData;
import jdz.farmKing.element.data.PlayerElementData;
import jdz.farmKing.element.data.PlayerElementDataManager;
import jdz.farmKing.farm.data.FarmDB;
import jdz.farmKing.farm.generation.FarmGenerator;
import jdz.farmKing.farm.generation.FarmSchema;
import jdz.farmKing.farm.grass.Grass;
import jdz.farmKing.farm.grass.GrassInfoSign;
import jdz.farmKing.stats.OneTimeEvent;
import jdz.farmKing.upgrades.Upgrade;
import jdz.farmKing.upgrades.UpgradeBonus;
import jdz.farmKing.utils.BuyAmount;
import lombok.Getter;

public class Farm {
	@Getter private final int id;
	@Getter private World world;

	@Getter public double gemsFromResetting = 0;

	@Getter private OfflinePlayer owner = null;

	public void setOwner(OfflinePlayer owner) {
		this.owner = owner;
		FarmDB.getInstance().setOwner(this, owner);
	}

	@Getter private Location origin;

	@Getter private int cropHeight;

	private FarmInfoHologram infoHologram = new FarmInfoHologram(this);
	@Getter public Crop[] crops = new Crop[16];
	@Getter private Grass grass = new Grass(this);
	private GrassInfoSign grassInfoSign;

	public BuyAmount buyQuantity = BuyAmount.BUY_1;

	@Getter private double income = 0, cropIncome = 0;

	private Map<UpgradeBonus, Double> upgradeBonuses = new HashMap<>();
	private Set<OneTimeEvent> completedEvents = new HashSet<>();

	public double getUpgradeBonus(UpgradeBonus type) {
		return upgradeBonuses.get(type);
	}

	public double getGemMultiplier() {
		return 1 + 0.02 * GEMS.get(this);
	}

	public long lastLogin = System.currentTimeMillis();

	public Farm(int id, int x, int z, OfflinePlayer owner) {
		this(id, x, z);
		this.owner = owner;
	}

	public Farm(int id, int x, int z) {
		this.id = id;
		this.world = Bukkit.getWorlds().get(0);
		this.origin = new Location(world, x, FarmGenerator.ISLAND_HEIGHT, z);

		int i = 0;
		for (CropType c : CropType.values())
			crops[i++] = new Crop(this, c);

		grassInfoSign = new GrassInfoSign(new Location(world, crops[0].getLocation().getBlockX() + 1, cropHeight,
				crops[0].getLocation().getBlockZ() + 10), this, grass);

		grassInfoSign.update();
		infoHologram.update();
	}

	public void gemReset() {
		GEMS.add(this, gemsFromResetting);
		owner.getPlayer().sendMessage(
				ChatColor.GREEN + "Farm reset! you now have " + UEcoFormatter.charFormat(GEMS.get(this), 4) + " gems");
		FarmScoreboards.updateGems(owner.getPlayer());

		ONLINE_TIME.set(this, 0);
		OFFLINE_TIME.set(this, 0);
		EARNINGS.set(this, 0);
		CLICKS_AUTO.set(this, 0);
		CLICKS_MANUAL.set(this, 0);

		for (Element element : Element.values()) {
			SEEDS(element).set(this, 0);
			SEEDS_TOTAL(element).set(this, 0);
		}

		for (Crop crop : crops)
			crop.reset();
		grass.reset();
		grassInfoSign.update();

		PlayerElementDataManager.getInstance().get(owner.getPlayer()).reset();

		UEcoBank.set(owner, 0);

		updateIncome();
		checkEvents();

		Bukkit.getScheduler().runTaskLater(FarmKing.getInstance(), () -> {
			crops[0].generate(world);
		}, 5);
	}

	public void onSecond() {
		updateIncome();
		checkEvents();

		addWorkerSeedsForSeconds(1);

		gemsFromResetting = Math.sqrt(EARNINGS.get(this) / 8000) - GEMS.get(this);
		gemsFromResetting = Math.max(0, Math.floor(gemsFromResetting));

		for (Crop c : crops)
			c.update();
		grassInfoSign.update();
	}

	public void updateIncome() {
		reapplyUpgrades();

		cropIncome = 0;
		for (Crop crop : crops)
			if (crop.isGenerated())
				cropIncome += crop.getIncome();
		cropIncome *= getUpgradeBonus(ONLINE_INCOME) * getGemMultiplier();

		grass.updateIncome();

		WORKERS.set(this, getNumWorkers());
		income = cropIncome + WORKERS.get(this) * grass.getIncomePerClick() * getUpgradeBonus(WORKER_INCOME);
	}

	private void reapplyUpgrades() {
		resetUpgradeBonuses();

		PlayerElementData elementData = PlayerElementDataManager.getInstance().get(owner.getPlayer());
		if (elementData.getElement() != null)
			for (int tier = 0; tier < ElementMetaData.numTiers; tier++)
				for (int upgrade = 0; upgrade < ElementMetaData.upgradesPerTier; upgrade++)
					if (elementData.hasBoughtUpgrade(tier, upgrade))
						applyUpgrade(elementData.getElement().getUpgrade(tier, upgrade));

		for (FarmAchievementSeries achievement : FarmAchievements.getAllAchievements())
			applyUpgrade(achievement.getUpgrade(owner));
	}

	public void addWorkerSeedsForSeconds(long seconds) {
		Map<Element, Double> workerSeeds = grass.getRandomSeeds(WORKERS.get(this) * seconds,
				getUpgradeBonus(WORKER_SEED_MULTIPLIER), getUpgradeBonus(WORKER_SEED_FLAT));
		for (Element element : workerSeeds.keySet())
			SEEDS(element).add(this, workerSeeds.get(element));
	}

	private void resetUpgradeBonuses() {
		upgradeBonuses.clear();
		for (UpgradeBonus type : UpgradeBonus.values())
			upgradeBonuses.put(type, type.isMultiplicative() ? 1D : 0D);
	}

	private void applyUpgrade(Upgrade upgrade) {
		for (int bonusIndex = 0; bonusIndex < upgrade.getNumBonuses(); bonusIndex++)
			applyUpgrade(upgrade.getType(bonusIndex), upgrade.getBonus(bonusIndex, this));
	}

	private void applyUpgrade(UpgradeBonus type, double bonus) {
		if (type.isMultiplicative())
			upgradeBonuses.put(type, upgradeBonuses.get(type) * (1 + bonus));
		else
			upgradeBonuses.put(type, upgradeBonuses.get(type) + bonus);
	}

	private void checkEvents() {
		for (OneTimeEvent event : OneTimeEvent.values())
			if (!completedEvents.contains(event) && event.isComplete(this)) {
				completedEvents.add(event);
				event.onUnlock(this);
			}
	}

	private double getNumWorkers() {
		double workers = 0;
		for (Crop crop : crops)
			if (crop.getQuantity() >= 100)
				workers++;

		workers += upgradeBonuses.get(WORKER_COUNT_FLAT);
		workers *= upgradeBonuses.get(WORKER_COUNT_MULTIPLIER);
		return Math.floor(workers);
	}

	public double getNextGemAmount() {
		return 8000.0 * Math.pow(GEMS.get(this) + 1, 2);
	}

	public void generateCrop(int index) {
		if (index > crops.length - 1)
			return;
		crops[index].generate(world);
	}

	public boolean isIn(Location l) {
		return (l.getBlockX() >= origin.getBlockX() && l.getBlockX() < origin.getBlockX() + FarmGenerator.ISLAND_WIDTH
				&& l.getBlockZ() >= origin.getBlockZ()
				&& l.getBlockZ() < origin.getBlockZ() + FarmGenerator.ISLAND_HEIGHT);
	}

	public double getLevelupGemRequirement(int level) {
		return 1.875E12 * Math.pow(100, (level - 1));
	}

	public int getLevel() {
		return (int) LEVEL.get(this);
	}

	public FarmSchema getSchematic() {
		return FarmGenerator.getSchematicForLevel(getLevel());
	}

	public Location getSpawn() {
		return getSchematic().getSpawn(this);
	}
}
