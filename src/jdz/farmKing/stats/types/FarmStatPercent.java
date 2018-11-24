
package jdz.farmKing.stats.types;

public class FarmStatPercent extends FarmStatBuffered {
	public FarmStatPercent(String name) {
		super(name, true, true);
	}
	
	public FarmStatPercent(String name, boolean isVisible) {
		super(name, isVisible, true);
	}
	
	public FarmStatPercent(String name, boolean isVisible, boolean hasMax) {
		super(name, isVisible, hasMax);
	}

	@Override
	public String valueToString(double val) {
		return String.format("%.2f", val * 100) + "%";
	}
}
