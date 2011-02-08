package com.bukkit.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.bukkit.xzise.xwarp.PermissionWrapper;
import com.bukkit.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class PermissionsCommand extends SubCommand {

	public PermissionsCommand(WarpList list, Server server) {
		super(list, server);
	}

	@Override
	public int getPossibility(String[] parameters) {
		if (parameters.length == 1 && parameters[0].equalsIgnoreCase("permissions")) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		player.sendMessage("Your permissions:");
		if (!MyWarp.permissions.useOfficial()) {
			player.sendMessage("(Use build in permissions!)");
		}
		for (PermissionTypes type : PermissionWrapper.PermissionTypes.values()) {
			WMPlayerListener.printPermission(type, player);
		}
		return true;
	}

}
