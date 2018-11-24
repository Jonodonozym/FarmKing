
package jdz.farmKing.stats.types;

public class FarmStatTime extends FarmStatBuffered {
	public FarmStatTime(String name) {
		super(name, true);
	}

	public FarmStatTime(String name, boolean isVisible) {
		super(name, isVisible);
	}

	@Override
	public String valueToString(double val) {
		return timeFromSeconds((long) val);
	}

	private String timeFromSeconds(long totalSeconds) {
		long days = totalSeconds / 86400;
		long hours = (totalSeconds % 86400) / 3600;
		long minutes = ((totalSeconds % 86400) % 3600) / 60;
		long seconds = ((totalSeconds % 86400) % 3600) % 60;

		String rs = "";
		if (days > 0)
			rs = rs + days + "d ";
		if (hours > 0)
			rs = rs + hours + "h ";
		if (minutes > 0)
			rs = rs + minutes + "m ";
		if (seconds > 0)
			rs = rs + seconds + "s ";

		if (rs.equals(""))
			rs = "0s";
		else
			rs = rs.substring(0, rs.length() - 1);

		return rs;
	}

}
