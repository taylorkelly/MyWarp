package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {

	public ReloadCommand(WarpList list, Server server) {
		super(list, server, "reload");
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length == 1) {
			this.list.loadFromDatabase(sender);
			return true;
		} else {
			return false;
		}
	}

}
