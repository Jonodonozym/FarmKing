
package jdz.farmKing.stats.types;

import jdz.farmKing.farm.Farm;
import jdz.statsTracker.stats.StatType;

public interface FarmStat extends StatType {
	public default boolean hasMax() {
		return getMaxType() != null;
	}

	public double get(Farm farm);

	public FarmStatMax getMaxType();
}
