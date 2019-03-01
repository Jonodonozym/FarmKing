package jdz.farmKing;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.interactableObject.InteractableObjectFactory;
import jdz.farmKing.achievements.AchievementInventories;
import jdz.farmKing.command.FarmCommandExecutor;
import jdz.farmKing.crops.CropBuySign;
import jdz.farmKing.crops.CropType;
import jdz.farmKing.crops.CropUpgradeFrame;
import jdz.farmKing.element.ElementMetaData;
import jdz.farmKing.element.data.PlayerElementDataManager;
import jdz.farmKing.element.gui.ElementSelectInventory;
import jdz.farmKing.element.gui.ElementUpgradeInventory;
import jdz.farmKing.farm.FarmIncomeGenerator;
import jdz.farmKing.farm.FarmScoreboards;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.farm.generation.FarmBuffer;
import jdz.farmKing.farm.grass.GrassBreakListener;
import jdz.farmKing.farm.grass.GrassUpgradeFrame;
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
		CropType.loadFromConfig(getConfig());

		new FarmCommandExecutor(this).register();

		registerEvents();

		FarmBuffer.fetchBuffer();
		PlayerFarms.respawnTallGrass();

		FarmBuffer.updateBuffer();

		AchievementInventories.reload();

		for (Player p : getServer().getOnlinePlayers()) {
			PlayerJoinQuit.playerJoinSetup(p);
			PlayerFarms.get(p);
		}
	}

	public void registerEvents() {
		new InventoryProtector().registerEvents(this);
		new Invincibility().registerEvents(this);
		new PlayerJoinQuit().registerEvents(this);
		new WorldGuard().registerEvents(this);
		new FarmScoreboards().registerEvents(this);
		new FarmIncomeGenerator().registerEvents(this);
		new GrassBreakListener().registerEvents(this);
	}
	
	public void registerInteractables() {
		new InteractableObjectFactory<CropBuySign>().register(this);
		new InteractableObjectFactory<CropUpgradeFrame>().register(this);
		new InteractableObjectFactory<GrassUpgradeFrame>().register(this);
	}

}
