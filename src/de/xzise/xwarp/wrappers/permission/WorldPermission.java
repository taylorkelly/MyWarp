package de.xzise.xwarp.wrappers.permission;

import de.xzise.wrappers.permissions.BufferPermission;
import de.xzise.wrappers.permissions.Permission;

public enum WorldPermission {
    // Warp to worlds
    TO_WORLD("warp.world.to"),
    WITHIN_WORLD("warp.world.within");
    
    public Permission<Boolean> getPermission(String world, boolean def) {
        return getPermission(this, world, def);
    }
    
    public static Permission<Boolean> getPermission(WorldPermission worldPermission, String world, boolean def) {
        return new BufferPermission(worldPermission.name + "." + world, def);
    }
    
    public final String name;

    private WorldPermission(String name) {
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