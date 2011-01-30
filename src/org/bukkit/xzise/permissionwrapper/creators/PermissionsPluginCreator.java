package org.bukkit.xzise.permissionwrapper.creators;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.xzise.permissionwrapper.wrapper.PermissionsInterface;
import org.bukkit.xzise.permissionwrapper.wrapper.PermissionsPluginWrapper;

import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionsPluginCreator implements PluginCreator {
	
	public PermissionsInterface getInterface(Server server) {
		Plugin plugin = server.getPluginManager().getPlugin("Permissions");
		if (plugin != null) {
			return new PermissionsPluginWrapper(((Permissions) plugin).getHandler());
		} else {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Permissions (by Nijikokun)";
	}
}
