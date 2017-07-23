package farmKing.upgrades;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import farmKing.crops.Farm;

public abstract class Upgrade {
	public static ChatColor loreColor = ChatColor.BLUE;
	
	private final String name;
	private final List<String> lore;
	private final int numBonuses;
	private final String description;
	
	public Upgrade(String name, String description, int numBonuses){
		this.name = name;
		this.description = description;
		
		this.lore = new ArrayList<String>();
		String[] words = description.split(" ");
		String currentString = "";
		for (String s: words){
			if (currentString.length()+s.length() > 35){
				this.lore.add(loreColor+currentString);
				currentString = "";
			}
			currentString = currentString + s +" ";
		}
		this.lore.add(loreColor+currentString);
		
		this.numBonuses = numBonuses;
	}
	
	public static enum upgradeType{
		NOTHING,
		RUN_ON_PURCHASE,
		RUN_ON_TICK,
		CLICK_DIRECT,
		CLICK_PERCENT,
		SEED_DIRECT,
		SEED_PERCENT,
		MAIN_CROP_PERCENT,
		ALL_CROPS_PERCENT,
		WORKER_COUNT_DIRECT,
		WORKER_COUNT_PERCENT,
		WORKER_PRODUCTION_PERCENT,
		WORKER_SEED_DIRECT,
		WORKER_SEED_PERCENT,
		OFFLINE_PRODUCTION_PERCENT,
		ONLINE_PRODUCTION_PERCENT,
		MAIN_CROP_COST_MULTIPLIER,
		ALL_CROP_COST_MULTIPLIER,
		AUTO_CLICKS,
		SEED_COST_MULTIPLIER,
		SEED_OVER_TIME,
		CROP_DIRECT;
	}

	public String getName() { return name; }
	public List<String> getLore() { return lore; }
	public String getDescription(){ return description; }
	public int getNumBonuses(){ return numBonuses; }
	
	public abstract double getBonus(int i, Farm f);
	public abstract upgradeType getType(int i);
	public abstract boolean isDisplayable(int i);
	public void run(Farm f){};
	
}
