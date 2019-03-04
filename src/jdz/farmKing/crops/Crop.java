package jdz.farmKing.crops;

import static jdz.farmKing.upgrades.UpgradeBonus.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import jdz.UEconomy.data.UEcoBank;
import jdz.farmKing.crops.calculators.CropCostCalculator;
import jdz.farmKing.crops.calculators.CropUpgradeCalculator;
import jdz.farmKing.element.data.PlayerElementData;
import jdz.farmKing.element.data.PlayerElementDataManager;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.utils.BuyAmount;
import jdz.farmKing.utils.Direction;
import lombok.Getter;

public class Crop {
	@Getter private final Farm farm;
	@Getter private final CropType type;

	@Getter private boolean generated = false;

	private final CropInfoHologram hologram;
	private final CropBuySign buySign;
	private final CropInfoSign infoSign;
	private final CropVisualisationGenerator visualGen;

	public Crop(Farm farm, CropType cropType) {
		this.farm = farm;
		type = cropType;

		if (cropType.getId() == 0)
			generated = true;
		else
			generated = farm.getCrops()[cropType.getId() - 1].getQuantity() > 0;

		hologram = new CropInfoHologram(this);
		buySign = new CropBuySign(this, getDirection());
		infoSign = new CropInfoSign(this, getDirection());
		visualGen = new CropVisualisationGenerator(this);

		if (isGenerated())
			hologram.generate();
	}

	@SuppressWarnings("deprecation")
	public void generate(World world) {
		if (generated)
			return;

		generated = true;

		// generating plant
		Location location = getLocation();
		int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
		Block base = world.getBlockAt(x, y, z);

		base.setType(type.getBase());
		if (type.getBase() != type.getMaterial()) {
			if (type.getMaterial() == Material.SUGAR_CANE_BLOCK) {
				world.getBlockAt(x - getDirection().getDx(), y, z + 1).setType(Material.BARRIER);
				world.getBlockAt(x - getDirection().getDx(), y, z - 1).setType(Material.BARRIER);
				world.getBlockAt(x - getDirection().getDx(), y, z).setType(Material.WATER);
			}
			Block plant = world.getBlockAt(x, y + 1, z);
			plant.setType(type.getMaterial());
			plant.setData(type.getData());
		}
		else
			base.setData(type.getData());

		hologram.generate();
		buySign.generate();
		infoSign.generate();

		for (int i = 0; i < getLevel(); i++)
			new CropUpgradeFrame(this, getDirection(), i, true).generate();
	}

	public void update() {
		hologram.update();
		buySign.update();
		infoSign.update();
	}

	public void reset() {
		if (!generated)
			return;

		generated = false;
		setQuantity(0);

		hologram.delete();
		buySign.delete();
		infoSign.delete();
		visualGen.clear();

		for (int i = 0; i < getLevel() + 1; i++)
			new CropUpgradeFrame(this, getDirection(), i, true).delete();
		setLevel(0);
	}

	public boolean buy(BuyAmount amount) {
		double price = getBuyCost(amount);
		if (UEcoBank.has(farm.getOwner(), price)) {
			UEcoBank.subtract(farm.getOwner(), price);
			visualGen.generatePlants(getQuantity(), getQuantity() + amount.getAmount());
			if (getQuantity() == 0)
				farm.generateCrop(type.getId() + 1);
			setQuantity(getQuantity() + amount.getAmount());
			return true;
		}
		return false;
	}

	public void levelUp() {
		FarmStats.CROP_LEVEL(type).add(farm, 1);
		if (getLevel() < 10)
			new CropUpgradeFrame(this, getDirection(), getLevel(), true).generate();
	}

	public double getBuyCost(BuyAmount amount) {
		int costMultiplierReduction = (int) farm.getUpgradeBonus(CROP_COST_MULTIPLIER);
		if (isElementCrop())
			costMultiplierReduction += farm.getUpgradeBonus(ELEMENT_CROP_COST_MULTIPLIER);
		return CropCostCalculator.getCost(type.getBasePrice(), costMultiplierReduction, getQuantity(), amount);
	}

	public double getIncome() {
		double income = type.getBaseIncome() * getQuantity() * CropUpgradeCalculator.getIncomeMultiplier(getLevel());
		income *= farm.getUpgradeBonus(CROP_INCOME);
		if (isElementCrop())
			income *= farm.getUpgradeBonus(ELEMENT_CROP_INCOME);
		return income;
	}

	public boolean isElementCrop() {
		PlayerElementData data = PlayerElementDataManager.getInstance().get(farm.getOwner().getPlayer());
		return data.getElement() != null && data.getElement().hasCrop(type.getMaterial());
	}

	public Direction getDirection() {
		return farm.getSchematic().getCropDirection(type.getId());
	}

	public Location getLocation() {
		return farm.getSchematic().getCropLocation(farm, type.getId());
	}

	public int getLevel() {
		return (int) FarmStats.CROP_LEVEL(type).get(farm);
	}

	public int getQuantity() {
		return (int) FarmStats.CROP_AMOUNT(type).get(farm);
	}

	private void setLevel(int level) {
		FarmStats.CROP_LEVEL(type).set(farm, level);
	}

	private void setQuantity(int amount) {
		FarmStats.CROP_AMOUNT(type).set(farm, amount);
	}
}