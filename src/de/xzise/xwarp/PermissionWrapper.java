package de.xzise.xwarp;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
	}
	
	private static PermissionTypes[] ADMIN_PERMISSIONS = new PermissionTypes[] {
		PermissionTypes.ADMIN_DELETE,
		PermissionTypes.ADMIN_INVITE,
		PermissionTypes.ADMIN_UNINVITE,
		PermissionTypes.ADMIN_GIVE, 
		PermissionTypes.ADMIN_MESSAGE,
		PermissionTypes.ADMIN_UPDATE, 
		PermissionTypes.ADMIN_TO_ALL,
		PermissionTypes.ADMIN_GLOBAL,
		PermissionTypes.ADMIN_PUBLIC,
		PermissionTypes.ADMIN_PRIVATE,
		PermissionTypes.ADMIN_RELOAD,
		PermissionTypes.ADMIN_RENAME,
	};
	
	private static PermissionTypes[] DEFAULT_PERMISSIONS = new PermissionTypes[] {
		PermissionTypes.TO_GLOBAL,
		PermissionTypes.TO_OWN,
		PermissionTypes.TO_OTHER,
		PermissionTypes.TO_INVITED,
		PermissionTypes.CREATE_PRIVATE,
		PermissionTypes.CREATE_PUBLIC,
		PermissionTypes.CREATE_GLOBAL,
		PermissionTypes.SIGN_WARP,
	};
	
	private PermissionHandler handler = null;
	
	public String getGroup(String player) {
		if (this.handler == null) {
			return null;
		} else {
			return this.handler.getGroup(player);
		}
	}
	
	private boolean permissionX(CommandSender sender, PermissionTypes permission) {
		if (contains(permission, DEFAULT_PERMISSIONS)) {
			return true;
		} else if (contains(permission, ADMIN_PERMISSIONS)) {
			return sender.isOp();
		} else {
			return false;
		}
	}
	
	public boolean permission(CommandSender sender, PermissionTypes permission) {
		if (sender instanceof Player) {
			if (this.handler != null) {
				return this.handler.has((Player) sender, permission.name);
			} else {
				return this.permissionX(sender, permission);
			}
		} else if (sender instanceof ConsoleCommandSender) {
			return true;
		} else {
			return this.permissionX(sender, permission);
		}
	}
	
	public int getInteger(Player player, PermissionTypes permission) {
		return this.handler.getPermissionInteger(player.getName(), permission.name);
	}

	public boolean hasAdminPermission(CommandSender sender) {
		return this.permissionOr(sender, ADMIN_PERMISSIONS);
	}

	public boolean permissionOr(CommandSender sender, PermissionTypes... permission) {
		for (PermissionTypes permissionType : permission) {
			if (this.permission(sender, permissionType)) {
				return true;
			}
		}
		return false;
	}

	public boolean permissionAnd(CommandSender sender, PermissionTypes... permission) {
		for (PermissionTypes permissionType : permission) {
			if (!this.permission(sender, permissionType)) {
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

	public static <T> boolean contains(T o, T[] a) {
		for (T t : a) {
			if (t != null && t.equals(o)) {
				return true;
			}
		}
		return false;
	}
	
}
