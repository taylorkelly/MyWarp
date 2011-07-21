package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class GlobalizeCommand extends WarpCommand {

	public GlobalizeCommand(WarpManager list, Server server) {
		super(list, server, "", "global");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
		this.list.globalize(warpName, creator, sender);
		return true;
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Sets the status of a warp to global." , "This is only possible if there is no global warp with this name." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Globalizes the warp.";
	}

}