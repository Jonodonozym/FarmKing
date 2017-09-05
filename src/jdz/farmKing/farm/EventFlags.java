
package jdz.farmKing.farm;

public enum EventFlags {
	ALIGNMENTS_UNLOCKED;
	
	public boolean isAchieved(Farm f){
		switch(this){
			case ALIGNMENTS_UNLOCKED: return (f.isLevel > 0 || f.getStat(StatType.FARM_GEMS) > 2e9);
			default: return false;
		}
	}
}
