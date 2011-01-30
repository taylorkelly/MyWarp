package org.bukkit.xzise.permissionwrapper.wrapper;

import org.bukkit.entity.Player;

public interface PermissionsInterface {

	boolean has(Player player, String name);
	
	int getInteger(Player player, String name);
}
