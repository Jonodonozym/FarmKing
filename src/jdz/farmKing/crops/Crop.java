package jdz.farmKing.crops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import jdz.UEconomy.data.UEcoBank;
import jdz.farmKing.crops.calculators.CropCostCalculator;
import jdz.farmKing.crops.calculators.CropUpgradeCalculator;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.utils.BuyAmount;
import jdz.farmKing.utils.Direction;
import lombok.Getter;
import lombok.Setter;

public class Crop {
	@Getter private final Farm farm;
	@Getter private final CropType type;

	@Getter private int quantity = 0;
	@Getter private boolean generated = false;
	@Getter private int level = 0;

	private final CropInfoHologram hologram;
	private final CropBuySign buySign;
	private final CropInfoSign infoSign;

	@Getter @Setter private double incomeMultiplier = 1;
	@Getter @Setter private int costMultiplierReductionLevel = 0;

	public Crop(Farm farm, CropType cropType) {
		this(farm, cropType, 0, 0, false);
	}

	public Crop(Farm farm, CropType cropType, int level, int quantity, boolean isGenerated) {
		this.farm = farm;
		type = cropType;
		this.quantity = quantity;
		generated = isGenerated;
		this.level = level;

		hologram = new CropInfoHologram(this);
		buySign = new CropBuySign(this, getDirection());
		infoSign = new CropInfoSign(this, getDirection());

		if (isGenerated())
			hologram.generate();
	}

	@SuppressWarnings("deprecation")
	public void generateStuff(World world) {
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

		for (int i = 0; i < level; i++)
			new CropUpgradeFrame(this, getDirection(), i, true).generate();
	}

	public void reset() {
		if (!generated)
			return;

		generated = false;
		quantity = 0;

		hologram.delete();
		buySign.delete();
		infoSign.delete();

		for (int i = 0; i < level + 1; i++)
			new CropUpgradeFrame(this, getDirection(), i, true).delete();
		level = 0;
	}

	public boolean buy(BuyAmount amount) {
		double price = getBuyCost(amount);
		if (UEcoBank.has(farm.getOwner(), price)) {
			UEcoBank.subtract(farm.getOwner(), price);
			quantity += amount.getAmount();
			return true;
		}
		return false;
	}

	public void levelUp() {
		level++;
		if (level < 10)
			new CropUpgradeFrame(this, getDirection(), level, true).generate();
	}

	public double getBuyCost(BuyAmount amount) {
		return CropCostCalculator.getCost(type.getBasePrice(), costMultiplierReductionLevel, quantity, amount);
	}

	public double getIncome() {
		return type.getBaseIncome() * quantity * CropUpgradeCalculator.getIncomeMultiplier(level) * incomeMultiplier;
	}

	private Direction getDirection() {
		return farm.getSchematic().getCropDirection(type.getId());
	}

	public Location getLocation() {
		return farm.getSchematic().getCropLocation(farm, type.getId());
	}
}