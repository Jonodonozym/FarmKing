
package jdz.farmKing.crops.calculators;

import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import jdz.farmKing.crops.Crop;
import jdz.farmKing.utils.BuyAmount;

public class CropUpgradeCalculator {
	private static final List<Double> upgradeBonuses = Arrays.asList(2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0);
	private static final List<Integer> quantityReq = Arrays.asList(10, 20, 40, 60, 100, 125, 150, 200, 250, 300);
	private static final List<Integer> costToBaseRatio = Arrays.asList(20, 30, 50, 75, 125, 150, 175, 225, 275, 400);

	public static void initialise(FileConfiguration config) {
		// TODO
	}

	public static double getCost(Crop crop, int level) {
		double baseCost = CropCostCalculator.getCost(crop.getType().getBasePrice(), 0, 0, BuyAmount.BUY_1);
		return baseCost * costToBaseRatio.get(level);
	}

	public static double getIncomeMultiplier(int level) {
		double upgradeBonus = 1;
		for (int i = 0; i < level - 1; i++)
			upgradeBonus *= upgradeBonuses.get(i);
		return upgradeBonus;
	}

	public static double getQuantityRequired(int level) {
		return quantityReq.get(level);
	}
}
