
package jdz.farmKing.stats.types;

import jdz.farmKing.stats.FarmStat;

public class FarmStatPercent extends FarmStat {
	public FarmStatPercent(String name) {
		super(name, true);
	}

	public FarmStatPercent(String name, boolean isVisible) {
		super(name, isVisible);
	}

	@Override
	public String valueToString(double val) {
		return String.format("%.2f", val * 100) + "%";
	}
}
