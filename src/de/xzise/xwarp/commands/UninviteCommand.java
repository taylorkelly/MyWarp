package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class UninviteCommand extends WarpCommand {

	public UninviteCommand(WarpList list, Server server) {
		super(list, server, true, "uninvite");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		this.list.uninvite(warpName, creator, sender, this.getPlayer(parameter));
		return true;
	}

}
