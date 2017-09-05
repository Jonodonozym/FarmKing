
package jdz.farmKing.farm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.jonodonozym.UPEconomy.UPEconomyAPI;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import jdz.farmKing.crops.Crop;
import jdz.farmKing.main.Main;
import jdz.farmKing.utils.SimpleTime;
import jdz.farmKing.utils.TimedTask;

@SuppressWarnings("deprecation")
public class FarmData {
	public static final int isWidth = 150, isLength = 150, isHeight = 100, spawnBorder = isWidth*2;

	public static int MIN_FARM_BUFFER = 1;
	public static int MAX_FARM_BUFFER = 2;
	public static int FIRST_LOAD_FARM_BUFFER = 2;
	
	public static int currentFarm = 0;
	
	public static List<CuboidClipboard> schematics = new ArrayList<CuboidClipboard>();
	public static List<Location> schemSpawns = new ArrayList<Location>();
	public static List<Integer> schemGrassY = new ArrayList<Integer>();
	
	public static List<Farm> emptyFarms = new ArrayList<Farm>();
	public static Map<String,Farm> playerToFarm = new HashMap<String,Farm>();
	public static Map<String, SimpleTime> lastLogin = new HashMap<String, SimpleTime>();
	
	
	public static void init(){
		Main.plugin.getLogger().info("Respawning tall grass...");
		World world = Main.plugin.getServer().getWorlds().get(0);
		for (Farm farm: emptyFarms)
			farm.respawnTallGrass(world);
		for (Farm farm: playerToFarm.values())
			farm.respawnTallGrass(world);
		
		int FB = MAX_FARM_BUFFER;
		int FB2 = FB - emptyFarms.size();
		if (emptyFarms.isEmpty()){
			Main.plugin.getLogger().info("This is the first time you are running this plugin on this server.");
			Main.plugin.getLogger().info("Lots of farms will be generated to speed up server reboots in the near future. This will take a few hours.");
			Main.plugin.getLogger().info("To disable this feature, force halt this server and set the FIRST_LOAD_FARM_BUFFER to -1 in the config.yml file.");
			FB = Math.max(FIRST_LOAD_FARM_BUFFER,MAX_FARM_BUFFER);
		}
		
		while (emptyFarms.size() < FB)
		{
			Main.plugin.getLogger().info("Generating farm buffer "+(emptyFarms.size()+1)+" of "+FB2);
			FarmData.generateFarm(currentFarm++);
		}
		
		startGeneratorTask();
	}
	
	
	private static void startGeneratorTask(){
		new TimedTask(20, ()->{
				for (Player p: Main.plugin.getServer().getOnlinePlayers())
					if (playerToFarm.containsKey(p.getName())){
						Farm farm = playerToFarm.get(p.getName());
						
						farm.updateIncome();
						UPEconomyAPI.addBalance(p, farm.currentIncome);
						farm.doManualClick(farm.autoClicksPerSecond);
						farm.addStat(StatType.FARM_EARNINGS, farm.currentIncome);
						
						for (Crop c: farm.crops){
							c.updateHologram();
							c.updateSign();
						}
						
						farm.updateGrassSign();
						FarmScoreboards.updateScoreboard(p);
					}
			}).start();

		new TimedTask(1200, ()->{
				for (Player p: Main.plugin.getServer().getOnlinePlayers())
					if (playerToFarm.containsKey(p.getName())){
						Farm farm = playerToFarm.get(p.getName());
						farm.addStat(StatType.FARM_ONLINE_TIME, 1);
						farm.addStat(StatType.FARM_PLAY_TIME, 1);
					}
			}).start();;
	}
	
	
	

	
	public static void generateFarm(int index){
		int row = 0;
		int temp = index;
		while (temp >= 12+row*4){
			temp -= 12+(row++)*4;
		}
		
		int x1 = -spawnBorder-isWidth*(row+1);
		int x2 = spawnBorder+isWidth*row;
		int z1 = -spawnBorder-isHeight*(row+1);
		int z2 = spawnBorder+isHeight*row;
		
		int dx=0, dz=0, x=0, z=0;
		switch(temp/4){
		case 0: dx = 1; x = x1; z = z1; break;
		case 1: dz = 1; x = x2; z = z1; break;
		case 2: dx = -1; x = x2; z = z2; break;
		case 3: dz = -1; x = x1; z = z2; break;
		}
		
		int remainder = (temp/4)*4-temp;
		int xx = x + dx*remainder*isWidth;
		int zz = z + dz*remainder*isLength;
		pasteFarmSchem(xx, zz, Main.plugin.getServer().getWorlds().get(0), 1);
		
		emptyFarms.add(new Farm(xx, zz, true));
	}

	public static void pasteFarmSchem(int X, int Z, World world, int level){
		level = Math.min(schematics.size(), Math.max(1, level));
		CuboidClipboard schem = schematics.get( Math.min(schematics.size()-1, Math.max(0, level-1)));
		Vector location = new Vector(  X, isHeight, Z).subtract(schem.getOffset());
		try { schem.paste(new EditSession(new BukkitWorld(world), 999999999), location, false, false); }
		catch (MaxChangedBlocksException e) { e.printStackTrace(); }
	}
}
