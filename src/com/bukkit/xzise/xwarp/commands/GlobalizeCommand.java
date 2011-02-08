package com.bukkit.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class GlobalizeCommand extends EditCommand {

	public GlobalizeCommand(WarpList list, Server server) {
		super(list, server, false, "global");
	}

	@Override
	protected void executeEdit(Player player, String warpName, String creator, String parameter) {
		this.list.globalize(warpName, creator, player);
	}

}
