package com.bukkit.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class CreateCommand extends SubCommand {

	public CreateCommand(WarpList list, Server server) {
		super(list, server);
	}

	@Override
	public int getPossibility(String[] parameters) {
		if (parameters.length == 2 && (parameters[0].equalsIgnoreCase("create") || parameters[0].equalsIgnoreCase("+") || parameters[0].equalsIgnoreCase("createp") || parameters[0].equalsIgnoreCase("+p") || parameters[0].equalsIgnoreCase("createg") || parameters[0].equalsIgnoreCase("+g"))) {
			return 1;
		}
		return -1;
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		Visibility visibility = Visibility.PUBLIC;
		if (parameters[0].equalsIgnoreCase("createp") || parameters[0].equalsIgnoreCase("+p")) {
			visibility = Visibility.PRIVATE;
		} else if (parameters[0].equalsIgnoreCase("createg") || parameters[0].equalsIgnoreCase("+g")) {
			visibility = Visibility.GLOBAL;
		}
		
		this.list.addWarp(parameters[1], player, visibility);
		return true;
	}

}
