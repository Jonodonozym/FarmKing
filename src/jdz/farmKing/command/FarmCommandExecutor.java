
package jdz.farmKing.command;

import java.util.Arrays;
import java.util.List;

import jdz.bukkitUtils.commands.CommandExecutor;
import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandExecutorPlayerOnly;
import jdz.farmKing.FarmKing;
import lombok.Getter;

@CommandExecutorPlayerOnly
public class FarmCommandExecutor extends CommandExecutor {
	@Getter private final List<SubCommand> subCommands = Arrays.asList(new FarmGoCommand(), new FarmAlignmentsCommand(),
			new FarmGemReset());

	public FarmCommandExecutor(FarmKing plugin) {
		super(plugin, "f");
	}

}
