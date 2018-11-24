
package jdz.farmKing.stats.types;

import jdz.UEconomy.UEcoFormatter;
import jdz.farmKing.farm.Farm;
import jdz.statsTracker.stats.abstractTypes.NoSaveStatType;
import lombok.Getter;

public class FarmStatLinked extends NoSaveStatType {
	private final ValueGetter getter;
	@Getter private final String name;
	@Getter private final boolean visible;
	
	public FarmStatLinked(String name, boolean visible, ValueGetter getter) {
		this.name = name;
		this.visible = visible;
		this.getter = getter;
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
