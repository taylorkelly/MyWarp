package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.WarpList;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class CreateCommand extends SubCommand {

	private final Visibility visibility;
	
	protected CreateCommand(WarpList list, Server server, String suffix, Visibility visibility) {
		super(list, server, CreateCommand.getCreateCommands(suffix));
		this.visibility = visibility;
	}
	
	public static CreateCommand newCreatePrivate(WarpList list, Server server) {
		return new CreateCommand(list, server, "p", Visibility.PRIVATE);
	}
	
	public static CreateCommand newCreatePublic(WarpList list, Server server) {
		return new CreateCommand(list, server, "", Visibility.PUBLIC);
	}
	
	public static CreateCommand newCreateGlobal(WarpList list, Server server) {
		return new CreateCommand(list, server, "g", Visibility.GLOBAL);
	}
	
	private static String[] getCreateCommands(String suffix) {
		return new String[] { "create" + suffix, "+" + suffix, "add" + suffix };
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (sender instanceof Player) {
			if (parameters.length == 2) {			
				this.list.addWarp(parameters[1], (Player) sender, ((Player) sender).getName(), this.visibility);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	protected String[] getFullHelpText() {
		String visibilityText = "";
		switch (this.visibility) {
		case PRIVATE :
			visibilityText = "private";
			break;
		case PUBLIC : 
			visibilityText = "public";
			break;
		case GLOBAL :
			visibilityText = "global";
			break;
		}
		return new String[] { "Creates a new warp, the visibility is by default " + visibilityText };
	}

	@Override
	protected String getSmallHelpText() {
		switch (this.visibility) {
		case PRIVATE : return "Creates private warp";
		case PUBLIC : return "Creates public warp";
		case GLOBAL : return "Creates global warp";
		default : return "Missing help text";
		}
	}

	@Override
	protected String getCommand() {
		return "warp " + this.commands[0] + " <name>";
	}

	@Override
	protected boolean listHelp(CommandSender sender) {
		switch (this.visibility) {
		case PRIVATE : return MyWarp.permissions.permission(sender, PermissionTypes.CREATE_PRIVATE);
		case PUBLIC : return MyWarp.permissions.permission(sender, PermissionTypes.CREATE_PUBLIC);
		case GLOBAL : return MyWarp.permissions.permission(sender, PermissionTypes.CREATE_GLOBAL);
		default : return false;
		}
	}
}
