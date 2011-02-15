package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class GiveCommand extends WarpCommand {

	public GiveCommand(WarpList list, Server server) {
		super(list, server, true, "give");
	}

	@Override
	protected void executeEdit(Player player, String warpName, String creator, String parameter) {
		this.list.give(warpName, creator, player, this.getPlayer(parameter));
	}

}
