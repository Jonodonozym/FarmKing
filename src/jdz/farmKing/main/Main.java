package jdz.farmKing.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import jdz.farmKing.achievements.AchievementInventories;
import jdz.farmKing.crops.Crop;
import jdz.farmKing.crops.CropType;
import jdz.farmKing.event.BlockBreak;
import jdz.farmKing.event.BlockPlace;
import jdz.farmKing.event.ClickEvent;
import jdz.farmKing.event.CropGrowth;
import jdz.farmKing.event.CropTrample;
import jdz.farmKing.event.HealthAndHunger;
import jdz.farmKing.event.ItemSpawn;
import jdz.farmKing.event.PlayerJoinQuit;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.FarmData;
import jdz.farmKing.farm.FarmIO;
import jdz.farmKing.utils.JarUtils;

public class Main extends JavaPlugin {
	public static Main plugin;
	
	public static List<Hologram> holograms = new ArrayList<Hologram>();
	
	public static double costMultiplier = 1.075;
	public static double costMultiplierReduction = 0.010;
	public static int cropPriceBuffer = 16000;
	
	@Override
	public void onEnable(){
		plugin = this;
		
		extractLibs();
		
		File f = new File(getDataFolder()+File.separator+"Schematics");
		if(!f.exists())
			f.mkdir();
		FarmIO.loadSchematics(getLogger(), f, getServer().getWorlds().get(0));

		Crop.initCropData(costMultiplier,cropPriceBuffer);
		
		File file = new File(getDataFolder() + File.separator + "config.yml");
		if(!file.exists())
			this.saveDefaultConfig();
		CropType.initializeCropType(getConfig());
		
		this.getCommand("f").setExecutor(new FarmCommands());
		
		registerEvents();
		
		getLogger().info("Respawning tall grass...");

		Farm.initializeFarmData();
		FarmIO.loadEmptyFarms();
		FarmData.init();
		
		AchievementInventories.reload();
		
		for(Player p: getServer().getOnlinePlayers()){
			PlayerJoinQuit.playerJoinSetup(p);
			FarmIO.loadFarm(p);
		}
	}
	
	@Override
	public void onDisable(){		
		for (Hologram h: holograms)
			h.delete();
		
		for (Player p: getServer().getOnlinePlayers())
			FarmIO.saveFarm(FarmData.playerToFarm.get(p));
		FarmIO.saveEmptyFarms();
	}
	
	public void registerEvents(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new ClickEvent(), this);
		pm.registerEvents(new CropTrample(), this);
		pm.registerEvents(new PlayerJoinQuit(), this);
		pm.registerEvents(new CropGrowth(), this);
		pm.registerEvents(new BlockPlace(), this);
		pm.registerEvents(new HealthAndHunger(), this);
		pm.registerEvents(new ItemSpawn(), this);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private void extractLibs(){
        try {
            final File[] libs = new File[] {
                    new File(getDataFolder(), "jnbt-1.1.jar")};
            for (final File lib : libs) {
                if (!lib.exists()) {
                    JarUtils.extractFromJar(lib.getName(),
                            lib.getAbsolutePath());
                }
            }
            for (final File lib : libs) {
                if (!lib.exists()) {
                    getLogger().warning(
                            "There was a critical error loading My plugin! Could not find lib: "
                                    + lib.getName());
                    Bukkit.getServer().getPluginManager().disablePlugin(this);
                    return;
                }
                addClassPath(JarUtils.getJarUrl(lib));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
	}
    
    private void addClassPath(final URL url) throws IOException {
        final URLClassLoader sysloader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
        final Class<URLClassLoader> sysclass = URLClassLoader.class;
        try {
            final Method method = sysclass.getDeclaredMethod("addURL",
                    new Class[] { URL.class });
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { url });
        } catch (final Throwable t) {
            t.printStackTrace();
            throw new IOException("Error adding " + url
                    + " to system classloader");
        }
    }
}
