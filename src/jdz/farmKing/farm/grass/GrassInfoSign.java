
package jdz.farmKing.farm.grass;

import static jdz.UEconomy.UEcoFormatter.charFormat;
import static jdz.UEconomy.UEcoFormatter.makeWhole;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.stats.FarmStats;
import lombok.Data;

@Data
public class GrassInfoSign {
	private final Location location;
	private final Farm farm;
	private final Grass grass;

	@SuppressWarnings("deprecation")
	public void update() {
		Block block = location.getBlock();

		if (block.getType() != Material.WALL_SIGN) {
			block.setType(Material.WALL_SIGN);
			block.setData((byte) 2);
		}

		Sign sign = (Sign) block.getState();
		sign.setLine(0, makeWhole(charFormat(FarmStats.CLICKS_MANUAL.get(farm), 4)) + " Clicks");
		sign.setLine(2, "$ per click:");
		sign.setLine(3, "$" + charFormat(grass.getIncomePerClick(), 4));
		sign.update();
	}
}
