package jdz.farmKing.farm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.jonodonozym.UPEconomy.UPEconomyAPI;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import jdz.farmKing.crops.Crop;
import jdz.farmKing.crops.CropType;
import jdz.farmKing.element.Element;
import jdz.farmKing.element.ElementUpgradeInventory;
import jdz.farmKing.main.Main;
import jdz.farmKing.upgrades.Upgrade;
import jdz.farmKing.utils.Items;

@SuppressWarnings("deprecation")
public class Farm {
	public static final int GRASS_RESPAWN_TIMER = 30;
	public static final int isWidth = 150, isLength = 150, isHeight = 100;
	private static final double gemMultiplier = 0.02;
	private static final double seedFindChanceBase = 0.05, seedFindChanceMultBase = 1;
	private static final double grassValueBase = 0;

	final Map<StatType, Double> stats = new HashMap<StatType, Double>();
	final Map<StatType, Double> statsCumulative = new HashMap<StatType, Double>();
	final Map<StatType, Double> statsMax = new HashMap<StatType, Double>();

	public void setStat(StatType type, double value) {
		stats.put(type, value);
		if (type.isMax() && stats.get(type) > statsMax.get(type))
			statsMax.put(type, stats.get(type));
	}

	public void addStat(StatType type, double amount) {
		stats.put(type, stats.get(type) + amount);
		if (type.isCumulative())
			statsCumulative.put(type, statsCumulative.get(type) + amount);
		if (type.isMax() && stats.get(type) > statsMax.get(type))
			statsMax.put(type, stats.get(type));
	}

	public void multStat(StatType type, double amount) {
		stats.put(type, stats.get(type) * amount);
		if (type.isCumulative())
			statsCumulative.put(type, statsCumulative.get(type) + amount);
		if (type.isMax() && stats.get(type) > statsMax.get(type))
			statsMax.put(type, stats.get(type));
	}

	public double getStat(StatType type) {
		return stats.get(type);
	}

	public double getStatMax(StatType type) {
		if (type.isMax())
			return stats.get(type);
		return 0;
	}

	public double getStatCumulative(StatType type) {
		if (type.isCumulative())
			return stats.get(type);
		return 0.0;
	}

	public final Map<EventFlags, Boolean> eventIsComplete = new HashMap<EventFlags, Boolean>();


	private World world;
	
	// level
	public int isLevel = 1;

	public double globalIncomeMultiplier = 1;

	// gems
	public double gemResetAmount = 0;

	// alignment
	public ElementUpgradeInventory elementInventory = null;

	public OfflinePlayer owner = null;
	public Location spawn;
	public final int x, z;
	private int cropX1, cropX2, cropY, cropZ;

	public Crop[] crops = new Crop[16];

	public double grassValue = 1;

	int grassDirectLevel = 0;
	int grassPercentLevel = 0;
	private static List<Double> grassDirectCost, grassPercentCost, grassDirectBonus, grassPercentBonus;

	public int buyQuantity = 1;
	public double currentIncome = 0;
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

	public Farm(int x, int z, boolean generateCrops) {
		for (StatType statType : StatType.values()) {
			stats.put(statType, 0.0);
			if (statType.isCumulative())
				statsCumulative.put(statType, 0.0);
			if (statType.isMax())
				statsMax.put(statType, 0.0);
		}

		for (EventFlags flagType : EventFlags.values())
			eventIsComplete.put(flagType, false);

		world = Main.plugin.getServer().getWorlds().get(0);
		this.x = x;
		this.z = z;
		updateSpawnLocation();

		if (generateCrops) {
			int i = 0;
			for (CropType c : CropType.cropTypes) {
				crops[i] = new Crop(this, c, getCropLocation(i++));
			}
			miscInit();
		}

		grassInfoSign = new Location(world, cropX1 + 1, cropY, cropZ + 10);

		updateGrassSign();
		updateHologram();
	}

	public Farm(int x, int z) {
		this(x, z, false);
		miscInit();
	}

	private void miscInit() {
		generateCrop(0);
		crops[0].updateHologram();
		Location grassLoc = new Location(world, cropX1 + 3, cropY + 3, cropZ + 10);
		generateGrassFrame(0, 0, grassLoc);
		generateGrassFrame(1, 0, grassLoc.add(0, -1, 0));
	}

	public void gemReset() {
		updateIncome();
		addStat(StatType.FARM_GEMS, gemResetAmount);
		owner.getPlayer().sendMessage(ChatColor.GREEN + "Farm reset! you now have "
				+ UPEconomyAPI.charFormat(getStat(StatType.FARM_GEMS), 4) + " gems");

		// deleting item frames
		int maxX = x + FarmData.schematics.get(isLevel - 1).getWidth();
		int maxZ = z + FarmData.schematics.get(isLevel - 1).getLength();
		List<Entity> entities = world.getEntities();
		for (Entity e : entities)
			if (e instanceof ItemFrame) {
				Location l = e.getLocation();
				if (l.getBlockX() > x && l.getBlockX() < maxX && l.getBlockZ() < maxZ && l.getBlockZ() > x)
					e.remove();
			}

		// re-building farm
		generate(x, z, world, isLevel);

		// clearing data
		for (Crop c : crops)
			if (c != null)
				c.deleteData();
		grassValue = 1;
		grassDirectLevel = 0;
		grassPercentLevel = 0;
		updateIncome();
		setStat(StatType.FARM_EARNINGS, 0);

		elementInventory = null;

		for (int i = 0; i < Element.align.length; i++) {
			StatType st1 = StatType.valueOf("FARM_SEEDS_" + i);
			StatType st2 = StatType.valueOf("FARM_SEEDS_" + i + "_EARNT");
			setStat(st1, 0);
			setStat(st2, 0);
		}

		UPEconomyAPI.setBalance(owner, 0);
		updateSpawnLocation();
		updateGrassSign();

		new BukkitRunnable() {
			@Override
			public void run() {
				miscInit();
			}
		}.runTaskLater(Main.plugin, 5L);

		Scoreboard scoreboard = owner.getPlayer().getScoreboard();
		Objective sb = scoreboard.getObjective("scoreboard");

		scoreboard.resetScores(FarmScoreboards.sbGem.get(owner.getName()));
		String gemsS = ChatColor.GREEN + "" + UPEconomyAPI.charFormat(getStat(StatType.FARM_GEMS), 4);
		sb.getScore(gemsS).setScore(0);
		FarmScoreboards.sbGem.put(owner.getPlayer(), gemsS);

		if (!eventIsComplete.get(EventFlags.ALIGNMENTS_UNLOCKED) && EventFlags.ALIGNMENTS_UNLOCKED.isAchieved(this)) {
			owner.getPlayer()
					.sendMessage(new String[] {
							ChatColor.GREEN + "# " + ChatColor.WHITE
									+ "Now that you've reached 2B gems, you can align with one of the elements!",
							ChatColor.GREEN + "# " + ChatColor.WHITE
									+ "Right-click the bottle to chose an element and see what it does!",
							ChatColor.GREEN + "# " + ChatColor.WHITE
									+ "You can align with a different element every gem reset!" });
			owner.getPlayer().getInventory().setItem(2, Items.alignmentItem);
			eventIsComplete.put(EventFlags.ALIGNMENTS_UNLOCKED, true);
		}
	}

	public void updateIncome() {
		autoClicksPerSecond = 0;
		setStat(StatType.FARM_OFFLINE_BONUS, 0.5);
		grassValue = grassValueBase;
		grassMultiplier = 1;
		seedFindChance = seedFindChanceBase;
		seedFindChanceMult = seedFindChanceMultBase;
		setStat(StatType.FARM_WORKERS, 0);
		workerMult = 1;
		workerProdMult = 0.1;
		workerSeedMult = 0.1;
		workerSeedDirect = 0;
		globalIncomeMultiplier = 1;

		for (Crop c : crops)
			if (c.isGenerated()) {
				c.incomeBonus = 1;
				setStat(StatType.FARM_CROP_QUANTITY_0, c.getQuantity());
				addStat(StatType.FARM_CROP_QUANTITY_TOTAL, c.getQuantity());
				if (elementInventory != null && elementInventory.alignment.getAlignmentCrops().contains(c.getType().name))
					addStat(StatType.FARM_CROP_QUANTITY_ALIGNMENT, c.getQuantity());
			} else
				break;

		if (grassPercentLevel > 0 && currentIncome > 0)
			grassValue = grassPercentBonus.get(grassPercentLevel - 1) * (currentIncome - workerIncome);
		if (grassDirectLevel > 0)
			grassValue += grassDirectBonus.get(grassDirectLevel - 1);

		if (elementInventory != null)
			for (int i = 0; i < Element.numUpgrades; i++)
				if (elementInventory.alignmentUpgradesBought[i])
					applyUpgrade(elementInventory.alignment.getUpgrade(i));

		// insert any more upgrades here

		grassValue *= grassMultiplier;
		seedFindChance *= seedFindChanceMult;

		multStat(StatType.FARM_WORKERS, workerMult);
		workerIncome = doClicks((int) getStat(StatType.FARM_WORKERS),
				grassValue * getStat(StatType.FARM_WORKERS) * workerProdMult,
				(seedFindChance * seedFindChanceMult + workerSeedDirect) * workerSeedMult);

		totalGemMultiplier = gemMultiplier * getStat(StatType.FARM_GEMS) + 1;
		globalIncomeMultiplier *= totalGemMultiplier;

		currentIncome = 0;
		for (Crop c : crops)
			if (c.isGenerated()) {
				c.updateIncome();
				currentIncome += c.currentIncome;
			} else
				break;

		currentIncome *= globalIncomeMultiplier;
		currentIncome += workerIncome;

		gemResetAmount = Math.sqrt(getStat(StatType.FARM_EARNINGS) / 8000) - getStat(StatType.FARM_GEMS);
		if (gemResetAmount < 1)
			gemResetAmount = 0;
		gemResetAmount = Math.floor(gemResetAmount);
	}

	@SuppressWarnings("incomplete-switch")
	private void applyUpgrade(Upgrade u) {
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
				double bonus = u.getBonus(i, this) + 1;
				for (Crop c : crops) {
					if (!c.isGenerated())
						break;
					for (String s : elementInventory.alignment.getAlignmentCrops())
						if (c.getType().name.equals(s)) {
							c.incomeBonus *= bonus;
							break;
						}
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
			case WORKER_COUNT_PERCENT:
				workerMult *= u.getBonus(i, this) + 1;
			case WORKER_PRODUCTION_PERCENT:
				workerProdMult *= u.getBonus(i, this) + 1;
			case WORKER_SEED_DIRECT:
				workerSeedDirect += u.getBonus(i, this) + 1;
			case WORKER_SEED_PERCENT:
				workerSeedMult *= u.getBonus(i, this) + 1;
			}
	}

	public double doClicks(int numClicks, double incomePerClick, double seedMultiplier) {
		addStat(StatType.FARM_CLICKS, numClicks);
		int seedsFound = (int) (seedFindChance * numClicks * seedMultiplier);
		double seedChance = (seedFindChance * numClicks * seedMultiplier) - seedsFound;
		for (int i = 0; i < Element.align.length; i++) {
			int seedsGained = seedsFound;
			if (seedChance > Math.random())
				seedsGained++;

			StatType st1 = StatType.valueOf("FARM_SEEDS_" + i);
			StatType st2 = StatType.valueOf("FARM_SEEDS_" + i + "_EARNT");
			addStat(st1, seedsGained);
			addStat(st2, seedsGained);
		}
		return incomePerClick * numClicks;
	}

	public double doManualClick(int numClicks) {
		UPEconomyAPI.addBalance(owner, grassValue * numClicks);
		return doClicks(numClicks, grassValue, 1);
	}

	public static void generate(int X, int Z, World world, int level) {
		level = Math.min(FarmData.schematics.size(), Math.max(1, level));
		CuboidClipboard schem = FarmData.schematics
				.get(Math.min(FarmData.schematics.size() - 1, Math.max(0, level - 1)));
		Vector location = new Vector(X, isHeight, Z).subtract(schem.getOffset());
		try {
			schem.paste(new EditSession(new BukkitWorld(world), 999999999), location, false, false);
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}

	public void generateCrop(int index) {
		index = Math.max(0, Math.min(index, crops.length - 1));
		crops[index].generateStuff(world);
	}

	public void updateCropPlants(int index) {
		int q = crops[index].getQuantity();
		if (q > 350)
			return;
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
				}
				world.createExplosion(x, yy, zz + 2, 0, false, false);
			}
			x += dx;
		}
	}

	public void clickItemFrame(ItemFrame itemFrame) {
		Location l = itemFrame.getLocation();
		int i = 0;
		for (Crop c : crops) {
			Location cL = c.getLocation();
			if (l.getBlockZ() >= cL.getBlockZ() - 2 && l.getBlockZ() <= cL.getBlockZ() + 2) {
				if (i < 8) {
					if (l.getBlockX() > cL.getBlockX()) {
						c.clickItemFrame(itemFrame, cL);
						return;
					}
				} else if (l.getBlockX() < cL.getBlockX()) {
					c.clickItemFrame(itemFrame, cL);
					return;
				}
			}
			i++;
		}

		// otherwise check if it's a grass upgrade
		if (l.getBlockX() <= cropX1 + 3 && l.getBlockX() >= cropX1 - 1) {
			if (l.getBlockY() == cropY + 3) {
				if (grassDirectLevel == 5)
					return;
				if (l.getBlockX() == cropX1 + 3 - grassDirectLevel) {
					if (UPEconomyAPI.hasEnough(owner, grassDirectCost.get(grassDirectLevel))) {
						UPEconomyAPI.subBalance(owner, grassDirectCost.get(grassDirectLevel));
						grassDirectLevel++;
						Location nextLoc = new Location(world, l.getBlockX() - 1, l.getBlockY(), l.getBlockZ());
						generateGrassFrame(0, grassDirectLevel, nextLoc);
						setUpgradePurchased(itemFrame);
					} else
						owner.getPlayer()
								.sendMessage(ChatColor.RED + "You don't have enough money to purchase that upgrade!");
				}
			}
			if (l.getBlockY() == cropY + 2) {
				if (grassPercentLevel == 5)
					return;
				if (l.getBlockX() == cropX1 + 3 - grassPercentLevel) {
					if (UPEconomyAPI.hasEnough(owner, grassPercentCost.get(grassPercentLevel))) {
						UPEconomyAPI.subBalance(owner, grassPercentCost.get(grassPercentLevel));
						grassPercentLevel++;
						Location nextLoc = new Location(world, l.getBlockX() - 1, l.getBlockY(), l.getBlockZ());
						generateGrassFrame(1, grassPercentLevel, nextLoc);
						setUpgradePurchased(itemFrame);
					} else
						owner.getPlayer()
								.sendMessage(ChatColor.RED + "You don't have enough money to purchase that upgrade!");
				}
			}
		}

		// otherwise, must be a faction upgrade
	}

	public void setUpgradePurchased(ItemFrame itemFrame) {
		ItemStack i = new ItemStack(Material.WOOL, 1, (short) 13);
		ItemMeta iM = i.getItemMeta();
		iM.setDisplayName(ChatColor.GREEN + "Upgrade Purchased!");
		i.setItemMeta(iM);
		itemFrame.setItem(i);
	}

	private void generateGrassFrame(int row, int level, Location l) {
		if (level >= 5)
			return;
		String nextCost;
		String nextBonus;
		if (row == 0) {
			nextCost = UPEconomyAPI.charFormat(grassDirectCost.get(level), 4);
			nextBonus = UPEconomyAPI.charFormat(grassDirectBonus.get(level) + 1, 4);
		} else {
			nextCost = UPEconomyAPI.charFormat(grassPercentCost.get(level), 4);
			nextBonus = UPEconomyAPI.charFormat(grassPercentBonus.get(level) * 100, 4);
		}
		ItemFrame nextUpgradeFrame;
		try {
			nextUpgradeFrame = world.spawn(l, ItemFrame.class);
		} catch (IllegalArgumentException e) {
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
			s.setLine(0, getStat(StatType.FARM_CLICKS) + " Clicks");
			s.setLine(3, "$" + UPEconomyAPI.charFormat(grassValue, 4));
			s.update();
		}
	}

	public void updateHologram() {
		if (owner == null)
			return;
		if (hologram == null) {
			Location l = new Location(world, spawn.getBlockX(), spawn.getBlockY() + 3, spawn.getBlockZ() + 3);
			hologram = HologramsAPI.createHologram(Main.plugin, l);
			Main.holograms.add(hologram);
			hologram.appendTextLine(ChatColor.YELLOW + "FARM DETAILS");
			hologram.appendTextLine("");
			line1 = hologram.appendTextLine("");
			line2 = hologram.appendTextLine("");
			hologram.appendTextLine("");
			hologram.appendTextLine(ChatColor.GREEN + "Bonuses from farm level " + isLevel + ":");
			hologram.appendTextLine(ChatColor.RED + "None");
			hologram.appendTextLine("");
			hologram.appendTextLine(
					ChatColor.GREEN + "Gems required for level up: " + UPEconomyAPI.charFormat(getGemReq(1), 4));

		}

		line1.setText(ChatColor.GREEN + "Multiplier from gems: " + ChatColor.YELLOW + "x"
				+ UPEconomyAPI.charFormat(totalGemMultiplier, 4));
		line2.setText(ChatColor.GREEN + "Gems gained from resetting: " + ChatColor.YELLOW
				+ UPEconomyAPI.charFormat(gemResetAmount, 4));
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

	public void respawnTallGrass(World world) {
		int level = Math.min(FarmData.schematics.size(), Math.max(1, isLevel));
		int xMax = x + FarmData.schematics.get(level - 1).getWidth();
		int zMax = z + FarmData.schematics.get(level - 1).getLength();
		int yHeight = FarmData.schemGrassY.get(level - 1);
		for (int x = this.x; x < xMax; x++)
			for (int z = this.z; z < zMax; z++)
				if (world.getBlockAt(x, yHeight, z).getType() == Material.GRASS) {
					Block b = world.getBlockAt(x, yHeight + 1, z);
					b.setType(Material.LONG_GRASS);
					b.setData((byte) 1);
				}
	}

	public void updateSpawnLocation() {
		int idx = Math.min(FarmData.schematics.size() - 1, Math.max(0, isLevel - 1));

		Location l = FarmData.schemSpawns.get(idx);
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

	// data based
	public static void initializeFarmData() {
		grassDirectCost = new ArrayList<Double>();
		grassDirectCost.add(500.00);
		grassDirectCost.add(2500.00);
		grassDirectCost.add(12500.00);
		grassDirectCost.add(75000.00);
		grassDirectCost.add(40000.00);
		grassDirectBonus = new ArrayList<Double>();
		grassDirectBonus.add(1.0);
		grassDirectBonus.add(4.0);
		grassDirectBonus.add(14.0);
		grassDirectBonus.add(39.0);
		grassDirectBonus.add(99.0);

		grassPercentCost = new ArrayList<Double>();
		grassPercentCost.add(2.4E4);
		grassPercentCost.add(7.48E6);
		grassPercentCost.add(3.62E9);
		grassPercentCost.add(5.75E11);
		grassPercentCost.add(1.22E14);
		grassPercentBonus = new ArrayList<Double>();
		grassPercentBonus.add(0.02);
		grassPercentBonus.add(0.04);
		grassPercentBonus.add(0.06);
		grassPercentBonus.add(0.08);
		grassPercentBonus.add(0.10);
	}
}
