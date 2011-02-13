package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class PublicizeCommand extends EditCommand {

	public PublicizeCommand(WarpList list, Server server) {
		super(list, server, false, "public");
	}

	@Override
	protected void executeEdit(Player player, String warpName, String creator, String parameter) {
		this.list.publicize(warpName, creator, player);
	}

}
