package de.xzise.xwarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

/**
 * Default command structure with a warp definition. The command structure is:
 * <blockquote><code>/warp &lt;command&gt; &lt;warpname&gt; [creator] &lt;parameter&gt;</code></blockquote>
 * The parameter could be disabled.    
 * 
 * @author Fabian Neundorf
 */
public abstract class WarpCommand extends DefaultSubCommand {

	private final int length;
	private final String[] parametersText;
	
	protected WarpCommand(WarpManager list, Server server, String[] parameters, String... commands) {
		super(list, server, commands);
		this.parametersText = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			this.parametersText[i] = "<" + parameters[i] + ">";
		}
		this.length = this.parametersText.length + 1;
	}
	
	protected WarpCommand(WarpManager list, Server server, String parameterText, String... commands) {
		this(list, server, parameterText == null || parameterText.isEmpty() ? new String[0] : new String[] { parameterText }, commands);
	}
	
	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length != this.length + 1 && parameters.length != this.length + 2) {
			return false;
		}
		String owner = "";
		int parameterStartIndex = 2;
		if (parameters.length == this.length + 2) {
			owner = this.getPlayer(parameters[2]);
			parameterStartIndex++;
		}
		String[] parameters2 = new String[this.parametersText.length];
		for (int i = 0; i < parameters2.length; i++) {
			parameters2[i] = parameters[i + parameterStartIndex];
		}
		return this.executeEdit(sender, parameters[1], owner, parameters2);
	}
	
	protected abstract boolean executeEdit(CommandSender sender, String warpName, String owner, String[] parameters);
	
	protected String getParameterText(boolean colorBeginning, boolean colorEnding, int index) {
		return (colorBeginning ? ChatColor.GREEN : "") + this.parametersText[index] + (colorEnding ? ChatColor.WHITE : "");
	}
	
	@Override
	protected String getCommand() {
		StringBuilder commandLine = new StringBuilder("warp " + this.commands[0] + " <name> [owner]");
		for (int i = 0; i < this.parametersText.length; i++) {
			commandLine.append(" " + this.parametersText[i]);
		}
		
		return commandLine.toString();
	}
}
