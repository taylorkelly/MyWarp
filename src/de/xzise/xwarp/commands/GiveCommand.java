package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class GiveCommand extends WarpCommand {

	public GiveCommand(WarpManager list, Server server) {
		super(list, server, "player", "give", "chown");
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
