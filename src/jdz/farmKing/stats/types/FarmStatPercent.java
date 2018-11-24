
package jdz.farmKing.stats.types;

public class FarmStatPercent extends FarmStatBuffered {
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
