package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.PermissionWrapper;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;
import de.xzise.xwarp.WarpManager;

public class PermissionsCommand extends SubCommand {

	public PermissionsCommand(WarpManager list, Server server) {
		super(list, server, "permissions");
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		Player player = null;
		boolean showGranted = true;
		boolean showDenied = true;
		switch (parameters.length) {
		case 2:
			if (!(parameters[1].equalsIgnoreCase("n") || parameters[1].equalsIgnoreCase("y"))) {
				player = this.server.getPlayer(parameters[1]);
				if (player == null) {
					sender.sendMessage("Player is not logged in.");
					return true;
				}
				break;
			} else {
				showGranted = parameters[1].equalsIgnoreCase("y");
				showDenied = parameters[1].equalsIgnoreCase("n");
			}
		case 1:
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				sender.sendMessage("You are not a player");
				return true;
			}
			break;
		case 3:
			if (parameters[2].equalsIgnoreCase("n") || parameters[2].equalsIgnoreCase("y")) {
				player = this.server.getPlayer(parameters[1]);
				if (player == null) {
					sender.sendMessage("Player is not logged in.");
					return true;
				}				

				showGranted = parameters[2].equalsIgnoreCase("y");
				showDenied = parameters[2].equalsIgnoreCase("n");
				break;
			} else {
				return false;
			}
		}
		
		if (player != null)	{
			sender.sendMessage("Your permissions:");
			if (!MyWarp.permissions.useOfficial()) {
				sender.sendMessage("(Use build in permissions!)");
			}
			for (PermissionTypes type : PermissionWrapper.PermissionTypes.values()) {
				boolean hasPermission = MyWarp.permissions.permission(player, type);
				if ((hasPermission && showGranted) || (!hasPermission && showDenied)) {
					String message = (hasPermission ? ChatColor.GREEN : ChatColor.RED) + type.name + ": " + (hasPermission ? "Yes": "No");
					player.sendMessage(message);
				}
			}
			return true;
		} else {
			return false;
		}
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
