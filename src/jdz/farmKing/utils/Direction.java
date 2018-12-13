
package jdz.farmKing.utils;

import org.bukkit.block.BlockFace;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Direction {
	NORTH(0, -1, (byte) 2), EAST(1, 0, (byte) 5), SOUTH(0, 1, (byte) 3), WEST(-1, 0, (byte) 4);
	@Getter private final int dx, dz;
	@Getter private final byte blockData;
	public BlockFace getFace(){
		return BlockFace.valueOf(name());
	}
	public Direction opposite() {
		return values()[(ordinal()+2)%2];
	}
}