package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class ReloadCommand extends DefaultSubCommand {

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

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Reloads all warps from the database." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Reloads the warps.";
	}

	@Override
	protected String getCommand() {
		return "warp reload";
	}

	@Override
	protected boolean listHelp(CommandSender sender) {
		return MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_RELOAD);
	}

}
