package de.xzise.xwarp.commands;

import java.util.Iterator;
import java.util.List;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.lister.GenericLister;

public class InfoCommand extends WarpCommand {

	public InfoCommand(WarpList list, Server server) {
		super(list, server, "", "info");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		Warp warp = this.list.getWarp(warpName, creator);
		sender.sendMessage("Warp info: " + ChatColor.GREEN + warp.name);
		
		// Group?
		String group = MyWarp.permissions.getGroup(warp.creator);
		String groupText = "";
		if (group != null) {
			groupText = ChatColor.WHITE + " (Group: " + ChatColor.GREEN + group + ChatColor.WHITE + ")";
		}
		
		sender.sendMessage("Creator: " + ChatColor.GREEN + warp.creator + groupText);
		String visibility = "";
		switch (warp.visibility) {
		case GLOBAL :
			visibility = "Global";
			break;
		case PUBLIC :
			visibility = "Public";
			break;
		case PRIVATE :
			visibility = "Private";
			break;
		}		
		if (sender instanceof Player) {
			visibility = GenericLister.getColor(warp, (Player) sender) + visibility;
		}
		sender.sendMessage("Visibility: " + visibility);
		List<String> permissions = warp.permissions;
		String invitees = "";
		if (permissions.size() == 0) {
			invitees = "None";
		} else {
			Iterator<String> i = permissions.iterator();
			while (i.hasNext()) {
				String name = i.next();
				invitees = invitees + ChatColor.GREEN + name;
				if (i.hasNext()) {
					invitees += ChatColor.WHITE + ", ";
				}
			}
		}
		sender.sendMessage("Invitees: " + invitees);

		List<String> editors = warp.editors;
		String editor = "";
		if (editors.size() == 0) {
			editor = "None";
		} else {
			Iterator<String> i = editors.iterator();
			while (i.hasNext()) {
				String name = i.next();
				editor = editor + ChatColor.GREEN + name;
				if (i.hasNext()) {
					editor += ChatColor.WHITE + ", ";
				}
			}
		}
		sender.sendMessage("Editors: " + editor);
		
		Location location = warp.getLocation();
		sender.sendMessage("Location: World = " + ChatColor.GREEN + location.getWorld().getName() + ChatColor.WHITE + ", x = " + ChatColor.GREEN + location.getBlockX() + ChatColor.WHITE + ", y = " + ChatColor.GREEN + location.getBlockY() + ChatColor.WHITE + ", z = " + ChatColor.GREEN + location.getBlockZ());
		
		return true;
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Show the information about the warp." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Show warp's information";
	}

}
