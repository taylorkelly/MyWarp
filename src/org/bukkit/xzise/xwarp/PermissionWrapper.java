package org.bukkit.xzise.xwarp;

import org.bukkit.entity.Player;
import org.bukkit.xzise.permissionwrapper.WrapperCreator;

public class PermissionWrapper extends WrapperCreator {

	public enum PermissionTypes {
		// Warp to global warps
		TO_GLOBAL("warp.to.global"), // Not implemented yet
		// Warp to own warps
		TO_OWN("warp.to.own"),
		// Warp to invited warps
		TO_INVITED("warp.to.invited"),
		// Warp to public warps
		TO_OTHER("warp.to.other"),

		// Create/Edit private warps
		CREATE_PRIVATE("warp.create.private"),
		// Create/Edit public warps
		CREATE_PUBLIC("warp.create.public"),
		// Create/Edit global warps
		CREATE_GLOBAL("warp.create.global"), // Not implemented yet

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

		// Warp to all warps
		ADMIN_TO_ALL("warp.admin.to.all");

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
			return (this == ADMIN_DELETE) || (this == ADMIN_GIVE) || (this == ADMIN_INVITE) || (this == ADMIN_MESSAGE) || (this == ADMIN_TO_ALL) || (this == ADMIN_UNINVITE) || (this == ADMIN_UPDATE);
		}
	}

	public boolean has(Player player, PermissionTypes permission) {
		return this.getPermissions().has(player, permission.name);
	}
	
	public int getInteger(Player player, PermissionTypes permission) {
		return this.getPermissions().getInteger(player, permission.name);
	}

	public boolean hasAdminPermission(Player player) {
		return this.permissionOr(player, PermissionTypes.ADMIN_DELETE,
				PermissionTypes.ADMIN_INVITE, PermissionTypes.ADMIN_UNINVITE,
				PermissionTypes.ADMIN_GIVE, PermissionTypes.ADMIN_MESSAGE,
				PermissionTypes.ADMIN_UPDATE, PermissionTypes.ADMIN_TO_ALL);
	}
	
	public static boolean isAdminPermission(String permission) {
		PermissionTypes type = PermissionTypes.getType(permission);
		return type == null ? false : type.isAdminPermission();
	}

	public boolean permissionOr(Player player, PermissionTypes... permission) {
		for (PermissionTypes permissionType : permission) {
			if (this.has(player, permissionType)) {
				return true;
			}
		}
		return false;
	}

	public boolean permissionAnd(Player player, PermissionTypes... permission) {
		for (PermissionTypes permissionType : permission) {
			if (!this.has(player, permissionType)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean useOfficial() {
		return !(this.getPermissions() instanceof OldPermissions);
	}

}
