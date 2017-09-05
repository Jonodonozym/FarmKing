
package jdz.farmKing.farm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.DataException;

import jdz.farmKing.crops.Crop;
import jdz.farmKing.crops.CropType;
import jdz.farmKing.element.ElementUpgradeInventory;
import jdz.farmKing.main.Main;
import jdz.farmKing.utils.ErrorLogger;

@SuppressWarnings("deprecation")
public class FarmIO {

	public static void loadSchematics(Logger logger, File f, World world){
		File[] files = f.listFiles();
		if (files != null){
			logger.info(files.length+" schematic files found.");
			for (File file: files)
				if (file.getName().startsWith("farmLevel")){
					try {
						CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
						FarmData.schematics.add(CuboidClipboard.loadSchematic(file));

						loop:
				        for (int x = 0; x < cc.getWidth(); ++x)
				            for (int y = 0; y < cc.getHeight(); ++y)
				                for (int z = 0; z < cc.getLength(); ++z)
				                    if (cc.getBlock(new Vector(x,y,z)).getType() == 7){
				                    	FarmData.schemSpawns.add( new Location(world, x, y , z));
				                    	break loop; 
				                    }
						loop2:
		            	for (int x = 0; x < cc.getWidth(); ++x)
			                for (int z = 0; z < cc.getLength(); ++z)
					            for (int y = 0; y < cc.getHeight(); ++y)
				                    if (cc.getBlock(new Vector(x,y,z)).getType() == Material.GRASS.getId()){
				                    	FarmData.schemGrassY.add( y );
				                    	break loop2; 
				                    }
						System.out.println("[FarmKing] "+FarmData.schemSpawns.size()+" farm schematics loaded.");
						}
					catch (IOException | DataException e) { logger.info("Error loading schematic: "+e); }
				}
		}
		else
			logger.info("Error: no schematic files found.");
	}
	
	public static void saveFarm(Farm farm){
		if(!Main.plugin.getDataFolder().exists())
			Main.plugin.getDataFolder().mkdir();
		
		File f = new File(Main.plugin.getDataFolder(), "Player Data");
		if (!f.exists())
			f.mkdir();
		
		f = new File(f, farm.owner.getName()+".yml");
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		try { toFileConfig(config, farm).save(f); }
		catch (IOException e) { ErrorLogger.createLog(e); }
	}
	
	public static void loadFarm(Player player){
		File f = new File(Main.plugin.getDataFolder()+File.separator+"Player Data"+File.separator+player.getName()+".yml");
		if (f.exists()){
			Farm farm = fromFileConfiguration(YamlConfiguration.loadConfiguration(f)); 
			FarmData.playerToFarm.put(player.getName(), farm);
		}
	}
	
	public static void saveEmptyFarms(){
		if(!Main.plugin.getDataFolder().exists())
			Main.plugin.getDataFolder().mkdir();

		try {
			File f = new File(Main.plugin.getDataFolder(), "Empty Farms.txt");
			if (!f.exists())
				f.createNewFile();

			BufferedWriter bw = new BufferedWriter( new FileWriter(f));
			
			bw.write(""+FarmData.currentFarm);
			bw.newLine();
			bw.write(""+FarmData.emptyFarms.size());
			bw.newLine();
			for (Farm farm: FarmData.emptyFarms){
				bw.write(farm.x+","+farm.z);
				bw.newLine();
			}
			
			bw.flush();
			bw.close();
		} catch (IOException e) {
			ErrorLogger.createLog(e);
		}
	}
	
	public static void loadEmptyFarms(){
		File f = new File(Main.plugin.getDataFolder(), "Empty Farms.txt");
		if (f.exists()){
			try {
				BufferedReader br = new BufferedReader( new FileReader(f));

				FarmData.currentFarm = Integer.parseInt(br.readLine());
				int numEmptyFarms = Integer.parseInt(br.readLine());
				for (int i=0; i<numEmptyFarms; i++){
					String[] xz = br.readLine().split(",");
					FarmData.emptyFarms.add(new Farm(Integer.parseInt(xz[0]), Integer.parseInt(xz[1])));
				}
				
				br.close();
				
			} catch (IOException e) { ErrorLogger.createLog(e);}
		}
	}


	private static FileConfiguration toFileConfig(FileConfiguration config, Farm f) {
		config.set("owner", f.owner==null?"null":f.owner.getName());
		config.set("x", f.x);
		config.set("z", f.z);
		config.set("grassDirectLevel", f.grassDirectLevel);
		config.set("grassPercentLevel", f.grassPercentLevel);
		config.set("buyQuantity", f.buyQuantity);
		config.set("islandLevel", f.isLevel);

		for (EventFlags flag : EventFlags.values())
			config.set("flags." + flag, f.eventIsComplete.get(flag));

		for (StatType stat : StatType.values()) {
			config.set("stats." + stat, f.eventIsComplete.get(stat));
			if (stat.isMax())
				config.set("statsMax." + stat, f.eventIsComplete.get(stat));
			if (stat.isCumulative())
				config.set("statsCumulative." + stat, f.eventIsComplete.get(stat));
		}

		for (Crop c : f.crops)
			config.set("crops." + c.getType().name, c.toString());

		config.set("AUI", f.elementInventory == null ? "null" : f.elementInventory.toString());

		return config;
	}

	private static Farm fromFileConfiguration(FileConfiguration config){		
		Farm f = new Farm(config.getInt("x"), config.getInt("z"), false);
		
		f.updateSpawnLocation();
		
		int i=0;
		for (CropType cropType: CropType.cropTypes)
			f.crops[i] = Crop.fromString(f, cropType, f.getCropLocation(i++), config.getString("crops."+cropType.name));

		if (!config.getString("owner").equals("null"))
			f.owner = Bukkit.getOfflinePlayer(config.getString("owner"));
		
		f.grassDirectLevel = config.getInt("grassDirectLevel");
		f.grassPercentLevel = config.getInt("grassPercentLevel");
		f.buyQuantity = config.getInt("buyQuantity");
		f.isLevel = config.getInt("islandLevel");

		if (!config.getString("AUI").equals("null"))
			f.elementInventory = ElementUpgradeInventory.fromString(config.getString("AUI"), f);
		
		for (String s: config.getConfigurationSection("stats").getKeys(false)){
			StatType type = StatType.valueOf(s.substring(6));
			f.stats.put(type, config.getDouble(s));
		}
		
		for (String s: config.getConfigurationSection("statsMax").getKeys(false)){
			StatType type = StatType.valueOf(s.substring(9));
			f.statsMax.put(type, config.getDouble(s));
		}
		
		for (String s: config.getConfigurationSection("statsCumulative").getKeys(false)){
			StatType type = StatType.valueOf(s.substring(16));
			f.statsCumulative.put(type, config.getDouble(s));
		}
		
		for (String s: config.getConfigurationSection("flags").getKeys(false)){
			EventFlags flag = EventFlags.valueOf(s.substring(6));
			f.eventIsComplete.put(flag, config.getBoolean(s));
		}
			
		return f;
	}
}
