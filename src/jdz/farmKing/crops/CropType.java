package jdz.farmKing.crops;

import static java.lang.Double.parseDouble;
import static org.bukkit.Material.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

public class CropType {
	private static List<CropType> cropTypes = new ArrayList<>();

	public static List<CropType> values() {
		return Collections.unmodifiableList(cropTypes);
	}

	public static CropType get(int index) {
		return cropTypes.get(index);
	}

	public static void loadFromConfig(FileConfiguration config) {
		cropTypes.clear();
		int i = 1;
		cropTypes.add(new CropType("Wheat", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), CROPS, 7, SOIL, WHEAT, 0));
		cropTypes.add(new CropType("Potato", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), POTATO, 7, SOIL, POTATO_ITEM, 0));
		cropTypes.add(new CropType("Carrot", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), CARROT, 7, SOIL, CARROT_ITEM, 0));
		cropTypes.add(new CropType("Beetroot", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), BEETROOT_BLOCK, 3, SOIL, BEETROOT, 0));
		cropTypes.add(new CropType("Sugarcane", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), SUGAR_CANE_BLOCK, 0, GRASS, SUGAR_CANE, 0));
		cropTypes.add(new CropType("Cactus", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), CACTUS, SAND));
		cropTypes.add(new CropType("Pumpkin", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), PUMPKIN, 4, PUMPKIN));
		cropTypes.add(new CropType("Melon", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), MELON_BLOCK, MELON_BLOCK));
		cropTypes.add(new CropType("Red Mushroom", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), RED_MUSHROOM, MYCEL));
		cropTypes.add(new CropType("Brown Mushroom", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), BROWN_MUSHROOM, MYCEL));
		cropTypes.add(new CropType("Fern", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), LONG_GRASS, 2, GRASS));
		cropTypes.add(new CropType("Deadbush", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), DEAD_BUSH, SAND));
		cropTypes.add(new CropType("Red Poppy", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), RED_ROSE, GRASS));
		cropTypes.add(new CropType("Dandelion", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), YELLOW_FLOWER, GRASS));
		cropTypes.add(new CropType("Sapling", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), SAPLING, GRASS));
		cropTypes.add(new CropType("Netherwart", parseDouble(config.getString("crops.cost.c" + i)),
				parseDouble(config.getString("crops.income.c" + i++)), NETHER_WARTS, 3, SOUL_SAND, NETHER_STALK, 0));
	}

	private static int SID = 0;

	@Getter private final double basePrice;
	@Getter private final double baseIncome;
	@Getter private final Material material;
	@Getter private final Material base;
	@Getter private final String name;
	@Getter private final byte data;

	@Getter private final ItemStack icon;
	@Getter private final int id = SID++;

	private CropType(String name, double basePrice, double baseIncome, Material material, Material base) {
		this(name, basePrice, baseIncome, material, (byte) 0, base, material, (byte) 0);
	}

	private CropType(String name, double basePrice, double baseIncome, Material material, int data, Material base) {
		this(name, basePrice, baseIncome, material, (byte) data, base, material, (byte) data);
	}

	private CropType(String name, double basePrice, double baseIncome, Material material, int data, Material base,
			Material iconMaterial, int iconData) {
		this.name = name;
		this.basePrice = basePrice;
		this.baseIncome = baseIncome;
		this.material = material;
		this.base = base;
		this.data = (byte) data;
		icon = new ItemStack(iconMaterial, 1, (byte) iconData);
	}
}
