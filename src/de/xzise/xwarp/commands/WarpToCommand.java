package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class WarpToCommand extends SubCommand {

	public WarpToCommand(WarpList list, Server server) {
		super(list, server, "to");
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		// TODO ChunkLoading
		int start = 0;
		if (parameters[0].equalsIgnoreCase("to") && (parameters.length == 2 || parameters.length == 3)) {
			start++;
		}
		String creator = "";
		if (parameters.length > start + 1) {
			creator = parameters[start + 1];
		}
		this.list.warpTo(parameters[start], creator, player);
		return true;
	}

	@Override
	public boolean isValid(String[] parameters) {
		return parameters.length == 1 || parameters.length == 2 || (parameters.length == 3 && parameters[0].equalsIgnoreCase("to"));
	}

}
