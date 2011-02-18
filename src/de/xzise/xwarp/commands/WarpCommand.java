package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

/**
 * Default command structure with a warp definition. The command structure is:
 * <blockquote><code>/warp &lt;command&gt; &lt;warpname&gt; [command] &lt;parameter&gt;</code></blockquote>
 * The parameter could be disabled.    
 * 
 * @author Fabian Neundorf
 */
public abstract class WarpCommand extends SubCommand {

	private final int length;
	private final boolean parameter;
	private final String parameterText;
	
	protected WarpCommand(WarpList list, Server server, String parameterText, String... commands) {
		super(list, server, commands);
		this.parameter = parameterText != null && !parameterText.isEmpty();
		if (this.parameter) {
			// Warp, Parameter
			this.length = 2;
			this.parameterText = " <" + parameterText + ">";
		} else {
			// Warp
			this.length = 1;
			this.parameterText = "";
		}
	}
	
	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length != this.length + 1 && parameters.length != this.length + 2) {
			return false;
		}
		String creator = "";
		int parameterIndex = 2;
		if (parameters.length == this.length + 2) {
			creator = this.getPlayer(parameters[2]);
			parameterIndex++;
		}
		String parameter = "";
		if (this.parameter) {
			parameter = parameters[parameterIndex];
		}
		return this.executeEdit(sender, parameters[1], creator, parameter);
	}
	
	protected abstract boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter);
	
	@Override
	protected String getCommand() {
		return "warp " + this.commands[0] + " <name> [owner]" + parameterText;
	}
}
