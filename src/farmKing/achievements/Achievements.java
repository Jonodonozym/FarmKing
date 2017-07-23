package farmKing.achievements;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.jonodonozym.UPEconomy.UPEconomyAPI;

import farmKing.crops.CropType;
import farmKing.crops.Farm;

public class Achievements {
	private static final List<Achievement> achievements;

	public static List<Achievement> getList() {
		return achievements;
	}

	static {
		achievements = new ArrayList<Achievement>();

		// =======================\\
		// plant quantity series \\
		// =======================\\

		final String[] plantSeriesNames = new String[] { " Farmer", " Enthusiast", " Cultivator", " Researcher",
				" Entrepreneur", " Obsession", " Extravaganza", " Maniac", " Fetish" };
		final int[] plantSeriesRequirements = new int[] { 1, 10, 100, 250, 500, 1000, 2500, 5000, 10000 };

		for (int i = 0; i < CropType.cropTypes.size(); i++) {
			CropType type = CropType.cropTypes.get(i);
			final int cropIndex = i;
			achievements.add(new AchievementSeries(type.material) {
				@Override
				public List<Achievement> initAchievements() {
					List<Achievement> A = new ArrayList<Achievement>();
					for (int ii = 0; ii < plantSeriesNames.length; ii++) {
						final int seriesIndex = ii;
						A.add(new Achievement(type.name + plantSeriesNames[ii],
								"Obtain " + plantSeriesRequirements[ii] + " " + type.name + " plants", null) {
							@Override
							protected boolean checkAchieved(Farm f) {
								return (f.crops[cropIndex].getQuantity() > plantSeriesRequirements[seriesIndex]);
							}
						});
					}
					return A;
				}
			});
		}

		// =======================\\
		// total quantity series \\
		// =======================\\

		achievements.add(new AchievementSeries(Material.WOOD_HOE) {
			@Override
			public List<Achievement> initAchievements() {
				List<Achievement> A = new ArrayList<Achievement>();
				for (int ii = 0; ii < plantSeriesNames.length; ii++) {
					final int seriesIndex = ii;
					A.add(new Achievement("Plant" + plantSeriesNames[ii],
							"Obtain " + plantSeriesRequirements[ii] * 10 + " total plants", null) {
						@Override
						protected boolean checkAchieved(Farm f) {
							return (f.numPlants > plantSeriesRequirements[seriesIndex] * 10);
						}
					});
				}
				return A;
			}
		});

		// ==============\\
		// money series \\
		// ==============\\

		final double[] moneySeriesRequirements = new double[] { 1e3, 1e6, 1e9, 1e15, 1e21, 1e27, 1e33, 1e39 };
		achievements.add(new AchievementSeries(Material.GOLD_INGOT) {
			@Override
			public List<Achievement> initAchievements() {
				List<Achievement> A = new ArrayList<Achievement>();
				for (int ii = 0; ii < moneySeriesRequirements.length; ii++) {
					final int seriesIndex = ii;
					A.add(new Achievement("Green Fingers " + (ii + 1) + "/" + (moneySeriesRequirements.length - 1),
							"Have $" + UPEconomyAPI.charFormat(moneySeriesRequirements[ii], 1) + " in your bank",
							null) {
						@Override
						protected boolean checkAchieved(Farm f) {
							return (UPEconomyAPI.getBalance(f.owner) > moneySeriesRequirements[seriesIndex]);
						}
					});
				}
				return A;
			}
		});

		// =============\\
		// Gems series \\
		// =============\\

		final double[] gemSeriesRequirements = new double[] { 100, 500, 5e3, 1e6, 1e9, 1e12, 1e15, 1e21 };
		achievements.add(new AchievementSeries(Material.DIAMOND) {
			@Override
			public List<Achievement> initAchievements() {
				List<Achievement> A = new ArrayList<Achievement>();
				for (int ii = 0; ii < gemSeriesRequirements.length; ii++) {
					final int seriesIndex = ii;
					A.add(new Achievement("Gem Hoarder " + (ii + 1) + "/" + (gemSeriesRequirements.length - 1),
							"Obtain " + UPEconomyAPI.charFormat(gemSeriesRequirements[ii], 1) + " gems", null) {
						@Override
						protected boolean checkAchieved(Farm f) {
							return (f.gems > gemSeriesRequirements[seriesIndex]);
						}
					});
				}
				return A;
			}
		});

		// ===================\\
		// tall grass series \\
		// ===================\\

		final String[] grassSeriesNames = new String[] { " Farmer", " Hater", " Destroyer", " Devastator", " Purger",
				" Annihilator" };
		final int[] grassSeriesRequirements = new int[] { 100, 2500, 10000, 100000, 500000, 1000000 };
		achievements.add(new AchievementSeries(Material.SHEARS) {
			@Override
			public List<Achievement> initAchievements() {
				List<Achievement> A = new ArrayList<Achievement>();
				for (int ii = 0; ii < grassSeriesRequirements.length; ii++) {
					final int seriesIndex = ii;
					A.add(new Achievement("Tall Grass" + grassSeriesNames[ii],
							"Obtain " + grassSeriesRequirements[ii] + " total plants", null) {
						@Override
						protected boolean checkAchieved(Farm f) {
							return (f.totalClicks > grassSeriesRequirements[seriesIndex]);
						}
					});
				}
				return A;
			}
		});
	


	// =================== \\
	// Offline time series \\
	// =================== \\

	final int[] offlineTimeSeriesRequirements = new int[] { 60, 240, 480, 720, 1440, 2880, 7200};
	achievements.add(new AchievementSeries(Material.DIAMOND) {
		@Override
		public List<Achievement> initAchievements() {
			List<Achievement> A = new ArrayList<Achievement>();
			for (int ii = 0; ii < offlineTimeSeriesRequirements.length; ii++) {
				final int seriesIndex = ii;
				String s = offlineTimeSeriesRequirements[seriesIndex] < 1440?
						offlineTimeSeriesRequirements[seriesIndex]/60 + " hours":
							offlineTimeSeriesRequirements[seriesIndex]/1440 + " days";
				A.add(new Achievement("Fast Asleep " + (ii + 1) + "/" + (offlineTimeSeriesRequirements.length - 1),
						"Have a total offline time of at least "+s, null) {
					@Override
					protected boolean checkAchieved(Farm f) {
						return (f.totalOfflineTimeMinutes > offlineTimeSeriesRequirements[seriesIndex]);
					}
				});
			}
			return A;
		}
	});
	



	// ================== \\
	// Online time series \\
	// ================== \\

	final int[] onlineTimeSeriesRequirements = new int[] { 5, 15, 30, 45, 60, 120, 240, 1440};
	achievements.add(new AchievementSeries(Material.DIAMOND) {
		@Override
		public List<Achievement> initAchievements() {
			List<Achievement> A = new ArrayList<Achievement>();
			for (int ii = 0; ii < onlineTimeSeriesRequirements.length; ii++) {
				final int seriesIndex = ii;
				String s = onlineTimeSeriesRequirements[ii] < 60?
						onlineTimeSeriesRequirements[ii]+" minutes":
							(onlineTimeSeriesRequirements[ii]/60)+" hours";
				A.add(new Achievement("Wide awake " + (ii + 1) + "/" + (onlineTimeSeriesRequirements.length - 1),
						"Have a total online time this reset of "+s, null) {
					@Override
					protected boolean checkAchieved(Farm f) {
						return (f.onlineTimeMinutes > onlineTimeSeriesRequirements[seriesIndex]);
					}
				});
			}
			return A;
		}
	});

	}
}
