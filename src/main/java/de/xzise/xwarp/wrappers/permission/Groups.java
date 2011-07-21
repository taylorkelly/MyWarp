package de.xzise.xwarp.wrappers.permission;



public class Groups {
    
    public static final Group<PermissionTypes> SIGN_CREATE_GROUP = new Group<PermissionTypes>(PermissionTypes.CREATE_SIGN_PRIVATE, PermissionTypes.CREATE_SIGN_PUBLIC, PermissionTypes.CREATE_SIGN_GLOBAL);
    public static final Group<PermissionTypes> CREATE_GROUP = new Group<PermissionTypes>(PermissionTypes.CREATE_PRIVATE, PermissionTypes.CREATE_PUBLIC, PermissionTypes.CREATE_GLOBAL);

    public static final Group<PermissionValues> TIMERS_COOLDOWN_GROUP = new Group<PermissionValues>(PermissionValues.WARP_COOLDOWN_PRIVATE, PermissionValues.WARP_COOLDOWN_PUBLIC, PermissionValues.WARP_COOLDOWN_GLOBAL);
    public static final Group<PermissionValues> TIMERS_WARMUP_GROUP = new Group<PermissionValues>(PermissionValues.WARP_WARMUP_PRIVATE, PermissionValues.WARP_WARMUP_PUBLIC, PermissionValues.WARP_WARMUP_GLOBAL);
    public static final Group<PermissionValues> LIMIT_GROUP = new Group<PermissionValues>(PermissionValues.WARP_LIMIT_PRIVATE, PermissionValues.WARP_LIMIT_PUBLIC, PermissionValues.WARP_LIMIT_GLOBAL);
    public static final Group<PricePermissions> PRICES_TO_GROUP = new Group<PricePermissions>(PricePermissions.WARP_PRICES_TO_PRIVATE, PricePermissions.WARP_PRICES_TO_PUBLIC, PricePermissions.WARP_PRICES_TO_GLOBAL);
    public static final Group<PricePermissions> PRICES_CREATE_GROUP = new Group<PricePermissions>(PricePermissions.WARP_PRICES_CREATE_PRIVATE, PricePermissions.WARP_PRICES_CREATE_PUBLIC, PricePermissions.WARP_PRICES_CREATE_GLOBAL);

}
