package jdz.farmKing.upgrades;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.StatType;

public abstract class Upgrade {
	public static ChatColor loreColor = ChatColor.BLUE;

	private final String name;
	private final String description;
	private final List<String> lore;
	private final List<UpgradeType> types;

	private Upgrade(String name, String description, List<UpgradeType> types) {
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
	}

	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return lore;
	}

	public String getDescription() {
		return description;
	}

	public int getNumBonuses() {
		return types.size();
	}

	public UpgradeType getType(int i) {
		return types.get(i);
	};

	public abstract double getBonus(int i, Farm f);

	public abstract boolean isDisplayable(int i);

	public static Upgrade fromConstant(String name, String description, UpgradeType type, double constant) {
		List<UpgradeType> list = new ArrayList<UpgradeType>();
		list.add(type);
		return new Upgrade(name, description, list) {
			@Override
			public double getBonus(int i, Farm f) {
				return constant;
			}

			@Override
			public boolean isDisplayable(int i) {
				return false;
			}

		};
	}

	public static Upgrade fromStat(String name, String description, UpgradeType type, StatType statType) {
		List<UpgradeType> list = new ArrayList<UpgradeType>();
		list.add(type);
		return new Upgrade(name, description, list) {
			@Override
			public double getBonus(int i, Farm f) {
				return f.getStat(statType);
			}

			@Override
			public boolean isDisplayable(int i) {
				return true;
			}

		};
	}

	public static Upgrade fromStatMax(String name, String description, UpgradeType type, StatType statType) {
		List<UpgradeType> list = new ArrayList<UpgradeType>();
		list.add(type);
		return new Upgrade(name, description, list) {
			@Override
			public double getBonus(int i, Farm f) {
				return f.getStatMax(statType);
			}

			@Override
			public boolean isDisplayable(int i) {
				return true;
			}

		};
	}

	public static Upgrade fromStatCumulative(String name, String description, UpgradeType type, StatType statType) {
		List<UpgradeType> list = new ArrayList<UpgradeType>();
		list.add(type);
		return new Upgrade(name, description, list) {
			@Override
			public double getBonus(int i, Farm f) {
				return f.getStatCumulative(statType);
			}

			@Override
			public boolean isDisplayable(int i) {
				return true;
			}

		};
	}

	public Upgrade multiply(double constant) {
		return multiply(0, constant);
	}

	public Upgrade add(double constant) {
		return add(0, constant);
	}

	public Upgrade power(double constant) {
		return power(0, constant);
	}

	public Upgrade log() {
		return log(0);
	}

	public Upgrade multiply(int index, double constant) {
		Upgrade self = this;
		if (this.getNumBonuses() > 1)
			return new Upgrade(name, description, types) {
				@Override
				public double getBonus(int i, Farm f) {
					if (i == index)
						return self.getBonus(i, f) * constant;
					return self.getBonus(i, f);
				}

				@Override
				public boolean isDisplayable(int i) {
					return self.isDisplayable(i);
				}
			};
		return new Upgrade(name, description, types) {
			@Override
			public double getBonus(int i, Farm f) {
				return self.getBonus(i, f) * constant;
			}

			@Override
			public boolean isDisplayable(int i) {
				return self.isDisplayable(i);
			}
		};
	}

	public Upgrade add(int index, double constant) {
		Upgrade self = this;
		if (this.getNumBonuses() > 1)
			return new Upgrade(name, description, types) {
				@Override
				public double getBonus(int i, Farm f) {
					if (i == index)
						return self.getBonus(i, f) + constant;
					return self.getBonus(i, f);
				}

				@Override
				public boolean isDisplayable(int i) {
					return self.isDisplayable(i);
				}
			};
		return new Upgrade(name, description, types) {
			@Override
			public double getBonus(int i, Farm f) {
				return self.getBonus(i, f) + constant;
			}

			@Override
			public boolean isDisplayable(int i) {
				return self.isDisplayable(i);
			}
		};
	}

	public Upgrade power(int index, double constant) {
		Upgrade self = this;
		if (this.getNumBonuses() > 1)
			return new Upgrade(name, description, types) {
				@Override
				public double getBonus(int i, Farm f) {
					if (i == index)
						return Math.pow(self.getBonus(i, f), constant);
					return self.getBonus(i, f);
				}

				@Override
				public boolean isDisplayable(int i) {
					return self.isDisplayable(i);
				}
			};
		return new Upgrade(name, description, types) {
			@Override
			public double getBonus(int i, Farm f) {
				return Math.pow(self.getBonus(i, f), constant);
			}

			@Override
			public boolean isDisplayable(int i) {
				return self.isDisplayable(i);
			}
		};

	}

	public Upgrade log(int index) {
		Upgrade self = this;
		if (this.getNumBonuses() > 1)
			return new Upgrade(name, description, types) {
				@Override
				public double getBonus(int i, Farm f) {
					if (i == index)
						return Math.log(self.getBonus(i, f));
					return self.getBonus(i, f);
				}

				@Override
				public boolean isDisplayable(int i) {
					return self.isDisplayable(i);
				}
			};
		return new Upgrade(name, description, types) {
			@Override
			public double getBonus(int i, Farm f) {
				return Math.log(self.getBonus(i, f));
			}

			@Override
			public boolean isDisplayable(int i) {
				return self.isDisplayable(i);
			}
		};

	}

	public Upgrade addUpgrade(Upgrade other) {
		List<UpgradeType> list = new ArrayList<UpgradeType>(types);
		list.addAll(other.types);
		Upgrade self = this;
		return new Upgrade(name, description, list) {

			@Override
			public double getBonus(int i, Farm f) {
				if (i < self.getNumBonuses())
					return self.getBonus(i, f);
				return other.getBonus(i - self.getNumBonuses(), f);
			}

			@Override
			public boolean isDisplayable(int i) {
				if (i < self.getNumBonuses())
					return self.isDisplayable(i);
				return other.isDisplayable(i - self.getNumBonuses());
			}

		};
	}
}
