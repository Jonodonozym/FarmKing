
package jdz.farmKing.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import jdz.UEconomy.events.BalanceChangeEvent;
import jdz.farmKing.FarmKing;
import jdz.farmKing.element.Element;
import jdz.farmKing.stats.types.FarmStat;
import jdz.farmKing.stats.types.FarmStatBuffered;
import jdz.farmKing.stats.types.FarmStatDouble;
import jdz.farmKing.stats.types.FarmStatInt;
import jdz.farmKing.stats.types.FarmStatLinked;
import jdz.farmKing.stats.types.FarmStatTime;
import jdz.statsTracker.event.StatChangeEvent;
import jdz.statsTracker.stats.StatType;
import jdz.statsTracker.stats.StatsManager;
import jdz.statsTracker.stats.abstractTypes.BufferedStatType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FarmStats extends BufferedStatType {
	@Getter public static List<FarmStat> values = getAll();

	private static List<FarmStat> getAll() {
		List<FarmStat> stats = new ArrayList<FarmStat>();
		stats.addAll(Arrays.asList(LEVEL, GEMS, ONLINE_TIME, OFFLINE_TIME, PLAY_TIME, EARNINGS, ALL_SEEDS_TOTAL,
				WORKERS, CLICKS, CLICKS_AUTO, CLICKS_MANUAL));
		stats.addAll(CropAmounts);
		stats.addAll(CropLevels);
		stats.addAll(SeedAmounts.values());
		stats.addAll(SeedTotalAmounts.values());

		List<FarmStat> maxTypes = new ArrayList<FarmStat>();
		for (FarmStat stat : stats)
			if (stat.hasMax())
				maxTypes.add(stat.getMaxType());

		stats.addAll(maxTypes);

		return stats;
	}

	public static void registerAll(FarmKing plugin) {
		StatsManager.getInstance().addTypes(plugin, getValues().toArray(new StatType[0]));
	}

	public static final FarmStatBuffered LEVEL = new FarmStatDouble("Level");
	public static final FarmStatBuffered GEMS = new FarmStatDouble("Gems");

	public static FarmStatBuffered CROP_AMOUNT(int index) {
		return CropAmounts.get(index);
	}

	private static final List<FarmStatBuffered> CropAmounts = new ArrayList<FarmStatBuffered>();
	static {
		for (int i = 0; i < 16; i++)
			CropAmounts.add(new FarmStatDouble("crop " + i, false));
	}
	
	public static FarmStatBuffered CROP_LEVEL(int index) {
		return CropAmounts.get(index);
	}

	private static final List<FarmStatBuffered> CropLevels = new ArrayList<FarmStatBuffered>();
	static {
		for (int i = 0; i < 16; i++)
			CropAmounts.add(new FarmStatInt("crop level " + i, false));
	}

	public static FarmStatBuffered SEEDS(Element element) {
		return SeedAmounts.get(element);
	}

	private static final Map<Element, FarmStatBuffered> SeedAmounts = new HashMap<Element, FarmStatBuffered>();
	static {
		for (Element element : Element.values())
			SeedAmounts.put(element, new FarmStatDouble("seeds " + element.name, false));
	}

	public static final FarmStatBuffered ONLINE_TIME = new FarmStatTime("Online time", false);
	static {
		Bukkit.getScheduler().runTaskTimer(FarmKing.getInstance(), () -> {
			for (Player player : Bukkit.getOnlinePlayers())
				ONLINE_TIME.add(player, 1);
		}, 20, 20);
	};
	public static final FarmStatBuffered OFFLINE_TIME = new FarmStatTime("Offline time", false);
	public static final FarmStatLinked PLAY_TIME = new FarmStatLinked("Play time", false, (f) -> {
		return ONLINE_TIME.get(f) + OFFLINE_TIME.get(f);
	});

	public static final FarmStatBuffered EARNINGS = new FarmStatDouble("Earnings", false) {
		@EventHandler
		public void onBalanceChange(BalanceChangeEvent event) {
			if (event.getOldBalance() < event.getNewBalance())
				add(event.getPlayer(), event.getNewBalance() - event.getOldBalance());
		}
	};

	public static FarmStatBuffered SEEDS_TOTAL(Element element) {
		return SeedTotalAmounts.get(element);
	}

	private static final Map<Element, FarmStatBuffered> SeedTotalAmounts = new HashMap<Element, FarmStatBuffered>();
	static {
		for (final Element element : Element.values())
			SeedTotalAmounts.put(element, new FarmStatDouble("seeds total " + element.name, false) {
				@EventHandler
				public void onStatChange(StatChangeEvent event) {
					if (event.getType() != SEEDS(element))
						return;
					double change = event.getNewValue() - event.getOldValue();
					if (change < 0)
						return;
					add(event.getPlayer(), change);
				}
			});
	}

	public static final FarmStatLinked ALL_SEEDS_TOTAL = new FarmStatLinked("seeds total", false, (f) -> {
		double val = 0;
		for (final Element element : Element.values())
			val += SEEDS_TOTAL(element).get(f);
		return val;
	});

	public static final FarmStatBuffered WORKERS = new FarmStatDouble("Workers", false);

	public static final FarmStatBuffered CLICKS_AUTO = new FarmStatDouble("Clicks Manual", false);
	public static final FarmStatBuffered CLICKS_MANUAL = new FarmStatDouble("Clicks Auto", false);
	public static final FarmStatLinked CLICKS = new FarmStatLinked("Clicks", false, (f) -> {
		return CLICKS_AUTO.get(f) + CLICKS_MANUAL.get(f);
	});
}
