package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import me.taylorkelly.mywarp.WarpList;

public class InviteCommand extends WarpCommand {

	public InviteCommand(WarpList list, Server server) {
		super(list, server, true, "invite");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		this.list.invite(warpName, creator, sender, this.getPlayer(parameter));
		return true;
	}

}
