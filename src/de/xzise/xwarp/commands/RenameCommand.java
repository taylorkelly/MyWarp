package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class RenameCommand extends EditCommand {

	public RenameCommand(WarpList list, Server server) {
		super(list, server, true, "update", "*");
	}

	@Override
	protected void executeEdit(Player player, String warpName, String creator, String parameter) {
		this.list.rename(warpName, creator, player, parameter);
	}
}
