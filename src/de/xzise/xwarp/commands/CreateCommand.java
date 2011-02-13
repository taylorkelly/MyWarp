package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class CreateCommand extends FixedParametersCommand {

	public CreateCommand(WarpList list, Server server) {
		super(list, server, 1, "create", "+", "createp", "+p", "createg", "+g");
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		Visibility visibility = Visibility.PUBLIC;
		if (parameters[0].equalsIgnoreCase("createp") || parameters[0].equalsIgnoreCase("+p")) {
			visibility = Visibility.PRIVATE;
		} else if (parameters[0].equalsIgnoreCase("createg") || parameters[0].equalsIgnoreCase("+g")) {
			visibility = Visibility.GLOBAL;
		}
		
		this.list.addWarp(parameters[1], player, visibility);
		return true;
	}
}
