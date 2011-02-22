package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class DeleteCommand extends WarpCommand {

	public DeleteCommand(WarpList list, Server server) {
		super(list, server, "", "delete", "-");
	}	

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
		this.list.deleteWarp(warpName, creator, sender);
		return true;
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Deletes the given warp." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Deletes the warp.";
	}

}
