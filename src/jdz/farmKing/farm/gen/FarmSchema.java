
package jdz.farmKing.farm.gen;

import org.bukkit.Material;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;

import lombok.Data;

@Data
@SuppressWarnings("deprecation")
public class FarmSchema {
	private final CuboidClipboard schematic;
	private final Vector spawn;
	private final int grassHeight;

	public FarmSchema(CuboidClipboard schematic) throws FarmSchemaFormatException {
		this.schematic = schematic;
		this.spawn = calculateSpawn(schematic);
		this.grassHeight = calculateGrassHeight(schematic);
	}

	private Vector calculateSpawn(CuboidClipboard cc) throws FarmSchemaFormatException {
		for (int x = 0; x < cc.getWidth(); ++x)
			for (int y = 0; y < cc.getHeight(); ++y)
				for (int z = 0; z < cc.getLength(); ++z) {
					Vector v = new Vector(x, y, z);
					if (cc.getBlock(v).getType() == 7)
						return v;
				}
		throw new FarmSchemaFormatException("Schematic does not have a Bedrock tile for spawning on!");
	}

	private int calculateGrassHeight(CuboidClipboard cc) throws FarmSchemaFormatException {
		for (int x = 0; x < cc.getWidth(); ++x)
			for (int z = 0; z < cc.getLength(); ++z)
				for (int y = 0; y < cc.getHeight(); ++y)
					if (cc.getBlock(new Vector(x, y, z)).getType() == Material.GRASS.getId())
						return y;
		throw new FarmSchemaFormatException("Schematic does not have grass!");
	}

	public static class FarmSchemaFormatException extends Exception {
		private static final long serialVersionUID = 4154783737477350213L;

		public FarmSchemaFormatException(String reason) {
			super(reason);
		}
	}
}
