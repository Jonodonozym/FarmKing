
package jdz.farmKing.farm.grass;

import java.util.Arrays;
import java.util.List;

public class GrassData {
	private static List<Integer> directUpgradeCost = Arrays.asList(500, 2500, 12500, 75000, 400000);
	private static List<Integer> directUpgradeBonus = Arrays.asList(4, 14, 39, 99, 499);
	private static List<Double> percentUpgradeCost = Arrays.asList(2.4E4, 7.48E6, 3.62E9, 5.75E11, 1.22E14);
	private static List<Double> percentUpgradeBonus = Arrays.asList(0.02, 0.04, 0.06, 0.08, 0.1);

	public static double getDirectCost(int currentLevel) {
		return directUpgradeCost.get(currentLevel);
	}

	public static double getDirectBonus(int currentLevel) {
		if (currentLevel == 0)
			return 0;
		return directUpgradeBonus.get(currentLevel - 1);
	}

	public static double getPercentCost(int currentLevel) {
		return percentUpgradeCost.get(currentLevel - 1);
	}

	public static double getPercentBonus(int currentLevel) {
		if (currentLevel == 0)
			return 0;
		return percentUpgradeBonus.get(currentLevel - 1);
	}
}
