package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class GlobalizeCommand extends WarpCommand {

	public GlobalizeCommand(WarpList list, Server server) {
		super(list, server, "", "global");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
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
