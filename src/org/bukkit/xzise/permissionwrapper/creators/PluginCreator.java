package org.bukkit.xzise.permissionwrapper.creators;

import org.bukkit.Server;
import org.bukkit.xzise.permissionwrapper.wrapper.PermissionsInterface;

public interface PluginCreator {
	
	PermissionsInterface getInterface(Server server);
	
	String getName();

}
