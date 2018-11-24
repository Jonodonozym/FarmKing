
package jdz.farmKing.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import jdz.farmKing.FarmKing;
import jdz.farmKing.element.Element;
import jdz.farmKing.stats.types.FarmStat;
import jdz.farmKing.stats.types.FarmStatBuffered;
import jdz.farmKing.stats.types.FarmStatDouble;
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
				WORKERS, CLICKS, CLICKS_AUTO, CLICKS_MANUAL, OFFLINE_BONUS));
		stats.addAll(CropQuantityTypes);
		stats.addAll(CurrentSeedTypes.values());
		stats.addAll(totalSeeds.values());

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

	public static final FarmStatBuffered LEVEL = new FarmStatDouble("Farm Level");
	public static final FarmStatBuffered GEMS = new FarmStatDouble("Farm Gems");

	public static FarmStatBuffered CROP_TYPE(int index) {
		return CropQuantityTypes.get(index);
	}

	private static final List<FarmStatBuffered> CropQuantityTypes = new ArrayList<FarmStatBuffered>();
	static {
		for (int i = 0; i < 16; i++)
			CropQuantityTypes.add(new FarmStatDouble("Farm crop " + i, false));
	}

	public static FarmStatBuffered SEEDS(Element element) {
		return CurrentSeedTypes.get(element);
	}

	private static final Map<Element, FarmStatBuffered> CurrentSeedTypes = new HashMap<Element, FarmStatBuffered>();
	static {
		for (Element element : Element.elements.values())
			CurrentSeedTypes.put(element, new FarmStatDouble("Farm seeds " + element.name, false));
	}

	public static final FarmStatBuffered ONLINE_TIME = new FarmStatTime("Farm Online Playtime", false);
	static {
		Bukkit.getScheduler().runTaskTimer(FarmKing.getInstance(), () -> {
			for (Player player : Bukkit.getOnlinePlayers())
				ONLINE_TIME.add(player, 1);
		}, 20, 20);
	};
	public static final FarmStatBuffered OFFLINE_TIME = new FarmStatTime("Farm Offline Playtime", false);
	public static final FarmStatLinked PLAY_TIME = new FarmStatLinked("Farm Total Playtime", false, (f) -> {
		return ONLINE_TIME.get(f) + OFFLINE_TIME.get(f);
	});

	public static final FarmStatBuffered EARNINGS = new FarmStatDouble("Farm Earnings", false);

	public static FarmStatBuffered SEEDS_TOTAL(Element element) {
		return totalSeeds.get(element);
	}

	private static final Map<Element, FarmStatBuffered> totalSeeds = new HashMap<Element, FarmStatBuffered>();
	static {
		for (final Element element : Element.elements.values())
			totalSeeds.put(element, new FarmStatDouble("Farm seeds total " + element.name, false) {
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

	public static final FarmStatLinked ALL_SEEDS_TOTAL = new FarmStatLinked("Farm seeds total", false, (f) -> {
		double val = 0;
		for (final Element element : Element.elements.values())
			val += SEEDS_TOTAL(element).get(f);
		return val;
	});

	public static final FarmStatBuffered WORKERS = new FarmStatDouble("Farm Workers", false);

	public static final FarmStatBuffered CLICKS = new FarmStatDouble("Farm Clicks", false);
	public static final FarmStatBuffered CLICKS_AUTO = new FarmStatDouble("Farm Clicks Manual", false);
	public static final FarmStatBuffered CLICKS_MANUAL = new FarmStatDouble("Farm Clicks Auto", false);

	public static final FarmStatLinked OFFLINE_BONUS = new FarmStatLinked("Farm Offline Bonus", false, (f) -> {
		return 0; // TODO
	});
}
