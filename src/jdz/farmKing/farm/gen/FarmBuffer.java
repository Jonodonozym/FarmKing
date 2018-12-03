
package jdz.farmKing.farm.gen;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import jdz.bukkitUtils.config.AutoConfig;
import jdz.bukkitUtils.config.NotConfig;
import jdz.farmKing.FarmKing;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.data.FarmDB;
import jdz.farmKing.farm.data.PlayerFarms;

public class FarmBuffer extends AutoConfig {
	protected FarmBuffer(FarmKing plugin) {
		super(plugin, "Farm Buffer");
	}

	private static final int FIRST_LOAD_BUFFER_SIZE = 2;
	private static final int REBOOT_BUFFER_SIZE = 2;

	@NotConfig private static List<Farm> bufferedFarms = new LinkedList<Farm>();
	@NotConfig private static Logger logger = Logger.getGlobal();
	
	public static void fetchBuffer() {
		bufferedFarms = FarmDB.getInstance().loadUnownedFarms();
	}

	public static void updateBuffer() {
		int currentFarm = bufferedFarms.size() + PlayerFarms.getPlayerFarms().size();

		int targetBufferedFarms = currentFarm == 0 ? FIRST_LOAD_BUFFER_SIZE : REBOOT_BUFFER_SIZE;

		int i = 0;
		int farmsToGen = targetBufferedFarms - bufferedFarms.size();
		logger.info("Buffering " + farmsToGen + "farms");
		while (bufferedFarms.size() < targetBufferedFarms) {
			logger.info("Generating farm " + (++i) + " of " + farmsToGen);
			bufferedFarms.add(FarmGenerator.generate(currentFarm++));
		}
		logger.info("Buffering " + farmsToGen + "farms completed");
	}

	public static Farm removeFirst() {
		if (bufferedFarms.size() > 0)
			return bufferedFarms.remove(0);
		return FarmGenerator.generate(PlayerFarms.getPlayerFarms().size());
	}
}
