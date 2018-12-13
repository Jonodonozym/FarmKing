
package jdz.farmKing.farm;

import static net.md_5.bungee.api.ChatColor.GREEN;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import jdz.farmKing.element.Element;
import lombok.Data;

@Data
public class PlayerScoreboardData {
	private final Player owner;
	private final Scoreboard scoreboard;
	private final Objective objective;

	private String balance = GREEN + "$0";
	private String income = GREEN + "$0/s";
	private String gems = GREEN + "0";
	private String workers = GREEN + "0 ";

	private final Map<Element, String> seeds = new HashMap<>();

	public PlayerScoreboardData(Player player) {
		this.owner = player;
		this.scoreboard = createScoreboard(player);
		this.objective = createObjective(scoreboard);

		for (Element element : Element.values())
			seeds.put(element, element.color + "0");
	}

	private Scoreboard createScoreboard(Player player) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		player.setScoreboard(scoreboard);
		return scoreboard;
	}

	private Objective createObjective(Scoreboard scoreboard) {
		Objective obj = scoreboard.registerNewObjective("scoreboard", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		return obj;
	}

	public void setSeed(Element element, String string) {
		seeds.put(element, string);
	}

	public String getSeed(Element element) {
		return seeds.get(element);
	}

	public int seedLines() {
		return (int) Math.ceil(Element.values().size() / 2.0);
	}

	public String getSeedLine(int line) {
		String lineString = "";
		for (int i = line * 2; i < (line + 1) * 2; i++)
			lineString += seeds.get(Element.values().get(i)) + " ";
		return lineString.substring(0, lineString.length() - 1);
	}
}
