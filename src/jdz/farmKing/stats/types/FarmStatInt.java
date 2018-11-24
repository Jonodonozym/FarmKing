
package jdz.farmKing.stats.types;

import jdz.farmKing.stats.FarmStat;

public class FarmStatInt extends FarmStat {
	public FarmStatInt(String name) {
		super(name, true);
	}
	
	public FarmStatInt(String name, boolean isVisible) {
		super(name, isVisible);
	}

	@Override
	public String valueToString(double val) {
		return (int)val + "";
	}
}
