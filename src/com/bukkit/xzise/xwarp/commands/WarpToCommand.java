package com.bukkit.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class WarpToCommand extends SubCommand {

	public WarpToCommand(WarpList list, Server server) {
		super(list, server);
	}

	@Override
	public int getPossibility(String[] parameters) {
		if (parameters.length > 0 && parameters.length <= 3) {
			if (parameters.length > 1 && parameters[0].equalsIgnoreCase("to")) {
				return 1;
			} else if (parameters.length <= 2) {
				return 0;
			}
		}
		return -1;
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
		this.list.warpTo(parameters[start], creator, player, start == 1);
		return true;
	}

}
