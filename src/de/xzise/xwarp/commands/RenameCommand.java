package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class RenameCommand extends WarpCommand {

	public RenameCommand(WarpList list, Server server) {
		super(list, server, true, "rename");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		this.list.rename(warpName, creator, sender, parameter);
		return true;
	}
}
