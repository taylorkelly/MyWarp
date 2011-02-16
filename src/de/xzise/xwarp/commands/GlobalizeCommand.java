package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class GlobalizeCommand extends WarpCommand {

	public GlobalizeCommand(WarpList list, Server server) {
		super(list, server, false, "global");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		this.list.globalize(warpName, creator, sender);
		return true;
	}

}
