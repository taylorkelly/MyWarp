package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.Lister;
import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class InfoCommand extends WarpCommand {

	public InfoCommand(WarpList list, Server server) {
		super(list, server, false, "info");
	}

	@Override
	protected void executeEdit(Player player, String warpName, String creator, String parameter) {
		Warp warp = this.list.getWarp(warpName, creator);
		player.sendMessage("Warp info: " + ChatColor.GREEN + warp.name);
		
		// Group?
		String group = MyWarp.permissions.getGroup(warp.creator);
		String groupText = "";
		if (group != null) {
			groupText = " (Group: " + ChatColor.GREEN + group;
		}
		
		player.sendMessage("Creator: " + ChatColor.GREEN + warp.creator + groupText);
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
		player.sendMessage("Visibility: " + Lister.getColor(warp, player) + visibility);
		Location location = warp.getLocation();
		player.sendMessage("Position: x = " + ChatColor.GREEN + location.getBlockX() + ChatColor.WHITE + ", y = " + ChatColor.GREEN + location.getBlockY() + ChatColor.WHITE + ", z = " + ChatColor.GREEN + location.getBlockZ());
	}

}
