package org.bukkit.xzise.permissionwrapper;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.xzise.permissionwrapper.creators.PluginCreator;
import org.bukkit.xzise.permissionwrapper.wrapper.PermissionsInterface;

public class WrapperCreator {

	private PermissionsInterface permissions;
	private final Logger log = Logger.getLogger("Minecraft");
	
	/**
	 * Initializes the wrapper. If no suitable wrapper is found it returns null.
	 * @param server Reference to the server for initialization.
	 * @param wrappers Preferred wrapper.
	 * @return null if no wrapper was found. Otherwise the first available wrapper.
	 */
	public final PermissionsInterface init(Plugin plugin, PluginCreator... wrappers) {
		this.permissions = null;
		this.log.info("[" + plugin.getDescription().getName() + "] Searching permissions");
		for (int i = 0; i < wrappers.length || this.permissions == null; i++) {
			this.permissions = wrappers[i].getInterface(plugin.getServer());
			if (this.permissions != null) {
				this.log.info("[" + plugin.getDescription().getName() + "] Permission loaded: " + wrappers[i].getName());
			}
		}
		return this.permissions;
	}
	
	public final PermissionsInterface getPermissions() {
		return this.permissions;
	}
}
