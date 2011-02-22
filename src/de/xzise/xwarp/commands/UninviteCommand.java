package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class UninviteCommand extends WarpCommand {

	public UninviteCommand(WarpList list, Server server) {
		super(list, server, "player", "uninvite");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
		this.list.uninvite(warpName, creator, sender, this.getPlayer(parameters[0]));
		return true;
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Revokes the invitation of the invited user." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Uninvites the user.";
	}
}
