package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class GiveCommand extends WarpCommand {

	public GiveCommand(WarpList list, Server server) {
		super(list, server, "player", "give");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
		this.list.give(warpName, creator, sender, this.getPlayer(parameters[0]));
		return true;
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Changes the owner of the warp." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Gives the warp away.";
	}
}
