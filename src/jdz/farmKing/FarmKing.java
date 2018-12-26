package jdz.farmKing;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.farmKing.achievements.AchievementInventories;
import jdz.farmKing.command.FarmCommandExecutor;
import jdz.farmKing.crops.CropType;
import jdz.farmKing.element.ElementMetaData;
import jdz.farmKing.element.data.PlayerElementDataManager;
import jdz.farmKing.element.gui.ElementSelectInventory;
import jdz.farmKing.element.gui.ElementUpgradeInventory;
import jdz.farmKing.farm.FarmIncomeGenerator;
import jdz.farmKing.farm.FarmScoreboards;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.farm.gen.FarmBuffer;
import jdz.farmKing.farm.grass.GrassData;
import jdz.farmKing.listeners.InventoryProtector;
import jdz.farmKing.listeners.Invincibility;
import jdz.farmKing.listeners.PlayerJoinQuit;
import jdz.farmKing.listeners.WorldGuard;
import jdz.farmKing.stats.FarmStats;
import lombok.Getter;

public class FarmKing extends JavaPlugin {
	@Getter private static FarmKing instance;

	@Override
	public void onEnable() {
		instance = this;

		FarmStats.registerAll(this);

		ElementMetaData.load(this);
		PlayerElementDataManager.getInstance().registerEvents(this);
		ElementSelectInventory.getInstance();
		ElementUpgradeInventory.getInstance();

		File file = new File(getDataFolder() + File.separator + "config.yml");
		if (!file.exists())
			this.saveDefaultConfig();
		CropType.initializeCropData(getConfig());

		new FarmCommandExecutor(this).register();

		registerEvents();

		GrassData.init();
		FarmBuffer.fetchBuffer();
		PlayerFarms.respawnTallGrass();

		FarmBuffer.updateBuffer();

		AchievementInventories.reload();

		for (Player p : getServer().getOnlinePlayers()) {
			PlayerJoinQuit.playerJoinSetup(p);
			FarmIO.load(p);
		}
	}

	public void registerEvents() {
		new InventoryProtector().registerEvents(this);
		new Invincibility().registerEvents(this);
		new PlayerJoinQuit().registerEvents(this);
		new WorldGuard().registerEvents(this);
		new FarmScoreboards().registerEvents(this);
		new FarmIncomeGenerator().registerEvents(this);
	}

}
