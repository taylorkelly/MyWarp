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
        TO_GLOBAL("warp.to.global", true),
        // Warp to own warps
        TO_OWN("warp.to.own", true),
        // Warp to invited warps
        TO_INVITED("warp.to.invited", true),
        // Warp to public warps
        TO_OTHER("warp.to.other", true),

        // Warp with sign to global
        SIGN_WARP_GLOBAL("warp.sign.to.global", true),
        // Warp to own warps
        SIGN_WARP_OWN("warp.sign.to.own", true),
        // Warp to invited warps
        SIGN_WARP_INVITED("warp.sign.to.invited", true),
        // Warp to public warps
        SIGN_WARP_OTHER("warp.sign.to.other", true),
        
        // Create warp sign to private warp
        CREATE_SIGN_PRIVATE("warp.sign.create.private", true),
        // Create warp sign to public warp
        CREATE_SIGN_PUBLIC("warp.sign.create.public", true),
        // Create warp sign to global warp
        CREATE_SIGN_GLOBAL("warp.sign.create.global", true),
        // Create warp sign to warp which doesn't exists
        SIGN_CREATE_UNKNOWN("warp.sign.create.unknown", true),

        // Create/Edit private warps
        CREATE_PRIVATE("warp.create.private", true),
        // Create/Edit public warps
        CREATE_PUBLIC("warp.create.public", true),
        // Create/Edit global warps
        CREATE_GLOBAL("warp.create.global", true),
        
        // Edit own warps
        EDIT_DELETE("warp.edit.delete", true),
        EDIT_INVITE("warp.edit.invite.add", true),
        EDIT_UNINVITE("warp.edit.invite.delete", true),
        EDIT_MESSAGE("warp.edit.message", true),
        EDIT_LOCATION("warp.edit.update", true),
        EDIT_RENAME("warp.edit.rename", true),
        // EDIT_(PRIVATE|PUBLIC|GLOBAL) == CREATE_*
        EDIT_EDITORS_ADD("warp.edit.editors.add", true),
        EDIT_EDITORS_REMOVE("warp.edit.editors.remove", true),
        EDIT_CHANGE_OWNER("warp.edit.owner", true),
        EDIT_CHANGE_CREATOR("warp.edit.creator", false),
        EDIT_PRICE("warp.edit.price.set", true),
        EDIT_FREE("warp.edit.price.free", false),
        EDIT_LIST("warp.edit.list", false),

        // Access to list
        CMD_LIST("warp.command.list", true),
        CMD_SEARCH("warp.command.search", true),
        CMD_INFO("warp.command.info", true),

        // Delete all warps
        ADMIN_DELETE("warp.admin.delete", false),
        // Invite to all warps
        ADMIN_INVITE("warp.admin.invite", false),
        // Uninvite to all warps
        ADMIN_UNINVITE("warp.admin.uninvite", false),
        // Edit the welcome message of all warps
        ADMIN_MESSAGE("warp.admin.message", false),
        // Update all warps
        ADMIN_UPDATE("warp.admin.update", false),
        // Rename all warps
        ADMIN_RENAME("warp.admin.rename", false),
        // Make other's warp privates
        ADMIN_PRIVATE("warp.admin.private", false),
        // Make other's warp public
        ADMIN_PUBLIC("warp.admin.public", false),
        // Make other's warps global
        ADMIN_GLOBAL("warp.admin.global", false),
        // Warp to all warps
        ADMIN_TO_ALL("warp.admin.to.all", false),
        // Reload database
        ADMIN_RELOAD("warp.admin.reload", false),
        // Converts from hmod file
        ADMIN_CONVERT("warp.admin.convert", false),
        // Export warps
        ADMIN_EXPORT("warp.admin.export", false),
        // Converts from hmod file
        ADMIN_EDITORS_REMOVE("warp.admin.editors.remove", false),
        // Converts from hmod file
        ADMIN_EDITORS_ADD("warp.admin.editors.add", false),
        // Give away all warps
        ADMIN_CHANGE_OWNER("warp.admin.give.owner", false),
        // Change the creator
        ADMIN_CHANGE_CREATOR("warp.admin.changecreator", false),
        // Warp other players
        ADMIN_WARP_OTHERS("warp.admin.warp.others", false),
        // Change the price
        ADMIN_PRICE("warp.admin.price.set", false),
        // Change the price to free
        ADMIN_FREE("warp.admin.price.free", false),
        // Hide from list/Show on list
        ADMIN_LIST("warp.admin.list", false),
        ;

        // Maybe upcoming permissions:
        // Different admin permissions for each warp (only edit public warps
        // e.g.)

        public final String name;
        private final boolean def;

        private PermissionTypes(String name, boolean def) {
            this.name = name;
            this.def = def;
        }
        
        public boolean isDefault() {
            return this.def;
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

    public enum WorldPermission {
        // Warp to worlds
        TO_WORLD("warp.to.world.to"),
        WITHIN_WORLD("warp.to.world.within");
        
        
        public final String name;

        WorldPermission(String name) {
            this.name = name;
        }

        public static WorldPermission getType(String name) {
            for (WorldPermission type : WorldPermission.values()) {
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

    private PermissionHandler handler = null;

    public String getGroup(String world, String player) {
        if (this.handler == null) {
            return null;
        } else {
            return this.handler.getGroup(world, player);
        }
    }

    private boolean permissionInternal(CommandSender sender, PermissionTypes permission) {
        if (permission.isDefault()) {
            return true;
        } else {
            return sender.isOp();
        }
    }
    
    private Boolean has(CommandSender sender, String name) {
        Player player = MinecraftUtil.getPlayer(sender);
        if (player != null && this.handler != null) {
            return this.handler.has(player, name);
        } else {
            return null;
        }
    }

    public boolean permission(CommandSender sender, PermissionTypes permission) {
        Boolean hasPermission = this.has(sender, permission.name);
        if (hasPermission == null) {
            return this.permissionInternal(sender, permission);
        } else {
            return hasPermission;
        }
    }

    public boolean hasAdminPermission(CommandSender sender) {
        for (PermissionTypes type : PermissionTypes.values()) {
            if (this.permission(sender, type) && !type.isDefault()) {
                return true;
            }
        }
        return false;
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
    
    public boolean hasWorldPermission(CommandSender sender, WorldPermission permission, String world, boolean def) {
        Boolean hasPermission = this.has(sender, permission.name + "." + world);
        if (hasPermission != null) {
            return hasPermission;
        } else {
            return def;
        }
    }

    public void init(Plugin plugin) {
        this.handler = null;
        if (plugin != null) {
            if (plugin.isEnabled()) {
                this.handler = ((Permissions) plugin).getHandler();
                MyWarp.logger.info("Linked with permissions: " + plugin.getDescription().getFullName());
            } else {
                MyWarp.logger.info("Doesn't link to disabled permissions: " + plugin.getDescription().getFullName());
            }
        } else {
            MyWarp.logger.warning("No permissions found until here. Permissions will be maybe activated later. Use defaults.");
        }
    }

    public boolean useOfficial() {
        return this.handler != null;
    }
}
