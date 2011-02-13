package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class UninviteCommand extends EditCommand {

	public UninviteCommand(WarpList list, Server server) {
		super(list, server, true, "uninvite");
	}

	@Override
	protected void executeEdit(Player player, String warpName, String creator, String parameter) {
		this.list.uninvite(warpName, creator, player, this.getPlayer(parameter));
	}

}
