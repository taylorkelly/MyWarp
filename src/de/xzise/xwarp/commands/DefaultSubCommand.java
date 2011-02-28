package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import me.taylorkelly.mywarp.WarpList;

/**
 * Command like list/create etc.
 * 
 * @author Fabian Neundorf.
 */
public abstract class DefaultSubCommand extends SubCommand {

	protected final WarpList list;
	protected final Server server;

	/**
	 * Creates a subcommand.
	 * 
	 * @param list
	 *            The list to all warps.
	 * @param server
	 *            The server instance.
	 * @param commands
	 *            The commands.
	 * @throws IllegalArgumentException
	 *             If commands is empty.
	 */
	protected DefaultSubCommand(WarpList list, Server server, String... commands) {
		super(commands);
		this.list = list;
		this.server = server;
	}

	protected String getPlayer(String name) {
		Player player = this.server.getPlayer(name);
		return player == null ? name : player.getName();
	}
}
