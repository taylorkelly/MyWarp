package de.xzise.xwarp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.taylorkelly.mywarp.WarpList;

/**
 * Command like list/create etc.
 * 
 * @author Fabian Neundorf.
 */
public abstract class SubCommand {

	protected final WarpList list;
	protected final Server server;
	protected final String[] commands;

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
	 * 
	 * @param sender
	 *            Sender of this command.
	 * @param parameters
	 *            Parameters including the command.
	 * @return True if the command could executed.
	 */
	protected abstract boolean internalExecute(CommandSender sender, String[] parameters);

	public final boolean execute(CommandSender sender, String[] parameters) {
		// player.sendMessage(this.getClass().getSimpleName());

		return this.internalExecute(sender, parameters);
	}

	public final String[] getFullHelp() {
		List<String> lines = new ArrayList<String>();
		lines.add(ChatColor.GREEN + this.getCommand());
		for (String string : this.getFullHelpText()) {
			lines.add(string);
		}
		if (this.commands.length > 1) {
			String aliases = "Aliases: ";
			for (int i = 1; i < this.commands.length; i++) {
				aliases += ChatColor.GREEN + this.commands[i];
				if (i < this.commands.length - 1) {
					aliases += ChatColor.WHITE + ", ";
				}
			}
			lines.add(aliases);
		}
		return lines.toArray(new String[0]);
	}

	public final String getSmallHelp() {
		return ChatColor.GREEN + this.getCommand() + ChatColor.WHITE + " - " + this.getSmallHelpText();
	}

	protected abstract String[] getFullHelpText();

	protected abstract String getSmallHelpText();

	protected abstract String getCommand();

	/**
	 * Returns if the sender could use this command and so if list the command
	 * in the help. By default every command is visible, but every command could
	 * override this.
	 * 
	 * @param sender
	 *            The user of the command.
	 * @return If the user could access this command.
	 */
	protected boolean listHelp(CommandSender sender) {
		return true;
	}
}
