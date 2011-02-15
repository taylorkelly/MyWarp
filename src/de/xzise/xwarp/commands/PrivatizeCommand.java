package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class PrivatizeCommand extends WarpCommand {

	public PrivatizeCommand(WarpList list, Server server) {
		super(list, server, false, "private");
	}

	@Override
	protected void executeEdit(Player player, String warpName, String creator, String parameter) {
		this.list.privatize(warpName, creator, player);
	}

}
