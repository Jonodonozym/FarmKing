
package jdz.farmKing.stats.types;

import jdz.UEconomy.UEcoFormatter;
import jdz.farmKing.stats.FarmStat;

public class FarmStatDouble extends FarmStat {
	public FarmStatDouble(String name) {
		super(name, true);
	}
	
	public FarmStatDouble(String name, boolean isVisible) {
		super(name, isVisible);
	}

	@Override
	public String valueToString(double val) {
		return UEcoFormatter.charFormat(val);
	}
}
