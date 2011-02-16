package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpToCommand extends SubCommand {

	public WarpToCommand(WarpList list, Server server) {
		super(list, server, "to");
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (sender instanceof Player && (parameters.length == 1 || parameters.length == 2 || (parameters.length == 3 && parameters[0].equalsIgnoreCase("to")))) {
			// TODO ChunkLoading
			int start = 0;
			if (parameters[0].equalsIgnoreCase("to") && (parameters.length == 2 || parameters.length == 3)) {
				start++;
			}
			String creator = "";
			if (parameters.length > start + 1) {
				creator = this.getPlayer(parameters[start + 1]);
			}
			this.list.warpTo(parameters[start], creator, (Player) sender);
			return true;
		} else {
			return false;
		}
	}
}
