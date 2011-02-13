package de.xzise.xwarp.commands;

import java.util.Arrays;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public abstract class EditCommand extends SubCommand {

	private final int length;
	private final boolean parameter;
	private final String[] commands;
	
	protected EditCommand(WarpList list, Server server, boolean parameter, String[] commands) {
		super(list, server);
		this.parameter = parameter;
		if (this.parameter) {
			// Warp, Parameter
			this.length = 2;
		} else {
			// Warp
			this.length = 1;
		}
		this.commands = commands;
	}
	
	protected EditCommand(WarpList list, Server server, boolean parameter, String command, String... commands) {
		this(list, server, parameter, concat(command, commands));
	}	
	
	private static String[] concat(String string, String... strings) {
		String[] result = Arrays.copyOf(strings, strings.length + 1);
		result[strings.length] = string;
		return result;
	}
	
	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		if (parameters.length < this.length || parameters.length > this.length + 1) {
			return false;
		}
		String creator = "";
		int parameterIndex = 1;
		if (parameters.length == this.length + 1) {
			creator = parameters[1];
			parameterIndex++;
		}
		String parameter = "";
		if (this.parameter) {
			parameter = parameters[parameterIndex];
		}
		this.executeEdit(player, parameters[0], creator, parameter);
		return true;
	}
	
	protected abstract void executeEdit(Player player, String warpName, String creator, String parameter);

	@Override
	public int getPossibility(String[] parameters) {
		if (parameters.length < this.length + 1 || parameters.length > this.length + 2) {
			return -1; // To many/Not enough parameters.
		}
		String command = parameters[0];
		for (String string : this.commands) {
			if (string.equalsIgnoreCase(command)) {
				return 1;
			}
		}
		return -1;
	}

}
