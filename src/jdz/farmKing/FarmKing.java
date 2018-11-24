package jdz.farmKing;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.farmKing.achievements.AchievementInventories;
import jdz.farmKing.command.FarmCommands;
import jdz.farmKing.crops.CropType;
import jdz.farmKing.element.Element;
import jdz.farmKing.element.ElementSelectInventory;
import jdz.farmKing.event.BlockBreak;
import jdz.farmKing.event.BlockPlace;
import jdz.farmKing.event.ClickEvent;
import jdz.farmKing.event.CropGrowth;
import jdz.farmKing.event.CropTrample;
import jdz.farmKing.event.HealthAndHunger;
import jdz.farmKing.event.ItemSpawn;
import jdz.farmKing.event.PlayerJoinQuit;
import jdz.farmKing.event.SwapHandEvent;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.FarmBuffer;
import jdz.farmKing.farm.FarmManager;
import jdz.farmKing.farm.FarmIO;
import lombok.Getter;

public class FarmKing extends JavaPlugin {
	@Getter private static FarmKing instance;
	
	@Override
	public void onEnable(){
		instance = this;
		
		Element.loadData();
		
		File f = new File(getDataFolder()+File.separator+"Schematics");
		if(!f.exists())
			f.mkdir();
		FarmIO.loadSchematics(getLogger(), f, getServer().getWorlds().get(0));

		File file = new File(getDataFolder() + File.separator + "config.yml");
		if(!file.exists())
			this.saveDefaultConfig();
		CropType.initializeCropData(getConfig());
		
		this.getCommand("f").setExecutor(new FarmCommands());
		
		registerEvents();

		Farm.initializeFarmData();
		FarmIO.loadEmptyFarms();
		FarmManager.respawnTallGrass();
		
		FarmBuffer.updateBuffer();
		
		AchievementInventories.reload();
		
		for(Player p: getServer().getOnlinePlayers()){
			PlayerJoinQuit.playerJoinSetup(p);
			FarmIO.load(p);
		}
	}
	
	@Override
	public void onDisable(){	
		for (Player p: getServer().getOnlinePlayers())
			if (FarmManager.playerToFarm.containsKey(p))
				FarmIO.save(FarmManager.playerToFarm.get(p));
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
		pm.registerEvents(new SwapHandEvent(), this);
		pm.registerEvents(new ElementSelectInventory(), this);
	}
	
}
