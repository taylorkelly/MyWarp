package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.taylorkelly.mywarp.WarpList;

/**
 * Command like list/create etc.
 * @author Fabian Neundorf.
 */
public abstract class SubCommand {
	
	protected final WarpList list;
	protected final Server server;
	protected final String[] commands;
	
	/**
	 * Creates a subcommand.
	 * @param list The list to all warps.
	 * @param server The server instance.
	 * @param commands The commands.
	 * @throws IllegalArgumentException If commands is empty.
	 */
	protected SubCommand(WarpList list, Server server, String... commands) {
		if (commands.length <= 0) {
			throw new IllegalArgumentException("No command given!");
		}
		this.list = list;
		this.server = server;
		this.commands = commands;
	}

	protected String getPlayer(String name) {
		Player player = this.server.getPlayer(name);
		return player == null ? name : player.getName();
	}
	
	public String[] getCommands() {
		return this.commands.clone();
	}
		
	/**
	 * Executes the command. If the command could executed it return true.
	 * @param sender Sender of this command.
	 * @param parameters Parameters including the command.
	 * @return True if the command could executed.
	 */
	protected abstract boolean internalExecute(CommandSender sender, String[] parameters);
	
	public final boolean execute(CommandSender sender, String[] parameters) {
//		player.sendMessage(this.getClass().getSimpleName());
		
		return this.internalExecute(sender, parameters);
	}
}
