package com.bukkit.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {

	public ReloadCommand(WarpList list, Server server) {
		super(list, server);
	}
	
	@Override
	public int getPossibility(String[] parameters) {
		if (parameters.length == 1 && parameters[0].equalsIgnoreCase("reload")) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		this.list.loadFromDatabase();
		return true;
	}

}
