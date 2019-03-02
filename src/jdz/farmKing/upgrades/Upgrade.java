
package jdz.farmKing.upgrades;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import jdz.farmKing.farm.Farm;
import lombok.Getter;

public class Upgrade{
	public static ChatColor loreColor = ChatColor.BLUE;

	@Getter private final String name;
	@Getter private final String description;
	@Getter private final List<String> lore;
	private final List<UpgradeBonus> types;
	private final List<UpgradeExpression> expressions;

	public Upgrade(String name, String description, List<UpgradeBonus> types, List<UpgradeExpression> expressions) {
		if (name == null)
			name = "";
		if (description == null)
			description = "";
		
		this.name = name;
		this.description = description;

		this.lore = new ArrayList<String>();
		String[] words = description.split(" ");
		String currentString = "";
		for (String s : words) {
			if (currentString.length() + s.length() > 35) {
				this.lore.add(loreColor + currentString);
				currentString = "";
			}
			currentString = currentString + s + " ";
		}
		this.lore.add(loreColor + currentString);

		this.types = types;
		this.expressions = expressions;
	}
	
	public static Upgrade emptyUpgrade(){
		return new Upgrade("","",new ArrayList<UpgradeBonus>(), new ArrayList<UpgradeExpression>());
	}

	public int getNumBonuses() {
		return types.size();
	}

	public UpgradeBonus getType(int i) {
		return types.get(i);
	}
	
	public double getBonus(int i, Farm f) {
		return expressions.get(i).evaluate(f);
	}

	public boolean isDisplayable(int i) {
		return expressions.get(i).isDisplayable();
	}
}
