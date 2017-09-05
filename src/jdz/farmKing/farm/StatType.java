
package jdz.farmKing.farm;

import java.util.HashSet;
import java.util.Set;

import com.jonodonozym.UPEconomy.UPEconomyAPI;

public enum StatType {
	FARM_LEVEL,
	FARM_EARNINGS,
	FARM_ONLINE_TIME,
	FARM_OFFLINE_TIME,
	FARM_PLAY_TIME,
	FARM_GEMS,
	FARM_SEEDS_0,
	FARM_SEEDS_1,
	FARM_SEEDS_2,
	FARM_SEEDS_3,
	FARM_SEEDS_0_EARNT,
	FARM_SEEDS_1_EARNT,
	FARM_SEEDS_2_EARNT,
	FARM_SEEDS_3_EARNT,
	FARM_CROP_QUANTITY_0,
	FARM_CROP_QUANTITY_1,
	FARM_CROP_QUANTITY_2,
	FARM_CROP_QUANTITY_3,
	FARM_CROP_QUANTITY_4,
	FARM_CROP_QUANTITY_5,
	FARM_CROP_QUANTITY_6,
	FARM_CROP_QUANTITY_7,
	FARM_CROP_QUANTITY_8,
	FARM_CROP_QUANTITY_9,
	FARM_CROP_QUANTITY_10,
	FARM_CROP_QUANTITY_11,
	FARM_CROP_QUANTITY_12,
	FARM_CROP_QUANTITY_13,
	FARM_CROP_QUANTITY_14,
	FARM_CROP_QUANTITY_15,
	FARM_CROP_QUANTITY_ALIGNMENT,
	FARM_CROP_QUANTITY_TOTAL,
	FARM_WORKERS,
	FARM_CLICKS,
	FARM_OFFLINE_BONUS;

	private static Set<StatType> cumulativeSet = new HashSet<StatType>();
	
	static{
		cumulativeSet.add(FARM_EARNINGS);
		cumulativeSet.add(FARM_ONLINE_TIME);
		cumulativeSet.add(FARM_OFFLINE_TIME);
		cumulativeSet.add(FARM_PLAY_TIME);
		cumulativeSet.add(FARM_CLICKS);
		cumulativeSet.add(FARM_SEEDS_0);
		cumulativeSet.add(FARM_SEEDS_1);
		cumulativeSet.add(FARM_SEEDS_2);
		cumulativeSet.add(FARM_SEEDS_3);
	}
	
	public boolean isCumulative(){
		return  cumulativeSet.contains(this);
	}
	public boolean isMax(){
		return  true;
	}
	
	
	public String toPlainString(){
		switch(this){
		default: return toString().toLowerCase();
		}
	}
	
	public String valueToString(double value){
		switch(this){
		case FARM_CLICKS:
			return UPEconomyAPI.charFormat(value, 4);
			
		case FARM_CROP_QUANTITY_0:
		case FARM_CROP_QUANTITY_1:
		case FARM_CROP_QUANTITY_10:
		case FARM_CROP_QUANTITY_11:
		case FARM_CROP_QUANTITY_12:
		case FARM_CROP_QUANTITY_13:
		case FARM_CROP_QUANTITY_14:
		case FARM_CROP_QUANTITY_15:
		case FARM_CROP_QUANTITY_2:
		case FARM_CROP_QUANTITY_3:
		case FARM_CROP_QUANTITY_4:
		case FARM_CROP_QUANTITY_5:
		case FARM_CROP_QUANTITY_6:
		case FARM_CROP_QUANTITY_7:
		case FARM_CROP_QUANTITY_8:
		case FARM_CROP_QUANTITY_9:
		case FARM_CROP_QUANTITY_ALIGNMENT:
		case FARM_CROP_QUANTITY_TOTAL:
			return ""+(int)value;
			
		case FARM_EARNINGS: return "$"+UPEconomyAPI.charFormat(value, 4);
		case FARM_GEMS: return UPEconomyAPI.charFormat(value, 4)+" Gems";
		case FARM_LEVEL: return ""+(int)value;
		case FARM_OFFLINE_BONUS: return UPEconomyAPI.charFormat(value*100, 4)+"%";
		
		case FARM_OFFLINE_TIME:
		case FARM_ONLINE_TIME:
		case FARM_PLAY_TIME:
			return timeFromSeconds((int)value);
			
		case FARM_SEEDS_0:
		case FARM_SEEDS_0_EARNT:
		case FARM_SEEDS_1:
		case FARM_SEEDS_1_EARNT:
		case FARM_SEEDS_2:
		case FARM_SEEDS_2_EARNT:
		case FARM_SEEDS_3:
		case FARM_SEEDS_3_EARNT: return UPEconomyAPI.charFormat(value, 4);
		
		case FARM_WORKERS: return ""+(int)value;
		default: return value+"";
		}
	}
	
	private static String timeFromSeconds(int totalMinutes){
		int days = totalMinutes / 1440;
		int hours = (totalMinutes % 1440) / 24;
		int minutes = ((totalMinutes % 1440 ) % 24);
		
		String rs = "";
		if (days > 0)
			rs = rs+days+"d ";
		if (hours > 0)
			rs = rs+hours+"h ";
		if (minutes > 0)
			rs = rs+minutes+"m ";
		
		if (rs.equals(""))
			rs = "0m";
		else
			rs = rs.substring(0, rs.length()-1);
		
		return rs;
	}
}
