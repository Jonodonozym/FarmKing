
package jdz.farmKing.crops.calculators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import jdz.farmKing.utils.BuyAmount;

public class CropCostCalculator {
	private static double costMultiplier;
	private static double reductionPerLevel;
	private static int maxLevels;

	private static final List<Map<BuyAmount, Double>> BuyAmountMultipliers = new ArrayList<Map<BuyAmount, Double>>();
	private static final List<List<Double>> baseMultiplier = new ArrayList<List<Double>>();

	public static double getCost(double baseCost, int multReductionLevels, int currentQuantity, BuyAmount amount) {
		if (currentQuantity > baseMultiplier.get(0).size())
			preprocess(currentQuantity + 10);

		double baseMult = baseMultiplier.get(multReductionLevels).get(currentQuantity);
		double buyAmountMult = BuyAmountMultipliers.get(multReductionLevels).get(amount);
		return baseCost * baseMult * buyAmountMult;
	}

	public static void initialise(FileConfiguration config) {
		costMultiplier = config.getDouble("crops.multiplier");
		costMultiplier = costMultiplier < 1 ? 1.075 : costMultiplier;

		reductionPerLevel = config.getDouble("crops.multiplierReduction");
		maxLevels = config.getInt("crops.costMultiplierLevelsPreprocess");
		
		preprocess(config.getInt("crops.cropQuantityPricePreprocess"));
	}

	private static void preprocess(int numCropLevels) {
		System.out.println("[FarmKing] pre-processing crop cost calculation data");

		while (baseMultiplier.size() < maxLevels)
			baseMultiplier.add(new ArrayList<>());

		preprocessBuyAmount();
		preprocessCosts(numCropLevels);

		System.out.println("[FarmKing] pre-processing crop cost done");
	}

	private static void preprocessBuyAmount() {
		while (BuyAmountMultipliers.size() < maxLevels) {
			Map<BuyAmount, Double> buyAmountMult = new HashMap<>();
			for (BuyAmount amount : BuyAmount.values()) {
				double multiplier = 0;
				for (int i = 0; i < amount.getAmount(); i++)
					multiplier += baseMultiplier.get(BuyAmountMultipliers.size()).get(i);
				multiplier = Double.parseDouble(String.format("%10.9f", multiplier));
				buyAmountMult.put(amount, multiplier);
			}
			BuyAmountMultipliers.add(buyAmountMult);
		}
	}

	private static void preprocessCosts(int numCropLevels) {
		for (int multiplierLevel = 0; multiplierLevel < maxLevels; multiplierLevel++) {
			double reducedMultiplier = costMultiplier - (multiplierLevel * reductionPerLevel);

			List<Double> list = baseMultiplier.get(multiplierLevel);
			if (list.isEmpty())
				list.add(1D);

			double current = list.get(list.size() - 1);
			while (list.size() < numCropLevels) {
				list.add(current);
				current = Double.parseDouble(String.format("%10.9f", current * reducedMultiplier));
			}
		}
	}

}
