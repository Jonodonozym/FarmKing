
package jdz.farmKing.stats.types;

import jdz.farmKing.farm.Farm;
import jdz.statsTracker.stats.StatType;
import jdz.statsTracker.stats.abstractTypes.MaxStatType;
import lombok.Getter;

public class FarmStatMax extends MaxStatType implements FarmStat {
	@Getter private final String name;
	@Getter private final boolean visible = false;
	
	protected FarmStatMax(StatType child) {
		super(child);
		this.name = "Max "+child.getName();
	}

	@Override
	public double get(Farm farm) {
		if (farm.getOwner() == null)
			return 0;
		return super.get(farm.getOwner());
	}

	@Override
	public FarmStatMax getMaxType() {
		return null;
	}

}
