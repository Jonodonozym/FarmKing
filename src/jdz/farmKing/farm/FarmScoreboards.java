
package jdz.farmKing.farm;

import static jdz.UEconomy.UEcoFormatter.charFormat;
import static jdz.UEconomy.UEcoFormatter.makeWhole;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Objective;

import jdz.UEconomy.data.UEcoBank;
import jdz.bukkitUtils.events.Listener;
import jdz.farmKing.element.Element;
import jdz.farmKing.farm.data.PlayerFarms;
import jdz.farmKing.stats.EventFlag;
import jdz.farmKing.stats.FarmStats;
import static net.md_5.bungee.api.ChatColor.*;

public class FarmScoreboards implements Listener {
	private static final Map<Player, PlayerScoreboardData> scoreboards = new HashMap<>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		createScoreboard(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		scoreboards.remove(e.getPlayer());
	}

	public static void updateScoreboard(Player player) {
		PlayerScoreboardData data = scoreboards.get(player);

		updateBalance(data);
		updateIncome(data);
		updateGems(player);
		updateSeeds(data);
		updateWorkers(data);
	}

	private static void updateBalance(PlayerScoreboardData data) {
		data.getScoreboard().resetScores(data.getBalance());
		data.setBalance(GREEN + "$" + charFormat(UEcoBank.get(data.getOwner()), 4));
		data.getObjective().getScore(data.getBalance()).setScore(6);
	}

	private static void updateIncome(PlayerScoreboardData data) {
		Farm farm = PlayerFarms.get(data.getOwner());
		data.getScoreboard().resetScores(data.getIncome());
		data.setIncome(GREEN + "$" + charFormat(farm == null ? 0 : farm.currentIncome, 4) + "/s");
		data.getObjective().getScore(data.getIncome()).setScore(3);
	}

	public static void updateGems(Player player) {
		PlayerScoreboardData data = scoreboards.get(player);
		Farm farm = PlayerFarms.get(player);

		data.getScoreboard().resetScores(data.getGems());
		data.setGems(GREEN + "" + makeWhole(charFormat(FarmStats.GEMS.get(farm), 4)));
		data.getObjective().getScore(data.getGems()).setScore(0);
	}

	private static void updateSeeds(PlayerScoreboardData data) {
		Farm farm = PlayerFarms.get(data.getOwner());

		if (farm == null || !EventFlag.ALIGNMENTS_UNLOCKED.isComplete(farm))
			return;

		for (int i = 0; i < data.seedLines(); i++)
			data.getScoreboard().resetScores(data.getSeedLine(i));

		for (Element element : Element.values()) {
			double seeds = FarmStats.SEEDS(element).get(farm);
			data.setSeed(element, element.color + makeWhole(charFormat(seeds, 4)));
		}

		for (int i = 0; i < data.seedLines(); i++)
			data.getObjective().getScore(data.getSeedLine(i)).setScore(-6 - i);
	}

	private static void updateWorkers(PlayerScoreboardData data) {
		Farm farm = PlayerFarms.get(data.getOwner());

		if (farm == null || !EventFlag.WORKERS_UNLOCKED.isComplete(farm))
			return;

		data.getScoreboard().resetScores(data.getWorkers());
		data.setWorkers(GREEN + "" + makeWhole(charFormat(FarmStats.WORKERS.get(farm), 4)) + " ");
		data.getObjective().getScore(data.getWorkers()).setScore(-3);
	}

	private static void createScoreboard(Player player) {
		PlayerScoreboardData data = new PlayerScoreboardData(player);
		scoreboards.put(player, data);

		Objective obj = data.getObjective();

		obj.setDisplayName(BOLD + "" + YELLOW + "Farm Details");
		obj.getScore(" ").setScore(8);

		obj.getScore(WHITE + "Balance").setScore(7);
		obj.getScore(data.getBalance()).setScore(6);
		obj.getScore("  ").setScore(5);

		obj.getScore(WHITE + "Income").setScore(4);
		obj.getScore(data.getIncome()).setScore(3);
		obj.getScore("   ").setScore(2);

		obj.getScore(WHITE + "Gems").setScore(1);
		obj.getScore(data.getGems()).setScore(0);

		Farm farm = PlayerFarms.get(player);
		if (farm == null)
			return;

		if (EventFlag.ALIGNMENTS_UNLOCKED.isComplete(farm))
			addSeedSection(player);
		if (EventFlag.WORKERS_UNLOCKED.isComplete(farm))
			addWorkerSection(player);

		updateGems(player);
		updateScoreboard(player);
	}

	public static void addSeedSection(Player player) {
		PlayerScoreboardData data = scoreboards.get(player);
		Objective sb = data.getObjective();

		sb.getScore("     ").setScore(-4);
		sb.getScore(WHITE + "Seeds").setScore(-5);

		updateSeeds(data);
	}

	public static void addWorkerSection(Player player) {
		PlayerScoreboardData data = scoreboards.get(player);
		Objective sb = data.getObjective();

		sb.getScore("    ").setScore(-1);
		sb.getScore(WHITE + "Workers").setScore(-2);

		updateWorkers(data);
	}
}
