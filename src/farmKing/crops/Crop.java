package farmKing.crops;

import farmKing.main.*;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.jonodonozym.UPEconomy.*;

public class Crop {
	private static double[] buy10Mult, buy100Mult, buy1000Mult;
	private static Map<Integer,Double>[] buyMap;
	public int CMI = 0; 

	private final Farm farm;
	private final CropType cropType;
	private final Location location;
	private final Location infoSign;
	private final int direction;
	private final World world;
	private final Main plugin;
	
	private Hologram hologram = null;
	private TextLine l1, l2;
	
	private int level = 0;
	private double upgradeBonus = 1;
	
	private int quantity = 0 ;
	private int isGenerated = 0;
	
	public double currentIncome;
	public double incomeBonus = 1;
	private double cost, cost10, cost100, cost1000;
	
	public Crop(Farm farm, CropType cropType, Location location, Main plugin, int level, int quantity, int isGenerated){
		this.farm = farm;
		this.cropType = cropType;
		this.location = location;
		this.plugin = plugin;
		this.level = level;
		this.quantity = quantity;
		this.isGenerated = isGenerated;
		
		this.world = farm.plugin.getServer().getWorlds().get(0);

		for (int i=0; i<level-1; i++)
			upgradeBonus *= CropType.upgradeBonuses.get(i);
		
		int index = CropType.cropToIndex.get(cropType.name);
		this.direction = index < 8? 1 : -1;
		this.infoSign = new Location(world, location.getBlockX()+5*direction, location.getBlockY()+1, location.getBlockZ());
		
		updatePrice();
		updateIncome();
		updateHologram();
		updateSign();
	}	
	
	public Crop(Farm farm, CropType cropType, Location location, Main plugin){
		this(farm, cropType, location, plugin, 0, 0, 0);
	}

	/**
	 * Resets the crop to it's original state
	 */
	public void deleteData(){
		if (isGenerated == 0) return;
		level = 0;
		isGenerated = 0;
		upgradeBonus = 1;
		quantity = 0;
		
		Main.holograms.remove(hologram);
		hologram.delete();
		hologram = null;
		
		updatePrice();
		updateIncome();
	}
	
	/**
	 * Generates the crop for the first time by generating the base, plant, buy sign, info sign and upgrade frame
	 * @param world
	 */
	@SuppressWarnings("deprecation")
	public void generateStuff(World world){
		if (isGenerated == 0){
			isGenerated = 1;
			
			//generating plant
			int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
			Block base = world.getBlockAt(x, y, z);
			
			base.setType(cropType.base);
			if (cropType.base != cropType.material){
				Block plant = world.getBlockAt(x, y+1, z);
				plant.setType(cropType.material);
				plant.setData(cropType.data);
			}
			else
				base.setData(cropType.data);
			
			//generating buy sign
			Block sign = world.getBlockAt(x+direction, y, z);
			sign.setType(Material.WALL_SIGN);
			sign.setData( direction < 0 ? (byte)4 : (byte)5 );
			Sign s = (Sign) sign.getState();
			s.setLine(1, cropType.name);
			s.setLine(2, "Buy "+farm.buyQuantity);
			s.update(true);
			
			//generating hologram
			updateHologram();
			
			//generating info sign
			updateSign();
			
			//generating first upgrade sign
			Location nL = new Location(world,x+5,y+3,z-2);
			if (direction < 0)
				nL = new Location(world,x-5,y+3,z-2);
			spawnNextUpgrade(nL);
		}
	}
	
	/**
	 * upgrades the hologram that sits above the plant with the quantity and cost of buying farm.buyQuantity plants
	 */
	public void updateHologram(){
		if (farm.owner == null) return;
		if (isGenerated == 0) return;
		if (hologram == null){
			World world = farm.plugin.getServer().getWorlds().get(0);
			Location l = new Location(world, location.getBlockX()+0.5, location.getBlockY()+2.5, location.getBlockZ()+0.5);
			hologram = HologramsAPI.createHologram(plugin, l);
			Main.holograms.add(hologram);
			l1 = hologram.appendTextLine("");
			l2 = hologram.appendTextLine("");
		}
		
		double buyCost = getBuyCost(farm.buyQuantity);
		
		ChatColor colorCode = ChatColor.RED;
		if (UPEconomyAPI.hasEnough(farm.owner, buyCost))
			colorCode = ChatColor.GREEN;
		
		l1.setText(quantity+" Plants");
		l2.setText(colorCode + "Buy "+farm.buyQuantity+": $"+UPEconomyAPI.charFormat(buyCost, 4));
	}
	
	/**
	 * Updates the info sign with the income per plant / total income
	 */
	@SuppressWarnings("deprecation")
	public void updateSign(){
		if (farm.owner == null) return;
		if (isGenerated == 0) return ;
		Block sign = world.getBlockAt(infoSign);
		if (sign.getType() != Material.WALL_SIGN){
			sign.setType(Material.WALL_SIGN);
			sign.setData( direction > 0 ? (byte)4 : (byte)5 );
			Sign s = (Sign) sign.getState();
			s.setLine(0, "$ per second:");
			s.update();
		}
		if (sign.getType() == Material.WALL_SIGN){
			Sign s = (Sign) sign.getState();
			double inc1 = cropType.baseIncome * upgradeBonus * farm.globalIncomeMultiplier;
			double inc2 = inc1 * quantity;
			s.setLine(1, "Plant: $"+UPEconomyAPI.charFormat(inc1, 4));
			s.setLine(2, "Total: $"+UPEconomyAPI.charFormat(inc2, 4));
			s.update();
		}
		sign = world.getBlockAt(new Location(world, location.getBlockX()+direction, location.getBlockY(), location.getBlockZ()));
		Sign s = (Sign) sign.getState();
		s.setLine(2, "Buy "+farm.buyQuantity);
	}
	
	/**
	 * Updates the plants cost based on the current CMI model
	 */
	public void updatePrice(){
		double cm = Main.costMultiplier * Math.pow(0.8, CMI);
		while (quantity > buyMap[CMI].size())
			buyMap[CMI].put(buyMap[CMI].size(), Double.parseDouble(String.format("%10.9f", buyMap[CMI].get(buyMap[CMI].size()-1) * cm)));

		cost = buyMap[CMI].get(quantity) * cropType.basePrice;
		cost10 = cost * buy10Mult[CMI];
		cost100 = cost * buy100Mult[CMI];
		cost1000 = cost * buy1000Mult[CMI];
	}
	
	
	/**
	 * Buys a specific amount of plants if the player has enough money to do so
	 * @param amount
	 * @return true if the player has enough money, false otherwise
	 */
	public boolean buy(int amount){
		double price = getBuyCost(amount);
		if (UPEconomyAPI.hasEnough(farm.owner, price)){
			UPEconomyAPI.subBalance(farm.owner, price);
			quantity += amount;
			updatePrice();
			updateIncome();
		}
		else
			return false;
		return true;
	}
	
	/**
	 * Updates this crops income
	 */
	public void updateIncome(){
		currentIncome = cropType.baseIncome * quantity * upgradeBonus * incomeBonus;
	}
	
	/**
	 * The method called when an itemframe is clicked for this crop's upgrade series
	 * If the user has enough money, crops and hasn't already bought the upgrade, then buys the upgrade
	 * otherwise, sends the player the related error message.
	 * @param itemFrame
	 * @param cropLocation
	 * @return
	 */
	public boolean clickItemFrame(ItemFrame itemFrame, Location cropLocation){
		if (isGenerated == 0) return false;

		int Z = itemFrame.getLocation().getBlockZ() - cropLocation.getBlockZ();
		int Y = itemFrame.getLocation().getBlockY() - cropLocation.getBlockY();
		int upgradeLevel = Z+3+5*(3-Y);
		
		double upgradePrice = buyMap[CMI].get(CropType.upgradeCostfromPrice.get(upgradeLevel-1)) * cropType.basePrice;
		if (upgradeLevel == level+1){
			int reqQuan = CropType.upgradeQuantityRequirement.get(level);
			if (quantity < reqQuan){
				farm.owner.getPlayer().sendMessage(ChatColor.RED+"You need "+reqQuan+" plants to purchase this upgrade!");
				return false;
			}
			if (!UPEconomyAPI.hasEnough(farm.owner, upgradePrice)){
				farm.owner.getPlayer().sendMessage(ChatColor.RED+"You don't have enough money to purchase this upgrade!");
				return false;
			}
			UPEconomyAPI.subBalance(farm.owner, upgradePrice);
			level++;
			upgradeBonus *= CropType.upgradeBonuses.get(level-1);
			updateIncome();

			//changing this one
			farm.setUpgradePurchased(itemFrame);
			
			//unlocking next one
				Location nL = new Location(world,itemFrame.getLocation().getBlockX(), itemFrame.getLocation().getBlockY(), itemFrame.getLocation().getBlockZ()+1);
				if (level % 5 == 0)
					nL.add(0, -1, -5 );
				spawnNextUpgrade(nL);
			
			return true;
		}
		return false;
	}
	
	/**
	 * spawns the next upgrade in the series
	 */
	private void spawnNextUpgrade(Location l){
		if (level < 10){
			ItemFrame nextUpgradeFrame = world.spawn(l, ItemFrame.class);
			ItemStack i = new ItemStack(Material.WOOL, 1, (short)14);
			int nextQuan = CropType.upgradeQuantityRequirement.get(level);
			String nextCost = UPEconomyAPI.charFormat( buyMap[CMI].get(CropType.upgradeCostfromPrice.get(level))*cropType.basePrice, 4);
			ItemMeta iM = i.getItemMeta();
			iM.setDisplayName(ChatColor.RED+""+nextQuan+" plants.  $"+nextCost);
			i.setItemMeta(iM);
			nextUpgradeFrame.setItem(i);
			nextUpgradeFrame.setFacingDirection(direction < 0 ? BlockFace.EAST : BlockFace.WEST);
		}
	}
	
	
	public int getQuantity(){ return quantity; }
	public boolean isGenerated(){ return (isGenerated == 1); }
	public CropType getType(){ return cropType; }
	public Location getLocation(){ return location; }
	

	/**
	 * Gets the cost of a certain quantity of crops
	 * @param i
	 * @return the cost, or NaN if the quantity is invalid
	 */
	private double getBuyCost(int i){
		switch(i){
			case 1: return cost;
			case 10: return cost10;
			case 100: return cost100;
			case 1000: return cost1000;
		}
		return Double.NaN;
	}
	
	/**
	 * Exports the crop to a line of text
	 */
	@Override
	public String toString(){
		return level+","+quantity+","+isGenerated;
	}
	
	/**
	 * Creates a crop from a line of text
	 * @return the loaded crop
	 */
	public static Crop fromString(Farm f, Main plugin, CropType cropType, Location location, String s){
		String[] args = s.split(",");
		Crop c = new Crop(f, cropType, location, plugin, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		return c;
	}

	
	/**
	 * Pre-processing for the crop cost to enhance performance
	 * @param costMultiplier
	 * @param maxCropSize
	 */
	public static void initCropData(double costMultiplier, int maxCropSize){
		System.out.println("[FarmKing] pre-processing crop cost calculation data");

		for (int c=0; c<3; c++){
			double cm = Main.costMultiplier - (c*Main.costMultiplierReduction);
			
			buyMap[c] = new HashMap<Integer,Double>();
			double current = 1;
			
			for(int i=0; i < maxCropSize; i++){
				buyMap[c].put(i, current);
				current = Double.parseDouble(String.format("%10.9f", current*cm));
			}
			
			buy10Mult[c] = 0;
			for (int i=0; i<10; i++){
				buy10Mult[c] += buyMap[c].get(i);
			}
			buy10Mult[c] = Double.parseDouble(String.format("%10.9f", buy10Mult[c]));
			
			buy100Mult[c] = 0;
			for (int i=0; i<100; i++){
				buy100Mult[c] += buyMap[c].get(i);
			}
			buy100Mult[c] = Double.parseDouble(String.format("%10.9f", buy100Mult[c]));
			
			buy1000Mult[c] = 0;
			for (int i=0; i<1000; i++){
				buy1000Mult[c] += buyMap[c].get(i);
			}
			buy1000Mult[c] = Double.parseDouble(String.format("%10.9f", buy1000Mult[c]));
		}
	}
}