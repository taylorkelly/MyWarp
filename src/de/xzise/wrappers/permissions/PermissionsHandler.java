package de.xzise.wrappers.permissions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.xzise.XLogger;
import de.xzise.wrappers.Factory;
import de.xzise.wrappers.Handler;

public class PermissionsHandler extends Handler<PermissionsWrapper> {

    public static final Map<String, Factory<PermissionsWrapper>> FACTORIES = new HashMap<String, Factory<PermissionsWrapper>>();
    private static final DefaultPermissions DEFAULT_PERMISSIONS = new DefaultPermissions();
    private static final PermissionsWrapper NULLARY_PERMISSIONS = new PermissionsWrapper() {
        
        @Override
        public Plugin getPlugin() {
            return null;
        }
        
        @Override
        public Boolean has(CommandSender sender, Permission<Boolean> permission) {
            return null;
        }
        
        @Override
        public Integer getInteger(CommandSender sender, Permission<Integer> permission) {
            return null;
        }

        @Override
        public String getGroup(String world, String player) {
            return null;
        }
    };
    
    static {
        FACTORIES.put("Permissions", new PermissionsPluginWrapper.PermissionsPluginFactory());
    }

    public PermissionsHandler(PluginManager pluginManager, String plugin, XLogger logger) {
        super(FACTORIES, NULLARY_PERMISSIONS, pluginManager, "permissions", plugin, logger);
    }

    public boolean permission(CommandSender sender, Permission<Boolean> permission) {
        Boolean result = this.getWrapper().has(sender, permission);
        if (result != null) {
            return result;
        } else {
            return DEFAULT_PERMISSIONS.has(sender, permission);
        }
    }

    public boolean permissionOr(CommandSender sender, Permission<Boolean>... permissions) {
        return this.permissionOr(sender, Arrays.asList(permissions));
    }
    
    public boolean permissionOr(CommandSender sender, List<Permission<Boolean>> permissions) {
        for (Permission<Boolean> permission : permissions) {
            if (this.permission(sender, permission)) {
                return true;
            }
        }
        return false;
    }

    public int getInteger(CommandSender sender, Permission<Integer> permission) {
        Integer result = this.getWrapper().getInteger(sender, permission);
        if (result != null) {
            return result;
        } else {
            return DEFAULT_PERMISSIONS.getInteger(sender, permission);
        }
    }

    public String getGroup(String world, String player) {
        return this.getWrapper().getGroup(world, player);
    }
    
}
