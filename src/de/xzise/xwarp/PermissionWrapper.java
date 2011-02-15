package de.xzise.xwarp;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionWrapper {

	public enum PermissionTypes {
		// Warp to global warps
		TO_GLOBAL("warp.to.global"),
		// Warp to own warps
		TO_OWN("warp.to.own"),
		// Warp to invited warps
		TO_INVITED("warp.to.invited"),
		// Warp to public warps
		TO_OTHER("warp.to.other"),
		
		// Warp with sign
		SIGN_WARP("warp.sign"),

		// Create/Edit private warps
		CREATE_PRIVATE("warp.create.private"),
		// Create/Edit public warps
		CREATE_PUBLIC("warp.create.public"),
		// Create/Edit global warps
		CREATE_GLOBAL("warp.create.global"),

		// Delete all warps
		ADMIN_DELETE("warp.admin.delete"),
		// Invite to all warps
		ADMIN_INVITE("warp.admin.invite"),
		// Uninvite to all warps
		ADMIN_UNINVITE("warp.admin.uninvite"),
		// Give away all warps
		ADMIN_GIVE("warp.admin.give"),
		// Edit the welcome message of all warps
		ADMIN_MESSAGE("warp.admin.message"),
		// Update all warps
		ADMIN_UPDATE("warp.admin.update"),
		// Rename all warps
		ADMIN_RENAME("warp.admin.rename"),
		// Make other's warp privates
		ADMIN_PRIVATE("warp.admin.private"),
		// Make other's warp public
		ADMIN_PUBLIC("warp.admin.public"),
		// Make other's warps global
		ADMIN_GLOBAL("warp.admin.global"),
		// Warp to all warps
		ADMIN_TO_ALL("warp.admin.to.all"),
		// Reload database
		ADMIN_RELOAD("warp.admin.reload");

		// Maybe upcoming permissions:
		// Different admin permissions for each warp (only edit public warps
		// e.g.)

		public final String name;

		PermissionTypes(String name) {
			this.name = name;
		}
		
		public static PermissionTypes getType(String name) {
			for (PermissionTypes type : PermissionTypes.values()) {
				if (type.name.equals(name)) {
					return type;
				}
			}
			return null;
		}
		
		public boolean isAdminPermission() {
			return this.name.matches("warp\\.admin\\..*");
		}
	}
	
	private PermissionHandler handler = null;

	private boolean permission(Player player, String permission) {
		if (this.handler == null) {
			if (permission.equals(PermissionTypes.TO_GLOBAL.name)
					|| permission.equals(PermissionTypes.TO_OWN.name)
					|| permission.equals(PermissionTypes.TO_OTHER.name)
					|| permission.equals(PermissionTypes.TO_INVITED.name)
					|| permission.equals(PermissionTypes.CREATE_PRIVATE.name)
					|| permission.equals(PermissionTypes.CREATE_PUBLIC.name)
					|| permission.equals(PermissionTypes.SIGN_WARP.name)) {
				return true; // Everybody can create private/public warps
			} else if (isAdminPermission(permission)) {
				return player.isOp();
			}
			return false;
		} else {
			return this.handler.has(player, permission);
		}
	}
	
	public String getGroup(String player) {
		if (this.handler == null) {
			return null;
		} else {
			return this.handler.getGroup(player);
		}
	}

	public boolean permission(Player player, PermissionTypes permission) {
		return this.permission(player, permission.name);
	}
	
	public int getInteger(Player player, PermissionTypes permission) {
		return this.handler.getPermissionInteger(player.getName(), permission.name);
	}

	public boolean hasAdminPermission(Player player) {
		return this.permissionOr(player, PermissionTypes.ADMIN_DELETE,
				PermissionTypes.ADMIN_INVITE, PermissionTypes.ADMIN_UNINVITE,
				PermissionTypes.ADMIN_GIVE, PermissionTypes.ADMIN_MESSAGE,
				PermissionTypes.ADMIN_UPDATE, PermissionTypes.ADMIN_TO_ALL);
	}
	
	public boolean isAdminPermission(String permission) {
		PermissionTypes type = PermissionTypes.getType(permission);
		return type == null ? false : type.isAdminPermission();
	}

	public boolean permissionOr(Player player, PermissionTypes... permission) {
		for (PermissionTypes permissionType : permission) {
			if (this.permission(player, permissionType)) {
				return true;
			}
		}
		return false;
	}

	public boolean permissionAnd(Player player, PermissionTypes... permission) {
		for (PermissionTypes permissionType : permission) {
			if (!this.permission(player, permissionType)) {
				return false;
			}
		}
		return true;
	}

	public void init(Server server) {
		Plugin test = server.getPluginManager().getPlugin("Permissions");
		if (test != null) {
			this.handler = ((Permissions) test).getHandler();
			MyWarp.logger.info("Permissions enabled.");
		} else {
			MyWarp.logger.severe("Permission system not found. Use defaults.");
		}
	}
	
	public boolean useOfficial() {
		return this.handler != null;
	}

}
