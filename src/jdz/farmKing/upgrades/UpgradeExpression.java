
package jdz.farmKing.upgrades;

import java.util.ArrayList;
import java.util.List;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.stats.types.FarmStat;
import net.objecthunter.exp4j.Expression;

public class UpgradeExpression {
	private final List<FarmStat> stats;
	private final Expression exp;

	public UpgradeExpression(Expression exp, List<FarmStat> stats) {
		this.exp = exp;
		this.stats = new ArrayList<FarmStat>(stats);
	}

	public double evaluate(Farm f) {
		for (FarmStat stat : stats)
			exp.setVariable(stat.getName(), stat.get(f));
		return exp.evaluate();
	}

	public boolean isDisplayable() {
		return !stats.isEmpty();
	}
}
