
package farmKing.achievements;

public class Achievement {
	public final StaticAchievement achievement;
	public boolean isAchieved = false;
	
	public Achievement(StaticAchievement achievement){
		this.achievement = achievement;
	}
	
	public StaticAchievement getAchievement(){
		return achievement;
	}
}
