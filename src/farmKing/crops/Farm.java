package farmKing.crops;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
import com.sk89q.worldedit.world.DataException;

import farmKing.alignment.Alignment;
import farmKing.alignment.AlignmentUpgradeInventory;
import farmKing.main.Items;
import farmKing.main.Main;
import farmKing.upgrades.Upgrade;


@SuppressWarnings("deprecation")
public class Farm {
	public static final int GRASS_RESPAWN_TIMER = 30;
	public static final int isWidth = 150, isLength = 150, isHeight = 100;
	
	public static List<CuboidClipboard> schematics = new ArrayList<CuboidClipboard>();
	public static List<Location> schemSpawns = new ArrayList<Location>();
	public static List<Integer> schemGrassY = new ArrayList<Integer>();

	Main plugin;
	World world;
	
	// level
	public int isLevel = 1;
	
	// money
	public double earnings = 0, totalEarnings = 0;
	
	// time
	public int onlineTimeMinutes = 0, offlineTimeMinutes = 0;
	public int totalOnlineTimeMinutes = 0, totalOfflineTimeMinutes = 0;
	public int longestPlayTimeMinutes = 0;
	
	public double globalIncomeMultiplier = 1;

	// gems
	public double gems = 0;
	public double gemMultiplier = 0.02,  totalGemMultiplier = 1;
	public double gemResetAmount = 0;
	
	
	// seeds
	public double seedFindChance = 0.05;
	public double seedFindChanceMult = 1;
	public double[] seeds = new double[4], seedsEarnt = new double[4], totalSeedsEarnt = new double[4];
	
	// workers
	public int workers = 0 ,prevWorkers = 0, maxWorkers = 0;
	public int workerMult = 1;
	public double workerProdMult = 1, workerSeedDirect = 0, workerSeedMult = 0.1, workerIncome = 0;
	
	// alignment
	public AlignmentUpgradeInventory AUI = null;

	public OfflinePlayer owner = null;
	public Location spawn;
	public final int x, z;
	private int cropX1, cropX2, cropY, cropZ;
	
	public Crop[] crops = new Crop[16];
	
	public double grassValue = 1;
	public double grassMultiplier = 1;
	public int clicks = 0, totalClicks = 0, autoClicksPerSecond = 0;
	
	public int grassDirectLevel = 0;
	public int grassPercentLevel = 0;
	public static List<Double> grassDirectCost, grassPercentCost, grassDirectBonus, grassPercentBonus;
	
	
	public int buyQuantity = 1;
	public double currentIncome = 0;
	
	public int numPlants = 0;
	
	public double offlineBonus = 0.5;

	private Location grassInfoSign;
	private Hologram hologram;
	private TextLine line1, line2;
	
	public Farm(Main plugin, int x, int z, boolean generateCrops){
		this.plugin = plugin;
		world = plugin.getServer().getWorlds().get(0);
		this.x = x;
		this.z = z;
		updateSpawnLocation();
		
		if (generateCrops){
			int i=0;
			for (CropType c: CropType.cropTypes){
				crops[i] = new Crop(this, c, getCropLocation(i++), plugin);
			}
			miscInit();
		}
		
		grassInfoSign = new Location(world,cropX1+1,cropY,cropZ+10);
		
		updateGrassSign();
		updateHologram();
	}

	public Farm(Main plugin, int x, int z){
		this(plugin,x,z,false);
		miscInit();
	}
	
	private void miscInit(){
		generateCrop(0);
		crops[0].updateHologram();
		Location grassLoc = new Location(world, cropX1+3,cropY+3, cropZ+10);
		generateGrassFrame(0,0,grassLoc);
		generateGrassFrame(1,0,grassLoc.add(0,-1,0));
	}
	
	public void gemReset(){
		updateIncome();
		gems += gemResetAmount;
		owner.getPlayer().sendMessage(ChatColor.GREEN+"Farm reset! you now have "+UPEconomyAPI.charFormat(gems, 4)+" gems");
		
		
		//deleting item frames
		int maxX = x + schematics.get(isLevel-1).getWidth();
		int maxZ = z + schematics.get(isLevel-1).getLength();
		List<Entity> entities = world.getEntities();
		for (Entity e: entities)
			if (e instanceof ItemFrame){
				Location l = e.getLocation();
				if (l.getBlockX() > x && l.getBlockX() < maxX && l.getBlockZ() < maxZ && l.getBlockZ() > x )
						e.remove();
			}
		
		//re-building farm
		generate(x, z, world, isLevel);
		
		//clearing data
		for (Crop c: crops)
			if (c != null) c.deleteData();
		grassValue = 1;
		grassDirectLevel = 0;
		grassPercentLevel = 0;
		updateIncome();
		earnings = 0;
		
		AUI = null;
		
		for (int i=0; i<seeds.length; i++){
			seeds[i] = 0;
			seedsEarnt[i] = 0;
		}
		
		UPEconomyAPI.setBalance(owner, 0);
		updateSpawnLocation();
		updateGrassSign();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				miscInit();
			}
		}.runTaskLater(plugin, 5L);

		Scoreboard scoreboard = owner.getPlayer().getScoreboard();
		Objective sb = scoreboard.getObjective("scoreboard");
		
		scoreboard.resetScores(plugin.sbGem.get(owner.getName()));
		String gemsS = ChatColor.GREEN + ""+UPEconomyAPI.charFormat(gems, 4);
		sb.getScore(gemsS).setScore(0);
		plugin.sbGem.put(owner.getName(),gemsS);
		
		if (gems > 2e9 && isLevel == 0 && owner.getPlayer().getInventory().getItem(2).getType() != Material.EXP_BOTTLE){
			owner.getPlayer().sendMessage(new String[]{
					ChatColor.GREEN+"# "+ChatColor.WHITE+"Now that you've reached 2B gems, you can align with one of the elements!",
					ChatColor.GREEN+"# "+ChatColor.WHITE+"Right-click the bottle to chose an element and see what it does!",
					ChatColor.GREEN+"# "+ChatColor.WHITE+"You can align with a different element every gem reset!"
			});
			owner.getPlayer().getInventory().setItem(2, Items.alignmentItem);
		}
	}
	
	public void updateIncome(){
		autoClicksPerSecond = 0;
		offlineBonus = 0.5;
		gemMultiplier = 0.02;
		grassValue = 1;
		grassMultiplier = 1;
		seedFindChance = 0.05;
		seedFindChanceMult = 1;
		numPlants = 0;
		prevWorkers = workers;
		if (workers > maxWorkers)
			maxWorkers = workers;
		workers = 0;
		workerMult = 1;
		workerProdMult = 0.1;
		workerSeedMult = 0.1;

		for (Crop c: crops)
			if (c.isGenerated()){
				c.incomeBonus = 1;
				numPlants += c.getQuantity();
			}
			else
				break;

		if (grassPercentLevel > 0 && currentIncome > 0)
			grassValue = grassPercentBonus.get(grassPercentLevel-1) * (currentIncome-workerIncome);
		if (grassDirectLevel > 0)
			grassValue += grassDirectBonus.get(grassDirectLevel-1);
		
		if (AUI != null)
		for (int i=0; i<Alignment.numUpgrades; i++)
			if (AUI.alignmentUpgradesBought[i])
				applyUpgrade(AUI.alignment.getUpgrade(i));

		// insert any more upgrades here
		
		grassValue *= grassMultiplier;
		seedFindChance *= seedFindChanceMult;

		workers *= workerMult;
		workerIncome = doClicks(workers, grassValue * workers * workerProdMult, (seedFindChance * seedFindChanceMult + workerSeedDirect)*workerSeedMult);
		
		totalGemMultiplier = gemMultiplier * gems + 1;
		globalIncomeMultiplier *= totalGemMultiplier;
		
		currentIncome = 0;
		for (Crop c: crops)
			if (c.isGenerated()){
				c.updateIncome();
				currentIncome += c.currentIncome;
			}
			else
				break;

		currentIncome *= globalIncomeMultiplier;
		currentIncome += workerIncome;
		
		gemResetAmount = Math.sqrt(earnings/8000) - gems;
		if (gemResetAmount < 1)
			gemResetAmount = 0;
		gemResetAmount = Math.floor(gemResetAmount);
	}

	@SuppressWarnings("incomplete-switch")
	private void applyUpgrade(Upgrade u){
		for (int i=0; i<u.getNumBonuses(); i++)
			switch(u.getType(i)){
			case CLICK_DIRECT:
				grassValue += u.getBonus(i, this);
				break;
			case AUTO_CLICKS:
				autoClicksPerSecond += u.getBonus(i, this);
				break;
			case CLICK_PERCENT:
				grassMultiplier *= u.getBonus(i, this)+1;
				break;
			case MAIN_CROP_PERCENT:
				double bonus = u.getBonus(i, this)+1;
				for (Crop c: crops){
					if (!c.isGenerated())
						break;
					for (String s: AUI.alignment.getAlignmentCrops())
						if (c.getType().name.equals(s)){
							c.incomeBonus *= bonus;
							break;
						}
				}
				break;
			case OFFLINE_PRODUCTION_PERCENT:
				offlineBonus *= u.getBonus(i, this) + 1;
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
				workers += u.getBonus(i, this);
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

	public double doClicks(int numClicks, double incomePerClick, double seedMultiplier){
		clicks += numClicks;
		totalClicks += numClicks;
		int seedsFound = (int)(seedFindChance * numClicks * seedMultiplier);
		double seedChance = (seedFindChance * numClicks * seedMultiplier) - seedsFound;
		for (int i=0; i<seeds.length; i++){
			int seedsGained = seedsFound;
			if (seedChance > Math.random())
				seedsGained++;
			seeds[i] += seedsGained;
			seedsEarnt[i] += seedsGained;
			totalSeedsEarnt[i] += seedsGained;
		}
		return incomePerClick * numClicks;
	}
	
	public double doManualClick(int numClicks){
		UPEconomyAPI.addBalance(owner, grassValue * numClicks);
		return doClicks(numClicks, grassValue, 1);
	}
	
	public static void generate(int X, int Z, World world, int level){
		level = Math.min(schematics.size(), Math.max(1, level));
		CuboidClipboard schem = schematics.get( Math.min(schematics.size()-1, Math.max(0, level-1)));
		Vector location = new Vector(  X, isHeight, Z).subtract(schem.getOffset());
		try { schem.paste(new EditSession(new BukkitWorld(world), 999999999), location, false, false); }
		catch (MaxChangedBlocksException e) { e.printStackTrace(); }
	}
	
	public void generateCrop(int index){
		index = Math.max(0, Math.min(index, crops.length-1));
		crops[index].generateStuff(plugin.getServer().getWorlds().get(0));	
	}
	
	public void updateCropPlants(int index){
		int q = crops[index].getQuantity();
		if (q > 350) return;
		Location l = crops[index].getLocation();
		CropType ct = crops[index].getType();
		int xx=l.getBlockX(), zz=l.getBlockZ()-2, yy = cropY, dx=0;
		int dev = Math.min(q/20, 12)+1;
		int zMax = zz+4;
		dx = (index < 8 ?  -1:  1);
		xx += dx*3;
		int x = xx;
		for (int i=0; i<dev; i++){
			Block b = world.getBlockAt(x, yy, zz);
			if (b.getType() == Material.AIR){
				for (int z=zz; z <= zMax; z+= 1){
					b = world.getBlockAt(x, yy, z);
					b.setType(ct.material);
					b.setData(ct.data);
					world.getBlockAt(x, yy-1, z).setType(ct.base);
					if (ct.material == Material.SUGAR_CANE_BLOCK){
						if ((zz - z)%2 == 0)
							world.getBlockAt(x, yy+1, z).setType(Material.SUGAR_CANE_BLOCK);
						else{
							b.setType(Material.AIR);
							world.getBlockAt(x, yy-1, z).setType(Material.STATIONARY_WATER);
						}
					}
				}
				world.createExplosion(x, yy, zz+2, 0, false, false);
			}
			x += dx;
		}
	}
	
	public void clickItemFrame(ItemFrame itemFrame){
		Location l = itemFrame.getLocation();
		int i=0;
		for (Crop c: crops){
			Location cL = c.getLocation();
			if (l.getBlockZ() >= cL.getBlockZ()-2 && l.getBlockZ() <= cL.getBlockZ()+2){
				if (i < 8){
					if (l.getBlockX() > cL.getBlockX()){
						c.clickItemFrame(itemFrame, cL);
						return;
					}
				}
				else if (l.getBlockX() < cL.getBlockX()){
					c.clickItemFrame(itemFrame, cL);
					return;
				}
			}
			i++;
		}
		
		//otherwise check if it's a grass upgrade
		if (l.getBlockX() <= cropX1 + 3 && l.getBlockX() >= cropX1-1){
			if (l.getBlockY() == cropY+3){
				if (grassDirectLevel == 5) return;
				if (l.getBlockX() == cropX1 + 3 - grassDirectLevel){
					if (UPEconomyAPI.hasEnough(owner, grassDirectCost.get(grassDirectLevel))){
						UPEconomyAPI.subBalance(owner, grassDirectCost.get(grassDirectLevel));
						grassDirectLevel++;
						Location nextLoc = new Location(world, l.getBlockX()-1,l.getBlockY(),l.getBlockZ());
						generateGrassFrame(0,grassDirectLevel, nextLoc);
						setUpgradePurchased(itemFrame);
					}
					else owner.getPlayer().sendMessage(ChatColor.RED+"You don't have enough money to purchase that upgrade!");
				}
			}
			if (l.getBlockY() == cropY+2){
				if (grassPercentLevel == 5) return;
				if (l.getBlockX() == cropX1 + 3 - grassPercentLevel){
					if (UPEconomyAPI.hasEnough(owner, grassPercentCost.get(grassPercentLevel))){
						UPEconomyAPI.subBalance(owner, grassPercentCost.get(grassPercentLevel));
						grassPercentLevel++;
						Location nextLoc = new Location(world, l.getBlockX()-1,l.getBlockY(),l.getBlockZ());
						generateGrassFrame(1,grassPercentLevel, nextLoc);
						setUpgradePurchased(itemFrame);
					}
					else owner.getPlayer().sendMessage(ChatColor.RED+"You don't have enough money to purchase that upgrade!");
				}
			}
		}
		
		//otherwise, must be a faction upgrade
	}
	
	public void setUpgradePurchased(ItemFrame itemFrame){
		ItemStack i = new ItemStack(Material.WOOL, 1, (short)13);
		ItemMeta iM = i.getItemMeta();
		iM.setDisplayName(ChatColor.GREEN+"Upgrade Purchased!");
		i.setItemMeta(iM);
		itemFrame.setItem(i);
	}
	
	private void generateGrassFrame(int row, int level, Location l){
		if (level >= 5) return;
		String nextCost;
		String nextBonus;
		if (row == 0){
			nextCost = UPEconomyAPI.charFormat( grassDirectCost.get(level),4);
			nextBonus = UPEconomyAPI.charFormat( grassDirectBonus.get(level) + 1,4);
		}
		else {
			nextCost = UPEconomyAPI.charFormat( grassPercentCost.get(level),4);
			nextBonus = UPEconomyAPI.charFormat( grassPercentBonus.get(level) * 100,4);
		}
		ItemFrame nextUpgradeFrame = world.spawn(l, ItemFrame.class);
		ItemStack i = new ItemStack(Material.WOOL, 1, (short)14);
		ItemMeta iM = i.getItemMeta();
		if (row == 0)
			iM.setDisplayName(ChatColor.RED+"+"+nextBonus+"  $"+nextCost);
		else
			iM.setDisplayName(ChatColor.RED+"+"+nextBonus+"%  $"+nextCost);
		i.setItemMeta(iM);
		nextUpgradeFrame.setItem(i);
		nextUpgradeFrame.setFacingDirection(BlockFace.NORTH);
	}
	
	public void updateGrassSign(){
		if (owner == null) return;
		Block block = world.getBlockAt(grassInfoSign);
		if (block.getType() != Material.WALL_SIGN){
			block.setType(Material.WALL_SIGN);
			block.setData( (byte)2 );
			Sign s = (Sign) block.getState();
			s.setLine(2, "$ per click:");
			s.update();
		}
		if (block.getType() == Material.WALL_SIGN){
			Sign s = (Sign) block.getState();
			s.setLine(0, clicks+" Clicks");
			s.setLine(3, "$"+UPEconomyAPI.charFormat(grassValue, 4));
			s.update();
		}
	}
	
	public void updateHologram(){
		if (owner == null) return;
		if (hologram == null){
			Location l = new Location(world, spawn.getBlockX(), spawn.getBlockY()-3, spawn.getBlockZ()+3);
			hologram = HologramsAPI.createHologram(plugin, l);
			Main.holograms.add(hologram);
			hologram.appendTextLine(ChatColor.YELLOW+"FARM DETAILS");
			hologram.appendTextLine("");
			line1 = hologram.appendTextLine("");
			line2 = hologram.appendTextLine("");
			hologram.appendTextLine("");
			hologram.appendTextLine(ChatColor.GREEN+"Bonuses from farm level "+isLevel+":");
			hologram.appendTextLine(ChatColor.RED+"None");
			hologram.appendTextLine("");
			hologram.appendTextLine(ChatColor.GREEN+"Gems required for level up: "+UPEconomyAPI.charFormat(getGemReq(1),4));
			
		}

		line1.setText(ChatColor.GREEN+"Multiplier from gems: "+ChatColor.YELLOW+"x" + UPEconomyAPI.charFormat(totalGemMultiplier, 4));
		line2.setText(ChatColor.GREEN+"Gems gained from resetting: " +ChatColor.YELLOW + UPEconomyAPI.charFormat(gemResetAmount, 4));
	}
	
	
	
	
	
	
	
	

	//helper methods
	public Location getCropLocation(int index){
		World world = plugin.getServer().getWorlds().get(0);
		if (index < 8)
			return new Location(world, cropX1, cropY, cropZ - index*6);
		return new Location(world, cropX2, cropY, cropZ - (crops.length-1-index)*6);
	}

	public boolean isIn(Location l){
		return ( l.getBlockX() > x && l.getBlockX() < x + Farm.isWidth &&
				 l.getBlockZ() > z && l.getBlockZ() < z + Farm.isLength  );
	}

	public void respawnTallGrass(World world){
		int level = Math.min(schematics.size(), Math.max(1, isLevel));
		int xMax = x + schematics.get(level-1).getWidth();
		int zMax = z + schematics.get(level-1).getLength();
		int yHeight = schemGrassY.get(level-1);
		for (int x = this.x; x < xMax; x++)
			for (int z = this.z; z < zMax; z++)
				if ( world.getBlockAt(x, yHeight, z).getType() == Material.GRASS){
					Block b = world.getBlockAt(x, yHeight+1, z);
					b.setType(Material.LONG_GRASS);
					b.setData((byte)1);
				}
	}
	
	public void updateSpawnLocation(){
		int idx = Math.min(schematics.size()-1, Math.max(0, isLevel-1));
		
		Location l = schemSpawns.get(idx);
		spawn = new Location(l.getWorld(), l.getX()+x+0.5, l.getY()+isHeight+1, l.getZ()+z+0.5,180,0);
		
		// works out crop locations based on spawn
		cropY = spawn.getBlockY()-3;
		cropZ = spawn.getBlockZ()-7;
		cropX1 = spawn.getBlockX()+16;
		cropX2 = spawn.getBlockX()-16;
	}
	
	public double getGemReq(int level){
		return 1.875E12 * Math.pow(100 , (level - 1));
	}
	
	//data based
	public static void initializeFarmData(){
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
	
	@Override
	public String toString(){
		String ownerName = owner==null? "null player" : owner.getName();
		String retString = x+","+z+","+ownerName+","+
				grassDirectLevel+","+grassPercentLevel+","+
				clicks+","+totalClicks+","+buyQuantity+","+
				UPEconomyAPI.charFormat(earnings, 10)+","+
				UPEconomyAPI.charFormat(gems, 10)+","+
				offlineTimeMinutes+","+onlineTimeMinutes+","+
				totalOfflineTimeMinutes+","+totalOnlineTimeMinutes+","+
				longestPlayTimeMinutes+","+
				seeds[0]+","+seeds[1]+","+seeds[2]+","+seeds[3]+","+
				seedsEarnt[0]+","+seedsEarnt[1]+","+seedsEarnt[2]+","+seedsEarnt[3]+","+
				totalSeedsEarnt[0]+","+totalSeedsEarnt[1]+","+totalSeedsEarnt[2]+","+totalSeedsEarnt[3]+","+
				isLevel+","+
				earnings+","+totalEarnings;
		for (Crop c: crops)
			if (c != null) retString = retString+"|"+c.toString();

		retString = retString+"|"+AUI == null? "null":AUI.toString();
		return retString;
	}
	
	public static Farm fromString(Main plugin, String s){
		String[] lines = s.split("\\|");
		String[] args = lines[0].split(",");
		Farm f = new Farm(plugin, Integer.parseInt(args[0]), Integer.parseInt(args[1]), false);
		f.updateSpawnLocation();
		for (int i=0; i<f.crops.length; i++)
			f.crops[i] = (Crop.fromString(f, plugin, CropType.cropTypes.get(i), f.getCropLocation(i), lines[i+1]));
			
		try {
		if (args[2] != "null player")
			f.owner = plugin.getServer().getOfflinePlayer(args[2]);
			f.grassDirectLevel = Integer.parseInt(args[3]);
			f.grassPercentLevel = Integer.parseInt(args[4]);
			f.clicks = Integer.parseInt(args[5]);
			f.totalClicks = Integer.parseInt(args[6]);
			f.buyQuantity = Integer.parseInt(args[7]);
			f.earnings = Double.parseDouble(UPEconomyAPI.charToEngr(args[8]));
			f.gems = Double.parseDouble(UPEconomyAPI.charToEngr(args[9]));
			f.offlineTimeMinutes = Integer.parseInt(args[10]);
			f.onlineTimeMinutes = Integer.parseInt(args[11]);
			f.totalOfflineTimeMinutes = Integer.parseInt(args[12]);
			f.totalOnlineTimeMinutes = Integer.parseInt(args[13]);
			f.longestPlayTimeMinutes = Integer.parseInt(args[14]);
			f.seeds[0] = Double.parseDouble(args[15]);
			f.seeds[1] = Double.parseDouble(args[16]);
			f.seeds[2] = Double.parseDouble(args[17]);
			f.seeds[3] = Double.parseDouble(args[18]);
			f.seedsEarnt[0] = Double.parseDouble(args[19]);
			f.seedsEarnt[1] = Double.parseDouble(args[20]);
			f.seedsEarnt[2] = Double.parseDouble(args[21]);
			f.seedsEarnt[3] = Double.parseDouble(args[22]);
			f.totalSeedsEarnt[0] = Double.parseDouble(args[23]);
			f.totalSeedsEarnt[1] = Double.parseDouble(args[24]);
			f.totalSeedsEarnt[2] = Double.parseDouble(args[25]);
			f.totalSeedsEarnt[3] = Double.parseDouble(args[26]);
			f.isLevel = Integer.parseInt(args[27]);
			f.earnings = Double.parseDouble(args[28]);
			f.totalEarnings = Double.parseDouble(args[29]);
	
			if (!lines[lines.length-1].equals("null"))
				f.AUI = AlignmentUpgradeInventory.fromString(lines[lines.length-1], f);
		}
		catch (IndexOutOfBoundsException e) {}
		
		return f;
	}
	
	public static void loadSchematics(Logger logger, File f, World world){
		File[] files = f.listFiles();
		if (files != null){
			logger.info(files.length+" schematic files found.");
			for (File file: files)
				if (file.getName().startsWith("farmLevel")){
					try {
						CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
						schematics.add(CuboidClipboard.loadSchematic(file));

						loop:
				        for (int x = 0; x < cc.getWidth(); ++x)
				            for (int y = 0; y < cc.getHeight(); ++y)
				                for (int z = 0; z < cc.getLength(); ++z)
				                    if (cc.getBlock(new Vector(x,y,z)).getType() == 7){
				                    	schemSpawns.add( new Location(world, x, y , z));
				                    	break loop; 
				                    }
						loop2:
		            	for (int x = 0; x < cc.getWidth(); ++x)
			                for (int z = 0; z < cc.getLength(); ++z)
					            for (int y = 0; y < cc.getHeight(); ++y)
				                    if (cc.getBlock(new Vector(x,y,z)).getType() == Material.GRASS.getId()){
				                    	schemGrassY.add( y );
				                    	break loop2; 
				                    }
						System.out.println("[FarmKing] "+schemSpawns.size()+" farm schematics loaded.");
						}
					catch (IOException | DataException e) { logger.info("Error loading schematic: "+e); }
				}
		}
		else
			logger.info("Error: no schematic files found.");
	}
}
