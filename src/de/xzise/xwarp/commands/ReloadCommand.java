package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ReloadCommand extends FixedParametersCommand {

	public ReloadCommand(WarpList list, Server server) {
		super(list, server, "reload");
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		this.list.loadFromDatabase(player);
		return true;
	}

}
