package me.taylorkelly.mywarp;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;

public class WarpPermissions {
    private static Permissions permissionsPlugin;
    private static boolean permissionsEnabled = false;

    public static void initialize(Server server) {
        Plugin test = server.getPluginManager().getPlugin("Permissions");
        if (test != null) {
            Logger log = Logger.getLogger("Minecraft");
            permissionsPlugin = ((Permissions) test);
            permissionsEnabled = true;
            log.log(Level.INFO, "[MYWARP] Permissions enabled.");
        } else {
            Logger log = Logger.getLogger("Minecraft");
            log.log(Level.SEVERE, "[MYWARP] Permissions isn't loaded, there are no restrictions.");
        }
    }

    public static boolean isAdmin(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.admin");
        } else {
            return player.isOp();
        }
    }

    private static boolean permission(Player player, String string) {
        return permissionsPlugin.Security.permission(player, string);
    }

    private static int getNum(Player player, String string) {
        return permissionsPlugin.Security.getPermissionInteger(player.getName(), string);
    }


    public static boolean warp(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.basic.warp");
        } else {
            return true;
        }
    }

    public static boolean delete(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.basic.delete");
        } else {
            return true;
        }
    }

    public static boolean list(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.basic.list");
        } else {
            return true;
        }
    }

    public static boolean welcome(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.basic.welcome");
        } else {
            return true;
        }
    }

    public static boolean search(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.basic.search");
        } else {
            return true;
        }
    }

    public static boolean give(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.soc.give");
        } else {
            return true;
        }
    }

    public static boolean invite(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.soc.invite");
        } else {
            return true;
        }
    }

    public static boolean uninvite(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.soc.uninvite");
        } else {
            return true;
        }
    }

    public static boolean canPublic(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.soc.public");
        } else {
            return true;
        }
    }

    public static boolean canPrivate(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.soc.private");
        } else {
            return true;
        }
    }

    public static boolean signWarp(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.sign.warp");
        } else {
            return true;
        }
    }

    public static boolean privateCreate(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.basic.createprivate");
        } else {
            return true;
        }
    }
    
    public static boolean publicCreate(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.basic.createpublic");
        } else {
            return true;
        }
    }
    
    public static boolean compass(Player player) {
        if (permissionsEnabled) {
            return permission(player, "mywarp.warp.basic.compass");
        } else {
            return true;
        }
    }

    public static int maxPrivateWarps(Player player) {
        return WarpSettings.maxPrivate;
    }
    
    public static int maxPublicWarps(Player player) {
        return WarpSettings.maxPublic;
    }
}
