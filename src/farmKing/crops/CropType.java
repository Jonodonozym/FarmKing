package farmKing.crops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import farmKing.main.Main;

public class CropType {
	public static List<CropType> cropTypes;
	public static Map<String,Integer> cropToIndex;
	public static final List<Double> upgradeBonuses;
	public static final List<Integer> upgradeQuantityRequirement;
	public static final List<Integer> upgradeCostfromPrice;
	
	public final double basePrice;
	public final double costMultiplier;
	public final double baseIncome;
	public final Material material;
	public final Material base;
	public final String name;
	public final byte data; 
	
	public CropType(String name, double basePrice, double costMultiplier, double baseIncome, Material material, Material base){
		this.name = name;
		this.basePrice = basePrice;
		this.costMultiplier = costMultiplier;
		this.baseIncome = baseIncome;
		this.material = material;
		this.base = base;
		this.data = 0;
	}
	
	public CropType(String name, double basePrice, double costMultiplier, double baseIncome, Material material, int data, Material base){
		this.name = name;
		this.basePrice = basePrice;
		this.costMultiplier = costMultiplier;
		this.baseIncome = baseIncome;
		this.material = material;
		this.base = base;
		this.data = (byte)data;
	}
	
	//static stuff
	static{
		upgradeBonuses = Arrays.asList(2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0);
		upgradeQuantityRequirement = Arrays.asList(10,20,40,60,100,125,150,200,250,300);
		upgradeCostfromPrice = Arrays.asList(20,30,50,75,125,150,175,225,275,400);
	}
	
	public static void initializeCropType(FileConfiguration config){
		
		double cm = Main.costMultiplier;
		cropTypes = new ArrayList<CropType>();
		int i=1;
		cropTypes.add( new CropType("Wheat", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.CROPS, 7, Material.SOIL));
		cropTypes.add( new CropType("Potato", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.POTATO, 7, Material.SOIL));
		cropTypes.add( new CropType("Carrot", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.CARROT, 7, Material.SOIL));
		cropTypes.add( new CropType("Beetroot", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.BEETROOT_BLOCK, 3, Material.SOIL));
		cropTypes.add( new CropType("Sugarcane", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.SUGAR_CANE_BLOCK, Material.GRASS));
		cropTypes.add( new CropType("Cactus", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.CACTUS, Material.SAND));
		cropTypes.add( new CropType("Pumpkin", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.PUMPKIN, 4, Material.PUMPKIN));
		cropTypes.add( new CropType("Melon", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.MELON_BLOCK, Material.MELON_BLOCK));
		cropTypes.add( new CropType("Red Mushroom", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.RED_MUSHROOM, Material.MYCEL));
		cropTypes.add( new CropType("Brown Mushroom", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.BROWN_MUSHROOM, Material.MYCEL));
		cropTypes.add( new CropType("Fern", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.LONG_GRASS, 2, Material.GRASS));
		cropTypes.add( new CropType("Deadbush", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.DEAD_BUSH, Material.SAND));
		cropTypes.add( new CropType("Red Poppy", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.RED_ROSE, Material.GRASS));
		cropTypes.add( new CropType("Dandelion", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.YELLOW_FLOWER, Material.GRASS));
		cropTypes.add( new CropType("Sapling", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.SAPLING, Material.GRASS));
		cropTypes.add( new CropType("Netherwart", Double.parseDouble(config.getString("crops.cost.c"+i)), cm, Double.parseDouble(config.getString("crops.income.c"+i++)), Material.NETHER_WARTS, Material.SOUL_SAND));
		
		i=0;
		cropToIndex = new HashMap<String, Integer>();
		for(CropType c: cropTypes)
			cropToIndex.put(c.name, i++);
	}
}
