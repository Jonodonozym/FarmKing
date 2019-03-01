
package jdz.farmKing.farm.grass;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;
import jdz.farmKing.farm.Farm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GrassBreakEvent extends Event implements Cancellable {
	private final Farm farm;
	private final Block block;
	private final Player player;

	public static HandlerList getHandlerList() {
		return getHandlers(GrassBreakEvent.class);
	}
}
