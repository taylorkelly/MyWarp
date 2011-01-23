package org.bukkit.xzise.xwarp;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionWrapper {

	private PermissionHandler handler = null;
	
	public boolean permission(Player player, String permission) {
		if (this.handler == null) {
			if (permission.matches("warp\\.create\\.(private|public)")
					|| permission.equals("warp.to")) {
				return true; // Everybody can create private/public warps
			} else if (permission.equals("warp.delete") || permission.equals("warp.invite")
					|| permission.equals("warp.uninvite") || permission.equals("give")
					|| permission.equals("message")) {
				return player.isOp();
			}
			return false;
		} else {
			return this.handler.permission(player, permission);
		}
	}
	
	public void init(Server server) {
		Plugin test = server.getPluginManager().getPlugin("Permissions");
        if (test != null) {
            Logger log = Logger.getLogger("Minecraft");
            this.handler = Permissions.Security;
            log.log(Level.INFO, "[MYWARP] Permissions enabled.");
        } else {
            Logger log = Logger.getLogger("Minecraft");
            log.log(Level.SEVERE, "[MYWARP] Permission system not found.");
        }
	}
	
}
