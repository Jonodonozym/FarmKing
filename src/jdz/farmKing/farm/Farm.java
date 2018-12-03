package jdz.farmKing.farm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import static jdz.UEconomy.UEcoFormatter.*;

import jdz.UEconomy.UEcoFormatter;
import jdz.UEconomy.data.UEcoBank;
import jdz.farmKing.FarmKing;
import jdz.farmKing.HologramManager;
import jdz.farmKing.achievements.Achievement;
import jdz.farmKing.achievements.AchievementData;
import jdz.farmKing.crops.Crop;
import jdz.farmKing.crops.CropType;
import jdz.farmKing.element.Element;
import jdz.farmKing.element.ElementUpgradeInventory;
import jdz.farmKing.farm.data.FarmDB;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.farm.grass.Grass;
import jdz.farmKing.stats.EventFlag;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.upgrades.Upgrade;
import jdz.farmKing.utils.BuyAmount;
import lombok.Getter;

@SuppressWarnings("deprecation")
public class Farm {
	public static final int GRASS_RESPAWN_TIMER = 30;
	public static final int isWidth = 150, isLength = 150, isHeight = 100;
	private static final double gemMultiplier = 0.02;
	private static final double seedFindChanceBase = 0.05, seedFindChanceMultBase = 1;
	private static final double grassValueBase = 1;

	@Getter private final int id;

	private World world;

	// level
	public int level = 1;

	public double globalIncomeMultiplier = 1;

	// gems3
	public double gemResetAmount = 0;

	// alignment
	public ElementUpgradeInventory elementInventory = null;

	@Getter private OfflinePlayer owner = null;

	public void setOwner(OfflinePlayer player) {
		FarmDB.getInstance().setOwner(this, player);
	}

	public Location spawn;
	public final int x, z;
	private int cropX1, cropX2, cropY, cropZ;

	@Getter public Crop[] crops = new Crop[16];
	@Getter private Grass grass = new Grass(this);

	public double grassValue = 1;

	int grassDirectLevel = 0;
	int grassPercentLevel = 0;
	private static List<Double> grassDirectCost, grassPercentCost, grassDirectBonus, grassPercentBonus;

	public BuyAmount buyQuantity = BuyAmount.BUY_1;
	public double currentIncome = 0, cropIncome = 0;
	private double workerIncome = 0;

	private Location grassInfoSign;
	private Hologram hologram;
	private TextLine line1, line2;
	public int autoClicksPerSecond = 0;
	private int grassMultiplier;
	private double seedFindChance;
	private double seedFindChanceMult;
	private int workerMult;
	private double workerProdMult;
	private double workerSeedMult;
	private int workerSeedDirect;
	private double totalGemMultiplier;
	public long lastLogin = System.currentTimeMillis();

	public Farm(int id, int x, int z, OfflinePlayer owner) {
		this(id, x, z, false);
		this.owner = owner;
	}

	public Farm(int id, int x, int z, boolean generateCrops) {
		this.id = id;

		for (Element e : Element.elements.values())
			seeds.put(e, 0.0);

		world = Bukkit.getWorlds().get(0);
		this.x = x;
		this.z = z;
		updateSpawnLocation();

		int i = 0;
		for (CropType c : CropType.cropTypes)
			crops[i] = new Crop(this, c, getCropLocation(i++));
		if (generateCrops)
			miscInit(generateCrops);

		grassInfoSign = new Location(world, cropX1 + 1, cropY, cropZ + 10);

		updateGrassSign();
		updateHologram();
	}

	private void miscInit(boolean generateCrops) {
		if (generateCrops)
			generateCrop(0);
		crops[0].updateHologram();
		Location grassLoc = new Location(world, cropX1 + 3, cropY + 3, cropZ + 10);
		generateGrassFrame(0, 0, grassLoc);
		generateGrassFrame(1, 0, grassLoc.add(0, -1, 0));
	}

	public void gemReset() {
		boolean hadAlignments = EventFlag.ALIGNMENTS_UNLOCKED.isComplete(this);

		updateIncome();
		
		FarmStats.GEMS.set(farm, gemResetAmount);
		owner.getPlayer().sendMessage(ChatColor.GREEN + "Farm reset! you now have "
				+ UEcoFormatter.charFormat(getStat(StatType.FARM_GEMS), 4, true) + " gems");

		// deleting item frames
		int maxX = x + PlayerFarms.schematics.get(level - 1).getWidth();
		int maxZ = z + PlayerFarms.schematics.get(level - 1).getLength();
		List<Entity> entities = world.getEntities();
		for (Entity e : entities)
			if (e instanceof ItemFrame) {
				Location l = e.getLocation();
				if (l.getBlockX() > x && l.getBlockX() < maxX && l.getBlockZ() < maxZ && l.getBlockZ() > x)
					e.remove();
			}

		// re-building farm
		generate(x, z, world, level);

		// clearing data
		for (Crop c : crops)
			if (c != null)
				c.deleteData();
		grassValue = grassValueBase;
		grassDirectLevel = 0;
		grassPercentLevel = 0;
		updateIncome();

		FarmStats.EARNINGS.set(this, 0);

		elementInventory = null;

		for (Element element : Element.elements.values()) {
			FarmStats.SEEDS(element).set(this, 0);
			FarmStats.SEEDS_TOTAL(element).set(this, 0);
		}

		UEcoBank.set(owner, 0);
		updateSpawnLocation();
		updateGrassSign();

		Bukkit.getScheduler().runTaskLater(FarmKing.getInstance(), () -> {
			miscInit(true);
		}, 5);

		FarmScoreboards.updateGems(owner.getPlayer());

		if (!hadAlignments && EventFlag.ALIGNMENTS_UNLOCKED.isComplete(this))
			EventFlag.ALIGNMENTS_UNLOCKED.onUnlock(this);
	}

	public double updateIncome() {
		autoClicksPerSecond = 0;
		setStat(StatType.FARM_OFFLINE_BONUS, 0.5);
		grassValue = grassValueBase;
		grassMultiplier = 1;
		seedFindChance = seedFindChanceBase;
		seedFindChanceMult = seedFindChanceMultBase;
		workerMult = 1;
		workerProdMult = 0.1;
		workerSeedMult = 0.1;
		workerSeedDirect = 0;
		globalIncomeMultiplier = 1;

		setStat(StatType.FARM_WORKERS, 0);
		setStat(StatType.FARM_CROP_QUANTITY_TOTAL, 0);
		setStat(StatType.FARM_CROP_QUANTITY_ALIGNMENT, 0);

		int i = 0;
		for (Crop c : crops) {
			StatType stat = StatType.valueOf("FARM_CROP_QUANTITY_" + (i++));
			if (c.isGenerated()) {
				c.CMI = 0;
				c.incomeBonus = 1;
				setStat(stat, c.getQuantity());
				addStat(StatType.FARM_CROP_QUANTITY_TOTAL, c.getQuantity());
				if (elementInventory != null && elementInventory.element.crops.contains(c.getType().name))
					addStat(StatType.FARM_CROP_QUANTITY_ALIGNMENT, c.getQuantity());
				if (c.getQuantity() >= 100)
					addStat(StatType.FARM_WORKERS, 1);
			}
			else
				setStat(stat, 0);
		}

		if (grassPercentLevel > 0 && currentIncome > 0)
			grassValue += grassPercentBonus.get(grassPercentLevel - 1) * (cropIncome);
		if (grassDirectLevel > 0)
			grassValue += grassDirectBonus.get(grassDirectLevel - 1);

		if (elementInventory != null)
			for (i = 0; i < Element.numUpgrades; i++)
				if (elementInventory.upgradesBought[i])
					applyUpgrade(elementInventory.element.getUpgrade(i));

		for (Achievement a : AchievementData.achToUpgrade.keySet())
			if (AchievementData.isAchieved.get(owner.getPlayer()).get(a))
				applyUpgrade(a.upgrade);

		// insert any more upgrades here

		if (!eventIsComplete.get(EventFlag.WORKERS_UNLOCKED) && EventFlag.WORKERS_UNLOCKED.isComplete(this))
			EventFlag.WORKERS_UNLOCKED.onUnlock(this);

		grassValue *= grassMultiplier;
		seedFindChance *= seedFindChanceMult;

		multStat(StatType.FARM_WORKERS, workerMult);

		totalGemMultiplier = gemMultiplier * getStat(StatType.FARM_GEMS) + 1;
		globalIncomeMultiplier *= totalGemMultiplier;

		workerIncome = doClicks(Math.floor(getStat(StatType.FARM_WORKERS)), grassValue * workerProdMult,
				(seedFindChance * seedFindChanceMult + workerSeedDirect) * workerSeedMult)[0];

		cropIncome = 0;
		for (Crop c : crops)
			if (c.isGenerated()) {
				c.updateIncome();
				cropIncome += c.currentIncome;
			}
			else
				break;

		cropIncome *= globalIncomeMultiplier;
		currentIncome = cropIncome + workerIncome;

		doAutoClicks(autoClicksPerSecond);

		gemResetAmount = Math.sqrt(getStat(StatType.FARM_EARNINGS) / 8000) - getStat(StatType.FARM_GEMS);
		if (gemResetAmount < 1)
			gemResetAmount = 0;
		gemResetAmount = Math.floor(gemResetAmount);
		
		return currentIncome;
	}

	public double getNextGemAmount() {
		return 8000.0 * Math.pow(getStat(StatType.FARM_GEMS) + 1, 2);
	}

	// @SuppressWarnings("incomplete-switch")
	private void applyUpgrade(Upgrade u) {
		double bonus = 0;
		for (int i = 0; i < u.getNumBonuses(); i++)
			switch (u.getType(i)) {
			case CLICK_DIRECT:
				grassValue += u.getBonus(i, this);
				break;
			case AUTO_CLICKS:
				autoClicksPerSecond += u.getBonus(i, this);
				break;
			case CLICK_PERCENT:
				grassMultiplier *= u.getBonus(i, this) + 1;
				break;
			case ALIGNMENT_CROP_PERCENT:
				bonus = u.getBonus(i, this) + 1;
				for (Crop c : crops) {
					if (!c.isGenerated())
						break;
					if (elementInventory.element.crops.contains(c.getType().name))
						c.incomeBonus *= bonus;
				}
				break;
			case OFFLINE_PRODUCTION_PERCENT:
				multStat(StatType.FARM_OFFLINE_BONUS, u.getBonus(i, this) + 1);
				break;
			case ONLINE_PRODUCTION_PERCENT:
				globalIncomeMultiplier *= u.getBonus(i, this) + 1;
				break;
			case SEED_DIRECT:
				seedFindChance += u.getBonus(i, this);
				break;
			case SEED_PERCENT:
				seedFindChanceMult *= u.getBonus(i, this) + 1;
				break;
			case WORKER_COUNT_DIRECT:
				addStat(StatType.FARM_WORKERS, u.getBonus(i, this));
				break;
			case WORKER_COUNT_PERCENT:
				workerMult *= u.getBonus(i, this) + 1;
				break;
			case WORKER_PRODUCTION_PERCENT:
				workerProdMult *= u.getBonus(i, this) + 1;
				break;
			case WORKER_SEED_DIRECT:
				workerSeedDirect += u.getBonus(i, this) + 1;
				break;
			case WORKER_SEED_PERCENT:
				workerSeedMult *= u.getBonus(i, this) + 1;
				break;
			case ALL_CROPS_PERCENT:
				bonus = u.getBonus(i, this) + 1;
				for (Crop c : crops) {
					if (!c.isGenerated())
						break;
					if (elementInventory.element.crops.contains(c.getType().name))
						c.incomeBonus *= bonus;
				}
				break;
			case ALL_CROP_COST_MULTIPLIER:
				for (Crop c : crops) {
					if (!c.isGenerated())
						break;
					c.CMI++;
				}
			case MAIN_CROP_COST_MULTIPLIER:
				for (Crop c : crops) {
					if (!c.isGenerated())
						break;
					if (elementInventory.element.crops.contains(c.getType().name))
						c.CMI++;
				}
			case SEED_OVER_TIME:
				double seeds = Math.floor(u.getBonus(i, this));
				for (Element element : Element.elements.values())
					addSeeds(element, seeds);
				break;
			}
	}

	public double[] doClicks(double d, double incomePerClick, double seedMultiplier) {
		double[] retDouble = new double[Element.elements.size() + 1];
		retDouble[0] = incomePerClick * d;
		UEconomyAPI.addBalance(owner, grassValue * d);
		addStat(StatType.FARM_EARNINGS, grassValue * d);
		addStat(StatType.FARM_CLICKS, d);

		if (eventIsComplete.get(EventFlag.ALIGNMENTS_UNLOCKED)) {
			int seedsFound = (int) (seedFindChance * d * seedMultiplier);
			double seedChance = (seedFindChance * d * seedMultiplier) - seedsFound;
			int i = 1;
			for (Element e : Element.elements.values()) {
				int seedsGained = seedsFound;
				if (seedChance > Math.random())
					seedsGained++;

				if (elementInventory != null && elementInventory.element.equals(e))
					addStat(StatType.FARM_ELEMENT_SEEDS_EARNT, seedsGained);
				addStat(StatType.FARM_SEEDS_EARNT, seedsGained);
				addSeeds(e, seedsGained);
				retDouble[i++] = seedsGained;
			}
		}
		return retDouble;
	}

	public double[] doAutoClicks(double numClicks) {
		addStat(StatType.FARM_CLICKS_AUTO, numClicks);
		return doClicks(numClicks, grassValue, 1000);
	}

	public double[] doManualClick(double numClicks) {
		addStat(StatType.FARM_CLICKS_MANUAL, numClicks);
		return doClicks(numClicks, grassValue, 1000);
	}

	public static void generate(int X, int Z, World world, int level) {
		level = Math.min(PlayerFarms.schematics.size(), Math.max(1, level));
		CuboidClipboard schem = PlayerFarms.schematics
				.get(Math.min(PlayerFarms.schematics.size() - 1, Math.max(0, level - 1)));
		Vector location = new Vector(X, isHeight, Z).subtract(schem.getOffset());
		try {
			schem.paste(new EditSession(new BukkitWorld(world), 999999999), location, false, false);
		}
		catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}

	public void generateCrop(int index) {
		index = Math.max(0, Math.min(index, crops.length - 1));
		crops[index].generateStuff(world);
	}

	public void updateCropPlants(int index) {
		int q = crops[index].getQuantity();
		Location l = crops[index].getLocation();
		CropType ct = crops[index].getType();
		int xx = l.getBlockX(), zz = l.getBlockZ() - 2, yy = cropY, dx = 0;
		int dev = Math.min(q / 20, 12) + 1;
		int zMax = zz + 4;
		dx = (index < 8 ? -1 : 1);
		xx += dx * 3;
		int x = xx;
		for (int i = 0; i < dev; i++) {
			Block b = world.getBlockAt(x, yy, zz);
			if (b.getType() == Material.AIR) {
				for (int z = zz; z <= zMax; z += 1) {
					b = world.getBlockAt(x, yy, z);
					b.setType(ct.material);
					b.setData(ct.data);
					world.getBlockAt(x, yy - 1, z).setType(ct.base);
					if (ct.material == Material.SUGAR_CANE_BLOCK) {
						if ((zz - z) % 2 == 0)
							world.getBlockAt(x, yy + 1, z).setType(Material.SUGAR_CANE_BLOCK);
						else {
							b.setType(Material.AIR);
							world.getBlockAt(x, yy - 1, z).setType(Material.STATIONARY_WATER);
						}
					}
					world.createExplosion(x, yy, zz, 0, false, false);
				}
			}
			x += dx;
		}
	}

	public Crop getCrop(Location location) {
		int i = 0;

		for (Crop c : crops) {
			Location cL = c.getLocation();
			if (location.getBlockZ() >= cL.getBlockZ() - 2 && location.getBlockZ() <= cL.getBlockZ() + 2) {
				if (i < 8) {
					if (location.getBlockX() > cL.getBlockX())
						return c;
				}
				else if (location.getBlockX() < cL.getBlockX())
					return c;
			}
			i++;
		}

		return null;
	}

	public void clickItemFrame(ItemFrame itemFrame) {
		Location l = itemFrame.getLocation();

		Crop crop = getCrop(l);
		if (crop != null) {
			crop.clickItemFrame(itemFrame);
			return;
		}

		// otherwise check if it's a grass upgrade
		if (l.getBlockX() <= cropX1 + 3 && l.getBlockX() >= cropX1 - 1) {
			if (l.getBlockY() == cropY + 3) {
				if (grassDirectLevel == 5)
					return;
				if (l.getBlockX() == cropX1 + 3 - grassDirectLevel) {
					if (UEcoBank.has(owner, grassDirectCost.get(grassDirectLevel))) {
						UEcoBank.subtract(owner, grassDirectCost.get(grassDirectLevel));
						grassDirectLevel++;
						Location nextLoc = new Location(world, l.getBlockX() - 1, l.getBlockY(), l.getBlockZ());
						generateGrassFrame(0, grassDirectLevel, nextLoc);
						setUpgradePurchased(itemFrame);
					}
					else
						owner.getPlayer()
								.sendMessage(ChatColor.RED + "You don't have enough money to purchase that upgrade!");
				}
			}
			if (l.getBlockY() == cropY + 2) {
				if (grassPercentLevel == 5)
					return;
				if (l.getBlockX() == cropX1 + 3 - grassPercentLevel) {
					if (UEcoBank.has(owner, grassPercentCost.get(grassPercentLevel))) {
						UEcoBank.subtract(owner, grassPercentCost.get(grassPercentLevel));
						grassPercentLevel++;
						Location nextLoc = new Location(world, l.getBlockX() - 1, l.getBlockY(), l.getBlockZ());
						generateGrassFrame(1, grassPercentLevel, nextLoc);
						setUpgradePurchased(itemFrame);
					}
					else
						owner.getPlayer()
								.sendMessage(ChatColor.RED + "You don't have enough money to purchase that upgrade!");
				}
			}
		}

		// otherwise, must be a faction upgrade
	}

	private void generateGrassFrame(int row, int level, Location l) {
		if (level >= 5)
			return;
		String nextCost;
		String nextBonus;
		if (row == 0) {
			nextCost = charFormat(grassDirectCost.get(level), 4);
			nextBonus = charFormat(grassDirectBonus.get(level) + 1, 4);
		}
		else {
			nextCost = charFormat(grassPercentCost.get(level), 4);
			nextBonus = charFormat(grassPercentBonus.get(level) * 100, 4);
		}
		ItemFrame nextUpgradeFrame;
		try {
			nextUpgradeFrame = world.spawn(l, ItemFrame.class);
		}
		catch (IllegalArgumentException e) {
			nextUpgradeFrame = (ItemFrame) world.getNearbyEntities(l, 1, 1, 1).iterator().next();
		}
		ItemStack i = new ItemStack(Material.WOOL, 1, (short) 14);
		ItemMeta iM = i.getItemMeta();
		if (row == 0)
			iM.setDisplayName(ChatColor.RED + "+" + nextBonus + "  $" + nextCost);
		else
			iM.setDisplayName(ChatColor.RED + "+" + nextBonus + "%  $" + nextCost);
		i.setItemMeta(iM);
		nextUpgradeFrame.setItem(i);
		nextUpgradeFrame.setFacingDirection(BlockFace.NORTH);
	}

	public void updateGrassSign() {
		if (owner == null)
			return;
		Block block = world.getBlockAt(grassInfoSign);
		if (block.getType() != Material.WALL_SIGN) {
			block.setType(Material.WALL_SIGN);
			block.setData((byte) 2);
			Sign s = (Sign) block.getState();
			s.setLine(2, "$ per click:");
			s.update();
		}
		if (block.getType() == Material.WALL_SIGN) {
			Sign s = (Sign) block.getState();
			s.setLine(0, makeWhole(charFormat(getStat(StatType.FARM_CLICKS_MANUAL), 4)) + " Clicks");
			s.setLine(3, "$" + charFormat(grassValue, 4));
			s.update();
		}
	}

	public void updateHologram() {
		if (owner == null)
			return;
		if (hologram == null) {
			Location l = spawn.clone().add(0, 3, 3);
			hologram = HologramManager.make(owner, l);
			hologram.appendTextLine(ChatColor.YELLOW + "FARM DETAILS");
			hologram.appendTextLine("");
			line1 = hologram.appendTextLine("");
			line2 = hologram.appendTextLine("");
			hologram.appendTextLine("");
			hologram.appendTextLine(ChatColor.GREEN + "Bonuses from farm level " + level + ":");
			hologram.appendTextLine(ChatColor.RED + "None");
			hologram.appendTextLine("");
			hologram.appendTextLine(
					ChatColor.GREEN + "Gems required for level up: " + makeWhole(charFormat(getGemReq(1), 4)));
		}

		line1.setText(ChatColor.GREEN + "Multiplier from gems: " + ChatColor.YELLOW + "x"
				+ charFormat(totalGemMultiplier, 4));
		line2.setText(ChatColor.GREEN + "Gems gained from resetting: " + ChatColor.YELLOW
				+ makeWhole(charFormat(gemResetAmount, 4)));
	}

	// helper methods
	public Location getCropLocation(int index) {
		if (index < 8)
			return new Location(world, cropX1, cropY, cropZ - index * 6);
		return new Location(world, cropX2, cropY, cropZ - (crops.length - 1 - index) * 6);
	}

	public boolean isIn(Location l) {
		return (l.getBlockX() > x && l.getBlockX() < x + Farm.isWidth && l.getBlockZ() > z
				&& l.getBlockZ() < z + Farm.isLength);
	}

	public void updateSpawnLocation() {
		int idx = Math.min(PlayerFarms.schematics.size() - 1, Math.max(0, level - 1));

		Location l = PlayerFarms.schemSpawns.get(idx);
		spawn = new Location(l.getWorld(), l.getX() + x + 0.5, l.getY() + isHeight + 1, l.getZ() + z + 0.5, 180, 0);

		// works out crop locations based on spawn
		cropY = spawn.getBlockY() - 3;
		cropZ = spawn.getBlockZ() - 7;
		cropX1 = spawn.getBlockX() + 16;
		cropX2 = spawn.getBlockX() - 16;
	}

	public double getGemReq(int level) {
		return 1.875E12 * Math.pow(100, (level - 1));
	}
}
