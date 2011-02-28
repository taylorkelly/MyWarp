package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class WarpToCommand extends SubCommand {

	public WarpToCommand(WarpManager list, Server server) {
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
			this.list.warpTo(parameters[start], creator, (Player) sender, false);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Warps the player to the given warp.", "This command is only ingame available." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Warps the player";
	}

	@Override
	protected String getCommand() {
		return "warp [to] <name> [creator]";
	}

	@Override
	protected boolean listHelp(CommandSender sender) {
		return MyWarp.permissions.permissionOr(sender, PermissionTypes.TO_GLOBAL, PermissionTypes.TO_INVITED, PermissionTypes.TO_OTHER, PermissionTypes.TO_OWN, PermissionTypes.ADMIN_TO_ALL);
	}
}
