package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends SubCommand {

	public CreateCommand(WarpList list, Server server) {
		super(list, server, "create", "+", "createp", "+p", "createg", "+g");
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length == 2 && sender instanceof Player) {
			Visibility visibility = Visibility.PUBLIC;
			if (parameters[0].equalsIgnoreCase("createp") || parameters[0].equalsIgnoreCase("+p")) {
				visibility = Visibility.PRIVATE;
			} else if (parameters[0].equalsIgnoreCase("createg") || parameters[0].equalsIgnoreCase("+g")) {
				visibility = Visibility.GLOBAL;
			}
			
			this.list.addWarp(parameters[1], (Player) sender, visibility);
			return true;
		} else {
			return false;
		}
	}
}
