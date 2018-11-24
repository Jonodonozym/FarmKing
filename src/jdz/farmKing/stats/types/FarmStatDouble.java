
package jdz.farmKing.stats.types;

import jdz.UEconomy.UEcoFormatter;

public class FarmStatDouble extends FarmStatBuffered {
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
