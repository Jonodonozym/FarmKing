
package jdz.farmKing.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import jdz.farmKing.FarmKing;
import jdz.farmKing.element.Element;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.stats.types.FarmStatDouble;
import jdz.farmKing.stats.types.FarmStatLinked;
import jdz.farmKing.stats.types.FarmStatTime;
import jdz.statsTracker.event.StatChangeEvent;
import jdz.statsTracker.stats.abstractTypes.BufferedStatType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FarmStat extends BufferedStatType {
	public static final FarmStat LEVEL = new FarmStatDouble("Farm Level");
	public static final FarmStat GEMS = new FarmStatDouble("Farm Gems");

	public static FarmStat CROP_TYPE(int index) {
		return CropQuantityTypes.get(index);
	}

	private static final List<FarmStat> CropQuantityTypes = new ArrayList<FarmStat>();
	static {
		for (int i = 0; i < 16; i++)
			CropQuantityTypes.add(new FarmStatDouble("Farm crop " + i, false));
	}

	public static FarmStat SEEDS(Element element) {
		return CurrentSeedTypes.get(element);
	}

	private static final Map<Element, FarmStat> CurrentSeedTypes = new HashMap<Element, FarmStat>();
	static {
		for (Element element : Element.elements.values())
			CurrentSeedTypes.put(element, new FarmStatDouble("Farm seeds " + element.name, false));
	}

	public static final FarmStat ONLINE_TIME = new FarmStatTime("Farm Online Playtime", false);
	static {
		Bukkit.getScheduler().runTaskTimer(FarmKing.getInstance(), ()->{
			for (Player player: Bukkit.getOnlinePlayers())
				ONLINE_TIME.add(player, 1);
		}, 20, 20);
	};
	public static final FarmStat OFFLINE_TIME = new FarmStatTime("Farm Offline Playtime", false);
	public static final FarmStatLinked PLAY_TIME = new FarmStatLinked("Farm Total Playtime", false, (f) -> {
		return ONLINE_TIME.get(f) + OFFLINE_TIME.get(f);
	});

	public static final FarmStat EARNINGS = new FarmStatDouble("Farm Earnings", false);

	public static FarmStat SEEDS_TOTAL(Element element) {
		return totalSeeds.get(element);
	}

	private static final Map<Element, FarmStat> totalSeeds = new HashMap<Element, FarmStat>();
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

	public static final FarmStat WORKERS = new FarmStatDouble("Farm Workers", false);

	public static final FarmStat CLICKS = new FarmStatDouble("Farm Clicks", false);
	public static final FarmStat CLICKS_AUTO = new FarmStatDouble("Farm Clicks Manual", false);
	public static final FarmStat CLICKS_MANUAL = new FarmStatDouble("Farm Clicks Auto", false);

	public static final FarmStatLinked OFFLINE_BONUS = new FarmStatLinked("Farm Offline Bonus", false, (f) -> {
		return 0; // TODO
	});

	public void set(Farm farm, double amount) {
		if (farm.getOwner() == null)
			return;
		super.set(farm.getOwner(), amount);
	}

	public void add(Farm farm, double amount) {
		if (farm.getOwner() == null)
			return;
		super.add(farm.getOwner(), amount);
	}

	public void subtract(Farm farm, double amount) {
		if (farm.getOwner() == null)
			return;
		super.add(farm.getOwner(), -amount);
	}

	public double get(Farm farm) {
		if (farm.getOwner() == null)
			return 0;
		return get(farm.getOwner());
	}

	@Getter private final String name;
	@Getter private final boolean visible;
}
