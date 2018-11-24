
package jdz.farmKing.stats.types;

import jdz.UEconomy.UEcoFormatter;

public class FarmStatDouble extends FarmStatBuffered {
	public FarmStatDouble(String name) {
		super(name, true, true);
	}
	
	public FarmStatDouble(String name, boolean isVisible) {
		super(name, isVisible, true);
	}
	
	public FarmStatDouble(String name, boolean isVisible, boolean hasMax) {
		super(name, isVisible, hasMax);
	}

	@Override
	public String valueToString(double val) {
		return UEcoFormatter.charFormat(val);
	}
}
