
package jdz.farmKing.stats.types;

import jdz.farmKing.farm.Farm;
import jdz.statsTracker.stats.abstractTypes.BufferedStatType;
import jdz.statsTracker.stats.abstractTypes.MaxStatType;
import lombok.Getter;

public abstract class FarmStatBuffered extends BufferedStatType implements FarmStat {
	@Getter private final String name;
	@Getter private final boolean visible;
	@Getter private final MaxStatType maxType;

	public FarmStatBuffered(String name) {
		this(name, true, true);
	}

	public FarmStatBuffered(String name, boolean visible, boolean max) {
		this.name = name;
		this.visible = visible;
		if (max)
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
}
