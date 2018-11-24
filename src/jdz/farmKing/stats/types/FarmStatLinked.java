
package jdz.farmKing.stats.types;

import jdz.UEconomy.UEcoFormatter;
import jdz.farmKing.farm.Farm;
import jdz.statsTracker.stats.abstractTypes.MaxStatType;
import jdz.statsTracker.stats.abstractTypes.NoSaveStatType;
import lombok.Getter;

public class FarmStatLinked extends NoSaveStatType implements FarmStat {
	private final ValueGetter getter;
	@Getter private final String name;
	@Getter private final boolean visible;
	@Getter private final MaxStatType maxType;

	public FarmStatLinked(String name, ValueGetter getter) {
		this(name, true, true, getter);
	}

	public FarmStatLinked(String name, boolean visible, ValueGetter getter) {
		this(name, visible, true, getter);
	}

	public FarmStatLinked(String name, boolean visible, boolean hasMax, ValueGetter getter) {
		this.name = name;
		this.visible = visible;
		this.getter = getter;
		if (hasMax)
			maxType = new MaxStatType(this) {
				@Override
				public String getName() {
					return "Max " + this.getName();
				}
				@Override
				public boolean isVisible() {
					return false;
				}
			};
		else
			maxType = null;
	}

	public double get(Farm farm) {
		return getter.get(farm);
	}

	@Override
	public String valueToString(double val) {
		return UEcoFormatter.charFormat(val);
	}

	public static interface ValueGetter {
		public double get(Farm farm);
	}
}
