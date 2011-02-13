package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class UpdateCommand extends EditCommand {

	public UpdateCommand(WarpList list, Server server) {
		super(list, server, false, "update", "*");
	}

	@Override
	protected void executeEdit(Player player, String warpName, String creator, String parameter) {
		this.list.update(warpName, creator, player);
	}
}
