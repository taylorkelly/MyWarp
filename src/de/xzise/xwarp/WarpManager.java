package de.xzise.xwarp;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.WarpDataSource;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

/**
 * Wraps around {@link WarpList} to provide permissions support.
 * @author Fabian Neundorf
 */
public class WarpManager {

	private WarpList list;
	private Server server;
	
	public WarpManager(Server server) {
		this.list = new WarpList();
		this.server = server;
		this.loadFromDatabase();
	}
	
	private void loadFromDatabase() {
		this.list.loadList(WarpDataSource.getWarps(this.server));
	}
	
	public void loadFromDatabase(CommandSender sender) {
		if (MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_RELOAD)) {
			this.loadFromDatabase();
		} else {
			sender.sendMessage(ChatColor.RED + "You have no permission to reload.");
		}
	}	
}
