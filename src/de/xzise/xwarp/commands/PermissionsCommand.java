package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.PermissionWrapper;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class PermissionsCommand extends SubCommand {

	public PermissionsCommand(WarpList list, Server server) {
		super(list, server, "permissions");
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length == 1 && sender instanceof Player) {
			sender.sendMessage("Your permissions:");
			if (!MyWarp.permissions.useOfficial()) {
				sender.sendMessage("(Use build in permissions!)");
			}
			for (PermissionTypes type : PermissionWrapper.PermissionTypes.values()) {
				PermissionsCommand.printPermission(type, (Player) sender);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public static void printPermission(PermissionTypes permission, Player player) {
		boolean hasPermission = MyWarp.permissions.permission(player, permission);
		String message = (hasPermission ? ChatColor.GREEN : ChatColor.RED) + permission.name + ": " + (hasPermission ? "Yes": "No");
		player.sendMessage(message);
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Shows all your permissions." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Shows the permissions of you";
	}

	@Override
	protected String getCommand() {
		return "warp permissions";
	}

	@Override
	protected boolean listHelp(CommandSender sender) {
		//TODO: false or true? It is only a debug function so ... false?
		return false;
	}

}
