package de.xzise.xwarp.wrappers.permission;

import de.xzise.wrappers.permissions.Permission;


public enum PermissionValues implements Permission<Integer> {
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
    WARP_LIMIT_PRIVATE("warp.limit.private", -1),
    WARP_LIMIT_PUBLIC("warp.limit.public", -1),
    WARP_LIMIT_GLOBAL("warp.limit.global", -1),
    WARP_LIMIT_TOTAL("warp.limit.total", -1),

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
    public final int def;

    private PermissionValues(String name) {
        this(name, 0);
    }
    
    private PermissionValues(String name, int def) {
        this.name = name;
        this.def = def;
    }

    public static PermissionTypes getType(String name) {
        for (PermissionTypes type : PermissionTypes.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getDefault() {
        // TODO Auto-generated method stub
        return null;
    }
}