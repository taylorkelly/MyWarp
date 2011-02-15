package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

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
	
	protected WarpCommand(WarpList list, Server server, boolean parameter, String... commands) {
		super(list, server, commands);
		this.parameter = parameter;
		if (this.parameter) {
			// Warp, Parameter
			this.length = 2;
		} else {
			// Warp
			this.length = 1;
		}
	}
	
	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		if (!this.isValid(parameters)) {
			return false;
		}
		String creator = "";
		int parameterIndex = 2;
		if (parameters.length == this.length + 1) {
			creator = this.getPlayer(parameters[2]);
			parameterIndex++;
		}
		String parameter = "";
		if (this.parameter) {
			parameter = parameters[parameterIndex];
		}
		this.executeEdit(player, parameters[1], creator, parameter);
		return true;
	}
	
	protected abstract void executeEdit(Player player, String warpName, String creator, String parameter);

	@Override
	public boolean isValid(String[] parameters) {
		return parameters.length == this.length + 1 || parameters.length == this.length + 2;
	}
}
