
package jdz.farmKing.farm.generation;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

import jdz.bukkitUtils.misc.Pair;
import jdz.farmKing.farm.Farm;
import jdz.farmKing.utils.Direction;
import lombok.Data;
import lombok.Getter;

@Data
@SuppressWarnings("deprecation")
public class FarmSchema {
	private final CuboidClipboard schematic;
	private final Vector spawn;
	@Getter private final int grassHeight;
	private final List<Pair<Vector, Direction>> cropLocations;

	public FarmSchema(CuboidClipboard schematic) throws FarmSchemaFormatException {
		this.schematic = schematic;
		this.spawn = calculateSpawn(schematic);
		this.grassHeight = calculateGrassHeight(schematic);
		this.cropLocations = calculateCropLocations(schematic);
	}

	private Vector calculateSpawn(CuboidClipboard cc) throws FarmSchemaFormatException {
		for (int x = 0; x < cc.getSize().getBlockX(); ++x)
			for (int y = 0; y < cc.getSize().getBlockY(); ++y)
				for (int z = 0; z < cc.getSize().getBlockZ(); ++z) {
					Vector location = new Vector(x, y, z);
					if (cc.getBlock(location).getType() == 7)
						return location;
				}
		throw new FarmSchemaFormatException("Schematic does not have a Bedrock tile for spawning on!");
	}

	private int calculateGrassHeight(CuboidClipboard cc) throws FarmSchemaFormatException {
		for (int x = 0; x < cc.getSize().getBlockX(); ++x)
			for (int y = 0; y < cc.getSize().getBlockY(); ++y)
				for (int z = 0; z < cc.getSize().getBlockZ(); ++z)
					if (cc.getBlock(new Vector(x, y, z)).getType() == Material.GRASS.getId())
						return y;
		throw new FarmSchemaFormatException("Schematic does not have grass!");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Pair<Vector, Direction>> calculateCropLocations(CuboidClipboard cc) throws FarmSchemaFormatException {
		Pair<Vector, Direction>[] locations = new Pair[16];
		for (int x = 0; x < cc.getSize().getBlockX(); ++x)
			for (int y = 0; y < cc.getSize().getBlockY(); ++y)
				for (int z = 0; z < cc.getSize().getBlockZ(); ++z) {
					Vector location = new Vector(x, y, z);
					BaseBlock block = cc.getBlock(location);
					if (block.getType() == Material.DISPENSER.getId()) {
						Direction direction = getDirectionForCrop(cc, location);
						int index = getIndexForCrop(cc, location, direction);
						locations[index] = new Pair(location, direction);
					}
				}
		return Arrays.asList(locations);
	}

	private Direction getDirectionForCrop(CuboidClipboard cc, Vector cropLocation) throws FarmSchemaFormatException {
		for (Direction direction : Direction.values()) {
			BaseBlock block = cc.getBlock(cropLocation.add(direction.getDx(), 0, direction.getDz()));
			if (block.getType() == Material.WALL_SIGN.getId())
				return direction;
		}
		throw new FarmSchemaFormatException("Crop at " + cropLocation + " in schema doesn't have ID sign");
	}

	private int getIndexForCrop(CuboidClipboard cc, Vector cropLocation, Direction direction) {
		BaseBlock block = cc.getBlock(cropLocation.add(direction.getDx(), 0, direction.getDz()));
		return Integer.parseInt(block.getNbtData().getString("Text1"));
	}

	public static class FarmSchemaFormatException extends Exception {
		private static final long serialVersionUID = 4154783737477350213L;

		public FarmSchemaFormatException(String reason) {
			super(reason);
		}
	}

	public Location getSpawn(Farm farm) {
		return farm.getOrigin().clone().add(spawn.getX(), spawn.getY(), spawn.getZ());
	}

	public Location getCropLocation(Farm farm, int cropId) {
		Vector cropLocation = cropLocations.get(cropId).getKey();
		return farm.getOrigin().clone().add(cropLocation.getX(), cropLocation.getY(), cropLocation.getZ());
	}

	public Direction getCropDirection(int cropId) {
		return cropLocations.get(cropId).getValue();
	}
	
	public Vector getSize() {
		return schematic.getSize();
	}
}
