
package jdz.farmKing.farm.generation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.DataException;

import jdz.farmKing.FarmKing;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.farm.generation.FarmSchema.FarmSchemaFormatException;

@SuppressWarnings("deprecation")
public class FarmGenerator {
	public static final int ISLAND_WIDTH = 150, ISLAND_LENGTH = 150, ISLAND_HEIGHT = 100,
			SPAWN_BORDER = ISLAND_WIDTH * 2;

	public static List<FarmSchema> schematics = new ArrayList<FarmSchema>();

	static {
		File datafolder = new File(FarmKing.getInstance().getDataFolder(), "schematics");
		if (!datafolder.exists())
			datafolder.mkdirs();

		File[] files = datafolder.listFiles();

		for (File file : files)
			if (file.getName().startsWith("farmLevel")) {
				try {
					CuboidClipboard schematic = CuboidClipboard.loadSchematic(file);
					schematics.add(new FarmSchema(schematic));
				}
				catch (IOException | DataException | FarmSchemaFormatException e) {
					System.out.println("Error loading schematic: " + e);
					e.printStackTrace();
				}
			}
	}

	// TODO decipher this bullshit
	public static Farm generate(int index) {
		int row = 0;
		int temp = index;
		while (temp >= 12 + row * 4)
			temp -= 12 + (row++) * 4;

		int x1 = -SPAWN_BORDER - ISLAND_WIDTH * (row + 1);
		int x2 = SPAWN_BORDER + ISLAND_WIDTH * row;
		int z1 = -SPAWN_BORDER - ISLAND_HEIGHT * (row + 1);
		int z2 = SPAWN_BORDER + ISLAND_HEIGHT * row;

		int dx = 0, dz = 0, x = 0, z = 0;
		switch (temp / 4) {
		case 0:
			dx = 1;
			x = x1;
			z = z1;
			break;
		case 1:
			dz = 1;
			x = x2;
			z = z1;
			break;
		case 2:
			dx = -1;
			x = x2;
			z = z2;
			break;
		case 3:
			dz = -1;
			x = x1;
			z = z2;
			break;
		}

		int remainder = (temp / 4) * 4 - temp;
		int xx = x + dx * remainder * ISLAND_WIDTH;
		int zz = z + dz * remainder * ISLAND_LENGTH;

		generateBlocks(index, xx, zz);
		return new Farm(index, xx, zz);
	}

	public static void generateBlocks(Farm farm) {
		generateBlocks(farm.getLevel(), farm.getOrigin().getBlockX(), farm.getOrigin().getBlockZ());
	}

	public static void generateBlocks(int level, int x, int z) {
		FarmSchema schematic = getSchematicForLevel(level);
		Vector origin = getOrigin(x, z, schematic);
		pasteSchema(schematic, origin, Bukkit.getWorlds().get(0));
	}

	public static FarmSchema getSchematicForLevel(int farmLevel) {
		if (farmLevel < 0)
			farmLevel = 0;
		if (farmLevel >= schematics.size())
			farmLevel = schematics.size() - 1;
		return schematics.get(farmLevel);
	}

	private static Vector getOrigin(int worldX, int worldZ, FarmSchema schematic) {
		return new Vector(worldX, ISLAND_HEIGHT, worldZ).subtract(schematic.getSchematic().getOffset());
	}

	private static void pasteSchema(FarmSchema schematic, Vector location, World world) {
		try {
			schematic.getSchematic().paste(new EditSession(new BukkitWorld(world), 999999999), location, false, false);
		}
		catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}

}
