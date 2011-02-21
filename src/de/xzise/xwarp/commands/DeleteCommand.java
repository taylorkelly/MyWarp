package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class DeleteCommand extends WarpCommand {

	public DeleteCommand(WarpManager list, Server server) {
		super(list, server, "", "delete", "-");
	}	

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
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
