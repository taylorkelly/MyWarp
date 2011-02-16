package de.xzise.xwarp.commands;

import java.util.Iterator;
import java.util.List;

import me.taylorkelly.mywarp.Lister;
import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand extends WarpCommand {

	public InfoCommand(WarpList list, Server server) {
		super(list, server, false, "info");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		Warp warp = this.list.getWarp(warpName, creator);
		sender.sendMessage("Warp info: " + ChatColor.GREEN + warp.name);
		
		// Group?
		String group = MyWarp.permissions.getGroup(warp.creator);
		String groupText = "";
		if (group != null) {
			groupText = " (Group: " + ChatColor.GREEN + group + ChatColor.WHITE + ")";
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
			visibility = Lister.getColor(warp, (Player) sender) + visibility;
		}
		sender.sendMessage("Visibility: " + visibility);
		List<String> permissions = warp.permissions;
		Iterator<String> i = permissions.iterator();
		String invitees = "";
		while (i.hasNext()) {
			String name = i.next();
			invitees = invitees + name;
			if (i.hasNext()) {
				invitees += ", ";
			}
		}
		sender.sendMessage("Invitees: " + invitees);
		
		Location location = warp.getLocation();
		sender.sendMessage("Position: x = " + ChatColor.GREEN + location.getBlockX() + ChatColor.WHITE + ", y = " + ChatColor.GREEN + location.getBlockY() + ChatColor.WHITE + ", z = " + ChatColor.GREEN + location.getBlockZ());
		
		return true;
	}

}
