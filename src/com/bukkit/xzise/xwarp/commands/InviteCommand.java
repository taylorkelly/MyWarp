package com.bukkit.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import me.taylorkelly.mywarp.WarpList;

public class InviteCommand extends EditCommand {

	public InviteCommand(WarpList list, Server server) {
		super(list, server, true, "invite");
	}

	@Override
	protected void executeEdit(Player player, String warpName, String creator, String parameter) {
		this.list.invite(warpName, creator, player, this.getPlayer(parameter));
	}

}
