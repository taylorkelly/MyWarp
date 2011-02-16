package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class PrivatizeCommand extends WarpCommand {

	public PrivatizeCommand(WarpList list, Server server) {
		super(list, server, false, "private");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		this.list.privatize(warpName, creator, sender);
		return true;
	}

}
