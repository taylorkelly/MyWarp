package de.xzise.xwarp.wrappers.permission;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.xwarp.Warp.Visibility;


public enum PermissionValues implements Permission<Integer>, VisibilityPermission {
    /*
     * VALUES
     */
    // Cooldown
    WARP_COOLDOWN_PRIVATE("warp.timers.cooldown.private", Visibility.PRIVATE),
    WARP_COOLDOWN_PUBLIC("warp.timers.cooldown.public", Visibility.PUBLIC),
    WARP_COOLDOWN_GLOBAL("warp.timers.cooldown.global", Visibility.GLOBAL),

    // Warmup
    WARP_WARMUP_PRIVATE("warp.timers.warmup.private", Visibility.PRIVATE),
    WARP_WARMUP_PUBLIC("warp.timers.warmup.public", Visibility.PUBLIC),
    WARP_WARMUP_GLOBAL("warp.timers.warmup.global", Visibility.GLOBAL),

    // Limits
    WARP_LIMIT_PRIVATE("warp.limit.private", -1, Visibility.PRIVATE),
    WARP_LIMIT_PUBLIC("warp.limit.public", -1, Visibility.PUBLIC),
    WARP_LIMIT_GLOBAL("warp.limit.global", -1, Visibility.GLOBAL),
    WARP_LIMIT_TOTAL("warp.limit.total", -1),

    ;

    public final String name;
    public final int def;
    public final Visibility visibility;

    private PermissionValues(String name) {
        this(name, null);
    }
    
    private PermissionValues(String name, int def) {
        this(name, def, null);
    }
    
    private PermissionValues(String name, Visibility visibility) {
        this(name, 0, visibility);
    }
    
    private PermissionValues(String name, int def, Visibility visibility) {
        this.name = name;
        this.def = def;
        this.visibility = visibility;
    }

    public static PermissionValues getType(String name) {
        for (PermissionValues type : PermissionValues.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getDefault() {
        return this.def;
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }
}