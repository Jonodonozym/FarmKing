
package jdz.farmKing.stats.types;

public class FarmStatInt extends FarmStatBuffered {
	public FarmStatInt(String name) {
		super(name, true, true);
	}
	
	public FarmStatInt(String name, boolean isVisible) {
		super(name, isVisible, true);
	}
	
	public FarmStatInt(String name, boolean isVisible, boolean hasMax) {
		super(name, isVisible, hasMax);
	}

	@Override
	public String valueToString(double val) {
		return (int)val + "";
	}
}
