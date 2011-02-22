package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpdateCommand extends WarpCommand {

	public UpdateCommand(WarpList list, Server server) {
		super(list, server, "", "update", "*");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
		if (sender instanceof Player) {
			this.list.update(warpName, creator, (Player) sender);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Updates the position of the warp." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Updates the warp's position.";
	}
}
