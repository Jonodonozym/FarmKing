
package jdz.farmKing.upgrades;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import jdz.farmKing.stats.FarmStats;
import jdz.farmKing.stats.types.FarmStat;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class UpgradeParser {
	public static Upgrade fromConfig(ConfigurationSection section) {
		String name = section.getString("name");
		String description = section.getString("description");
		List<String> upgradeExpressions = section.getStringList("upgrades");
		return parse(name, description, upgradeExpressions);
	}

	public static Upgrade parse(String name, String description, List<String> expressionStrings) {
		int i = 0;

		List<UpgradeBonus> types = new ArrayList<UpgradeBonus>();
		List<UpgradeExpression> expressions = new ArrayList<UpgradeExpression>();

		for (i = 0; i < expressionStrings.size(); i++) {
			try {
				String[] args = expressionStrings.get(i).split(">");
				types.add(UpgradeBonus.valueOf(args[0].trim()));
				expressions.add(parseExpression(args[1].trim()));
			}
			catch (Exception e) {
				System.out.println("couldn't parse expression: " + expressionStrings.get(i));
				e.printStackTrace();
			}
		}

		return new Upgrade(name, description, types, expressions);
	}

	private static UpgradeExpression parseExpression(String input) throws ParseException {
		List<FarmStat> stats = new ArrayList<FarmStat>();

		input = input.toLowerCase();

		Set<String> varSet = new HashSet<String>();
		for (FarmStat type : FarmStats.getValues()) {
			String label = type.getName().toLowerCase().replaceAll("_", " ");
			if (input.contains("[" + label + "]")) {
				varSet.add(label);
				stats.add(type);
			}
		}

		Expression exp = new ExpressionBuilder(input).variables(varSet).build();

		return new UpgradeExpression(exp, stats);
	}
}
