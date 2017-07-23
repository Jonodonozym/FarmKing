package farmKing.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.google.common.io.Files;
import com.jonodonozym.UPEconomy.UPEconomyAPI;

import farmKing.crops.Crop;
import farmKing.crops.CropType;
import farmKing.crops.Farm;
import farmKing.event.*;
import farmKing.utils.JarUtils;
import farmKing.utils.SimpleTime;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	PluginDescriptionFile pdfile = getDescription();
	Logger logger;
	
	// actual farm data
	public List<Farm> emptyFarms = new ArrayList<Farm>();

	public final int spawnBorder = Farm.isWidth*2;
	
	public int MIN_FARM_BUFFER = 1;
	public int MAX_FARM_BUFFER = 2;
	public int FIRST_LOAD_FARM_BUFFER = 2;
	
	public int currentFarm = 0;
	
	public static Map<String,Farm> playerToFarm = new HashMap<String,Farm>();
	
	public Map<String,String> sbBal = new HashMap<String,String>();
	public Map<String,String> sbInc = new HashMap<String,String>();
	public Map<String,String> sbGem = new HashMap<String,String>();
	public static Set<BukkitRunnable> runnables = new HashSet<BukkitRunnable>();
	public static Map<String, SimpleTime> lastLogin = new HashMap<String, SimpleTime>();
	
	public static List<Hologram> holograms = new ArrayList<Hologram>();
	
	public static double costMultiplier = 1.075;
	public static double costMultiplierReduction = 0.010;
	public static int cropPriceBuffer = 16000;
	
	public static void main(String[] args){
		System.out.println(Calendar.getInstance().toString());
	}
	
	@Override
	public void onEnable(){
		logger = getLogger();
		logger.info(pdfile.getName() + " has been enabled (V."+pdfile.getVersion() +")");
		
		extractLibs();
		
		File f = new File(getDataFolder()+File.separator+"Schematics");
		if(!f.exists())
			f.mkdir();
		Farm.loadSchematics(logger, f, getServer().getWorlds().get(0));
		
		Farm.initializeFarmData();
		Crop.initCropData(costMultiplier,cropPriceBuffer);
		
		File file = new File(getDataFolder() + File.separator + "config.yml");
		if(!file.exists())
			this.saveDefaultConfig();
		CropType.initializeCropType(getConfig());
		
		this.getCommand("f").setExecutor(new FarmCommands(this));
		
		registerEvents();

		loadData();
		
		logger.info("Respawning tall grass...");
		World world = getServer().getWorlds().get(0);
		for (Farm farm: emptyFarms)
			farm.respawnTallGrass(world);
		for (Farm farm: playerToFarm.values())
			farm.respawnTallGrass(world);
		
		int FB = MAX_FARM_BUFFER;
		int FB2 = FB - emptyFarms.size() + 1;
		if (emptyFarms.isEmpty()){
			logger.info("This is the first time you are running this plugin on this server.");
			logger.info("Lots of farms will be generated to speed up server reboots in the near future. This will take a few hours.");
			logger.info("To disable this feature, force halt this server and set the FIRST_LOAD_FARM_BUFFER to -1 in the config.yml file.");
			FB = Math.max(FIRST_LOAD_FARM_BUFFER,MAX_FARM_BUFFER);
		}
		
		while (emptyFarms.size() < FB)
		{
			logger.info("Generating farm buffer "+(emptyFarms.size()+1)+" of "+FB2);
			generateFarm(currentFarm++);
		}
		
		new BukkitRunnable(){
			@Override
			public void run(){
				for (Player p: getServer().getOnlinePlayers())
					if (playerToFarm.containsKey(p.getName())){
						Farm farm = playerToFarm.get(p.getName());
						
						farm.updateIncome();
						UPEconomyAPI.addBalance(p, farm.currentIncome);
						farm.doManualClick(farm.autoClicksPerSecond);
						farm.earnings += farm.currentIncome;
						
						for (Crop c: farm.crops){
							c.updateHologram();
							c.updateSign();
						}
						
						farm.updateGrassSign();
						updateScoreboard(p);
					}
			}
		}.runTaskTimer(this, 20, 20);
		
		new BukkitRunnable() {
			@Override
			public void run(){
				for (Player p: getServer().getOnlinePlayers())
					if (playerToFarm.containsKey(p.getName())){
						Farm farm = playerToFarm.get(p.getName());
						farm.onlineTimeMinutes++;
						if (farm.longestPlayTimeMinutes < farm.onlineTimeMinutes + farm.offlineTimeMinutes)
							farm.longestPlayTimeMinutes = farm.onlineTimeMinutes + farm.offlineTimeMinutes;
					}
			}
		}.runTaskTimer(this, 1200, 1200);
	}
	
	@Override
	public void onDisable(){
		for(Player player : Bukkit.getOnlinePlayers()) {
		    player.kickPlayer(ChatColor.RED+"Server shutting down :(");
		}
		
		while (!runnables.isEmpty()) {
			BukkitRunnable r = runnables.iterator().next();
			r.run();
			runnables.remove(r);
		}
		
		for (Hologram h: holograms)
			h.delete();
		
		saveData();
		
		logger.info(pdfile.getName() + " has been disabled (V."+pdfile.getVersion() +")");
	}
	
	public void updateScoreboard(Player player){
		Scoreboard scoreboard = player.getScoreboard();
		
		Objective sb = scoreboard.getObjective("scoreboard");

		scoreboard.resetScores(sbBal.get(player.getName()));
		scoreboard.resetScores(sbInc.get(player.getName()));
		
		String balance = ChatColor.GREEN + "$"+UPEconomyAPI.charFormat(UPEconomyAPI.getBalance(player), 4);
		String income = ChatColor.GREEN + "$"+UPEconomyAPI.charFormat(playerToFarm.get(player.getName()).currentIncome, 4)+"/s";
		
		sb.getScore(balance).setScore(6);
		sb.getScore(income).setScore(3);
		
		sbBal.put(player.getName(), balance);
		sbInc.put(player.getName(), income);
	}
	
	public void registerEvents(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BlockBreak(this), this);
		pm.registerEvents(new ClickEvent(this), this);
		pm.registerEvents(new CropTrample(), this);
		pm.registerEvents(new PlayerJoinQuit(this), this);
		pm.registerEvents(new CropGrowth(), this);
		pm.registerEvents(new BlockPlace(), this);
		pm.registerEvents(new HealthAndHunger(), this);
		pm.registerEvents(new ItemSpawn(), this);
	}
	
	public void loadData(){
		if(!getDataFolder().exists())
			getDataFolder().mkdir();
		File f = new File(getDataFolder(), "Save_Data.txt");
		if (f.exists()){
			try {
				BufferedReader br = new BufferedReader( new FileReader(f));

				currentFarm = Integer.parseInt(br.readLine());
				int numEmptyFarms = Integer.parseInt(br.readLine());
				for (int i=0; i<numEmptyFarms; i++)
					emptyFarms.add(Farm.fromString(this, br.readLine()));
				int numAllFarms = Integer.parseInt(br.readLine());
				for (int i=0; i<numAllFarms; i++){
					playerToFarm.put(br.readLine(), Farm.fromString(this, br.readLine()));
				}
				int numLogins = Integer.parseInt(br.readLine());
				for (int i=0; i<numLogins; i++){
					String player = br.readLine();
					SimpleTime time = SimpleTime.fromString(br.readLine(), ",");
					lastLogin.put(player, time);
				}
				
				br.close();
				logger.info("Loading farm and player data successful!");
				
			} catch (IOException e) { e.printStackTrace();}
		}
	}
	
	public void saveData(){
		if(!getDataFolder().exists())
			getDataFolder().mkdir();
		
		File f = new File(getDataFolder(), "Save_Data.txt");
		if(f.exists()){
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
			int day = now.get(Calendar.DAY_OF_MONTH);
			int hour = now.get(Calendar.HOUR_OF_DAY);
			int minute = now.get(Calendar.MINUTE);
			int second = now.get(Calendar.SECOND);

			String timestamp = String.format("%d-%02d-%02d %02d-%02d-%02d", year, month, day, hour, minute, second);
			
			File backupDir = new File(getDataFolder() + File.separator + "Backups");
			
			if (!backupDir.exists())
				backupDir.mkdir();
			
			try {
				Files.copy(f, new File(backupDir,"Save_Data - "+timestamp+".txt"));
				logger.info("Previous data was backed up successfully.");
			} catch (IOException e) { logger.info("Error: previous data failed to back-up"); }

			f = new File(getDataFolder(), "Save_Data.txt");
		}
		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter(f));
			
			bw.write(""+currentFarm);
			bw.newLine();
			bw.write(""+emptyFarms.size());
			bw.newLine();
			for (Farm farm: emptyFarms){
				bw.write(farm.toString());
				bw.newLine();
			}
			bw.write(""+playerToFarm.keySet().size());
			bw.newLine();
			for (String player: playerToFarm.keySet()){
				bw.write(player);
				bw.newLine();
				bw.write(playerToFarm.get(player).toString());
				bw.newLine();
			}
			bw.write(""+lastLogin.size());
			bw.newLine();
			for (String player: lastLogin.keySet()){
				bw.write(player);
				bw.newLine();
				bw.write(lastLogin.get(player).toString(","));
				bw.newLine();
			}
			
			bw.flush();
			bw.close();
			logger.info("Saving farm and player data successful!");
		} catch (IOException e) { }
	}
	
	public void generateFarm(int index){
		int row = 0;
		int temp = index;
		while (temp >= 12+row*4){
			temp -= 12+(row++)*4;
		}
		
		int x1 = -spawnBorder-Farm.isWidth*(row+1);
		int x2 = spawnBorder+Farm.isWidth*row;
		int z1 = -spawnBorder-Farm.isHeight*(row+1);
		int z2 = spawnBorder+Farm.isHeight*row;
		
		int dx=0, dz=0, x=0, z=0;
		switch(temp/4){
		case 0: dx = 1; x = x1; z = z1; break;
		case 1: dz = 1; x = x2; z = z1; break;
		case 2: dx = -1; x = x2; z = z2; break;
		case 3: dz = -1; x = x1; z = z2; break;
		}
		
		int remainder = (temp/4)*4-temp;
		int xx = x + dx*remainder*Farm.isWidth;
		int zz = z + dz*remainder*Farm.isLength;
		Farm.generate(xx, zz, getServer().getWorlds().get(0), 1);
		emptyFarms.add(new Farm(this, xx, zz, true));
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
