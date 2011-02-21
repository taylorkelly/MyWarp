package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class UninviteCommand extends WarpCommand {

	public UninviteCommand(WarpManager list, Server server) {
		super(list, server, "player", "uninvite");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		this.list.uninvite(warpName, creator, sender, this.getPlayer(parameter));
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
