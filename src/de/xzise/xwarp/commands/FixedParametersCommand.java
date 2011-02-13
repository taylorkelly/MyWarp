package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;

public abstract class FixedParametersCommand extends SubCommand {

	protected final int parameterCount;
	
	/**
	 * Creates a subcommand which allows fixed sub parameters.
	 * @param list The list to all warps.
	 * @param server The server instance.
	 * @param parameterCount The number of sub parameters (not including the command).
	 * @param commands The commands.
	 * @throws IllegalArgumentException If commands is empty.
	 */
	protected FixedParametersCommand(WarpList list, Server server, int parameterCount, String... commands) {
		super(list, server, commands);
		this.parameterCount = parameterCount + 1;
	}
	
	/**
	 * Creates a subcommand which allows no sub parameters (only the command).
	 * @param list The list to all warps.
	 * @param server The server instance.
	 * @param commands The commands.
	 * @throws IllegalArgumentException If commands is empty.
	 */
	protected FixedParametersCommand(WarpList list, Server server, String... commands) {
		this(list, server, 0, commands);
	}

	@Override
	public final boolean isValid(String[] parameters) {
		return parameters.length == parameterCount;
	}
}
