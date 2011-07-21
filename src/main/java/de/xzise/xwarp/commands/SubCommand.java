package de.xzise.xwarp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Basical subcommand without any list.
 * @author Fabian Neundorf.
 */
public abstract class SubCommand implements CommandExecutor {

	protected final String[] commands;

	/**
	 * Creates a subcommand.
	 * @param commands
	 *            The commands.
	 * @throws IllegalArgumentException
	 *             If commands is empty.
	 */
	protected SubCommand(String... commands) {
		if (commands.length <= 0) {
			throw new IllegalArgumentException("No command given!");
		}
		this.commands = commands;
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


	public String[] getCommands() {
		return this.commands.clone();
	}

	public final String[] getFullHelp() {
		List<String> lines = new ArrayList<String>();
		lines.add("xWarp help: " + ChatColor.GREEN + this.getCommand());
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    return this.execute(sender, args);
	}
}
