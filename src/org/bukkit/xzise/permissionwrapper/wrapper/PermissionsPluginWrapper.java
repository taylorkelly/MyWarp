package org.bukkit.xzise.permissionwrapper.wrapper;

import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;

public class PermissionsPluginWrapper implements PermissionsInterface {

	private PermissionHandler handler;
	
	public PermissionsPluginWrapper(PermissionHandler handler) {
		this.handler = handler;
	}
	
	public boolean has(Player player, String name) {
		return this.handler.has(player, name);
	}

	@Override
	public int getInteger(Player player, String name) {
		return this.handler.getPermissionInteger(player.getName(), name);
	}
	
}
