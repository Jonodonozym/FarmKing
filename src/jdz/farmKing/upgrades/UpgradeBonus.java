package jdz.farmKing.upgrades;

import jdz.UEconomy.UEcoFormatter;

public enum UpgradeType {
	CLICK_DIRECT, CLICK_PERCENT, SEED_DIRECT, SEED_PERCENT,
	ALIGNMENT_CROP_PERCENT, ALL_CROPS_PERCENT, WORKER_COUNT_DIRECT, WORKER_COUNT_PERCENT,
	WORKER_PRODUCTION_PERCENT, WORKER_SEED_DIRECT, WORKER_SEED_PERCENT, OFFLINE_PRODUCTION_PERCENT,
	ONLINE_PRODUCTION_PERCENT, MAIN_CROP_COST_MULTIPLIER, ALL_CROP_COST_MULTIPLIER, AUTO_CLICKS,
	SEED_OVER_TIME;

	public String valueToString(double value){
		switch(this){
		case ALIGNMENT_CROP_PERCENT:
		case ALL_CROPS_PERCENT:
		case CLICK_PERCENT:
		case OFFLINE_PRODUCTION_PERCENT:
		case ONLINE_PRODUCTION_PERCENT:
		case SEED_PERCENT:
		case WORKER_COUNT_PERCENT:
		case WORKER_PRODUCTION_PERCENT:
		case WORKER_SEED_PERCENT:
			return UEcoFormatter.charFormat(value*100, 5)+"%";

		case WORKER_COUNT_DIRECT:
		case SEED_DIRECT:
		case WORKER_SEED_DIRECT:
		case CLICK_DIRECT:
			return UEcoFormatter.makeWhole(UEcoFormatter.charFormat(value, 5));
			
		case ALL_CROP_COST_MULTIPLIER:
		case MAIN_CROP_COST_MULTIPLIER:
			return (int)value+" rank"+(value>1?"s":"");
			
		case AUTO_CLICKS:
			return (int)value+" click"+(value>1?"s":"")+"/s";
			
		case SEED_OVER_TIME:
			return UEcoFormatter.makeWhole(UEcoFormatter.charFormat(value, 5))+" seeds/s";

		default:
			return value + "";
		}
	}
}