package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class DeleteCommand extends WarpCommand {

	public DeleteCommand(WarpList list, Server server) {
		super(list, server, false, "delete", "-");
	}	

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		this.list.deleteWarp(warpName, creator, sender);
		return true;
	}

}
