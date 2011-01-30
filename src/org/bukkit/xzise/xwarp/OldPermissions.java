package org.bukkit.xzise.xwarp;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.xzise.permissionwrapper.creators.PluginCreator;
import org.bukkit.xzise.permissionwrapper.wrapper.PermissionsInterface;
import org.bukkit.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class OldPermissions implements PluginCreator, PermissionsInterface {
	
	@Override
	public PermissionsInterface getInterface(Server server) {
		return this;
	}

	@Override
	public boolean has(Player player, String name) {
		if (name.equals(PermissionTypes.TO_GLOBAL.name)
				|| name.equals(PermissionTypes.TO_OWN.name)
				|| name.equals(PermissionTypes.TO_OTHER.name)
				|| name.equals(PermissionTypes.TO_INVITED.name)
				|| name.equals(PermissionTypes.CREATE_PRIVATE.name)
				|| name.equals(PermissionTypes.CREATE_PUBLIC.name)) {
			return true; // Everybody can create private/public warps
		} else if (PermissionWrapper.isAdminPermission(name)) {
			return player.isOp();
		}
		return false;
	}

	@Override
	public int getInteger(Player player, String name) {
		return -1;
	}

	@Override
	public String getName() {
		return "Build in permissions";
	}
	
	

}
