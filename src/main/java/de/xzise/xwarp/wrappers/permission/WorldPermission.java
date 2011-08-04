package de.xzise.xwarp.wrappers.permission;

import org.bukkit.plugin.PluginManager;

import de.xzise.MinecraftUtil;
import de.xzise.wrappers.permissions.SuperPerm;
import de.xzise.wrappers.permissions.SuperPermBufferPermission;
import de.xzise.xwarp.XWarp;

public enum WorldPermission {
    // Warp to worlds
    TO_WORLD("xwarp.warp.world.to.", "Allows you to warp into the world "),
    WITHIN_WORLD("xwarp.warp.world.within.", "Allows you to warp within the world ");

    public SuperPermBufferPermission getPermission(String world, boolean def) {
        return new SuperPermBufferPermission(this.getName(world), this.description + world, def);
    }

    public SuperPermBufferPermission getPermission(String world) {
        return getPermission(world, true);
    }

    public String getName(String world) {
        return this.name + world;
    }

    public final String name;
    public final String description;

    private WorldPermission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static void register(String world, PluginManager pluginManager) {
        SuperPerm[] superPerms = new SuperPerm[WorldPermission.values().length];
        int idx = 0;
        for (WorldPermission worldPermission : WorldPermission.values()) {
            superPerms[idx++] = worldPermission.getPermission(world);
        }
        MinecraftUtil.register(pluginManager, XWarp.logger, superPerms);
    }

    public static void unregister(String world, PluginManager pluginManager) {
        try {
            for (WorldPermission worldPermission : WorldPermission.values()) {
                pluginManager.removePermission(worldPermission.getName(world));
            }
        } catch (NoSuchMethodError e) {
            XWarp.logger.warning("Unable to unregister the world permissions.");
        }
    }
}