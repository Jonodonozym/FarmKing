
package jdz.farmKing.stats.types;

import jdz.statsTracker.stats.StatType;
import jdz.statsTracker.stats.abstractTypes.MaxStatType;

public interface FarmStat extends StatType {
	public default boolean hasMax() {
		return getMaxType() != null;
	}

	public MaxStatType getMaxType();
}
