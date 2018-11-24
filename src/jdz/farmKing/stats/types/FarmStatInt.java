
package jdz.farmKing.stats.types;

public class FarmStatInt extends FarmStatBuffered {
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
