package jdz.farmKing.upgrades;

import jdz.UEconomy.UEcoFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UpgradeBonus {
	GRASS_FLAT(false), GRASS_INCOME(true), SEED_FLAT(false), SEED_MULTIPLIER(true),
	ELEMENT_CROP_INCOME(true), CROP_INCOME(true), WORKER_COUNT_FLAT(false), WORKER_COUNT_MULTIPLIER(true),
	WORKER_INCOME(true), WORKER_SEED_FLAT(false), WORKER_SEED_MULTIPLIER(true), OFFLINE_INCOME(true),
	ONLINE_INCOME(true), ELEMENT_CROP_COST_MULTIPLIER(false), CROP_COST_MULTIPLIER(false), AUTO_CLICKS(false),
	SEEDS_OVER_TIME(false);
	
	@Getter private final boolean isMultiplicative;

	public String valueToString(double value){
		switch(this){
		case ELEMENT_CROP_INCOME:
		case CROP_INCOME:
		case GRASS_INCOME:
		case OFFLINE_INCOME:
		case ONLINE_INCOME:
		case SEED_MULTIPLIER:
		case WORKER_COUNT_MULTIPLIER:
		case WORKER_INCOME:
		case WORKER_SEED_MULTIPLIER:
			return UEcoFormatter.charFormat(value*100, 5)+"%";

		case WORKER_COUNT_FLAT:
		case WORKER_SEED_FLAT:
		case GRASS_FLAT:
		case SEED_FLAT:
			return UEcoFormatter.makeWhole(UEcoFormatter.charFormat(value, 5));
			
		case CROP_COST_MULTIPLIER:
		case ELEMENT_CROP_COST_MULTIPLIER:
			return (int)value+" rank"+(value>1?"s":"");
			
		case AUTO_CLICKS:
			return (int)value+" click"+(value>1?"s":"")+"/s";
			
		case SEEDS_OVER_TIME:
			return UEcoFormatter.makeWhole(UEcoFormatter.charFormat(value, 5))+" seeds/s";

		default:
			return value + "";
		}
	}
}