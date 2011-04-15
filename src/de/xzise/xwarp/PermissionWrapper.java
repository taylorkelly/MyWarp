package de.xzise.xwarp;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import de.xzise.MinecraftUtil;

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

        // Warp with sign to global
        SIGN_WARP_GLOBAL("warp.sign.global"),
        // Warp to own warps
        SIGN_WARP_OWN("warp.sign.own"),
        // Warp to invited warps
        SIGN_WARP_INVITED("warp.sign.invited"),
        // Warp to public warps
        SIGN_WARP_OTHER("warp.sign.other"),

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
        ADMIN_RELOAD("warp.admin.reload"),
        // Converts from hmod file
        ADMIN_CONVERT("warp.admin.convert"),
        // Export warps
        ADMIN_EXPORT("warp.admin.export"),
        // Converts from hmod file
        ADMIN_EDITORS_REMOVE("warp.admin.editors.remove"),
        // Converts from hmod file
        ADMIN_EDITORS_ADD("warp.admin.editors.add"),
        // Give away all warps
        ADMIN_CHANGE_OWNER("warp.admin.give.owner"),
        // Change the creator
        ADMIN_CHANGE_CREATOR("warp.admin.changecreator"),
        // Warp other players
        ADMIN_WARP_OTHERS("warp.admin.warp.others"),
        // Change the price
        ADMIN_PRICE("warp.admin.price")
        ;

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

    public enum PermissionValues {
        /*
         * VALUES
         */
        // Cooldown
        WARP_COOLDOWN_PRIVATE("warp.timers.cooldown.private"),
        WARP_COOLDOWN_PUBLIC("warp.timers.cooldown.public"),
        WARP_COOLDOWN_GLOBAL("warp.timers.cooldown.global"),

        // Warmup
        WARP_WARMUP_PRIVATE("warp.timers.warmup.private"),
        WARP_WARMUP_PUBLIC("warp.timers.warmup.public"),
        WARP_WARMUP_GLOBAL("warp.timers.warmup.global"), 
        
        // Limits
        WARP_LIMIT_PRIVATE("warp.limit.private"),
        WARP_LIMIT_PUBLIC("warp.limit.public"),
        WARP_LIMIT_GLOBAL("warp.limit.global"),
        WARP_LIMIT_TOTAL("warp.limit.total"),

        // Prices (warp)
        WARP_PRICES_TO_PRIVATE("warp.prices.to.private"),
        WARP_PRICES_TO_PUBLIC("warp.prices.to.public"),
        WARP_PRICES_TO_GLOBAL("warp.prices.to.global"),
        
        // Prices (create)
        WARP_PRICES_CREATE_PRIVATE("warp.prices.create.private"),
        WARP_PRICES_CREATE_PUBLIC("warp.prices.create.public"),
        WARP_PRICES_CREATE_GLOBAL("warp.prices.create.global"),

        ;

        public final String name;

        PermissionValues(String name) {
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

    private static PermissionTypes[] ADMIN_PERMISSIONS = new PermissionTypes[] { PermissionTypes.ADMIN_DELETE, PermissionTypes.ADMIN_INVITE, PermissionTypes.ADMIN_UNINVITE, PermissionTypes.ADMIN_CHANGE_OWNER, PermissionTypes.ADMIN_MESSAGE, PermissionTypes.ADMIN_UPDATE, PermissionTypes.ADMIN_TO_ALL, PermissionTypes.ADMIN_GLOBAL, PermissionTypes.ADMIN_PUBLIC, PermissionTypes.ADMIN_PRIVATE, PermissionTypes.ADMIN_RELOAD, PermissionTypes.ADMIN_RENAME, PermissionTypes.ADMIN_CONVERT, PermissionTypes.ADMIN_EXPORT, PermissionTypes.ADMIN_EDITORS_ADD, PermissionTypes.ADMIN_EDITORS_REMOVE, PermissionTypes.ADMIN_WARP_OTHERS, PermissionTypes.ADMIN_CHANGE_CREATOR, };

    private static PermissionTypes[] DEFAULT_PERMISSIONS = new PermissionTypes[] { PermissionTypes.TO_GLOBAL, PermissionTypes.TO_OWN, PermissionTypes.TO_OTHER, PermissionTypes.TO_INVITED, PermissionTypes.SIGN_WARP_GLOBAL, PermissionTypes.SIGN_WARP_OWN, PermissionTypes.SIGN_WARP_OTHER, PermissionTypes.SIGN_WARP_INVITED, PermissionTypes.CREATE_PRIVATE, PermissionTypes.CREATE_PUBLIC, PermissionTypes.CREATE_GLOBAL, };

    private PermissionHandler handler = null;

    public String getGroup(String world, String player) {
        if (this.handler == null) {
            return null;
        } else {
            return this.handler.getGroup(world, player);
        }
    }

    private boolean permissionInternal(CommandSender sender, PermissionTypes permission) {
        if (MinecraftUtil.contains(permission, DEFAULT_PERMISSIONS)) {
            return true;
        } else if (MinecraftUtil.contains(permission, ADMIN_PERMISSIONS)) {
            return sender.isOp();
        } else {
            return false;
        }
    }

    public boolean permission(CommandSender sender, PermissionTypes permission) {
        Player player = MinecraftUtil.getPlayer(sender);
        if (player != null && this.handler != null) {
            return this.handler.has(player, permission.name);
        } else {
            return this.permissionInternal(sender, permission);
        }
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

    public int getInteger(CommandSender sender, PermissionValues value, int def) {
        if (this.handler != null) {
            Player player = MinecraftUtil.getPlayer(sender);
            if (player != null) {
                int result = this.handler.getPermissionInteger(player.getWorld().getName(), player.getName(), value.name);
                return result < 0 ? def : result;
            } else {
                return def;
            }
        } else {
            return def;
        }
    }

    public void init(Plugin plugin) {
        this.handler = null;
        if (plugin != null) {
            if (plugin.isEnabled()) {
                this.handler = ((Permissions) plugin).getHandler();
                MyWarp.logger.info("Permissions enabled.");
            } else {
                MyWarp.logger.info("Permissions system found, but not enabled. Use defaults.");
            }
        } else {
            MyWarp.logger.warning("Permission system not found. Use defaults.");
        }
    }

    public boolean useOfficial() {
        return this.handler != null;
    }
}
