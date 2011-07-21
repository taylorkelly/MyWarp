package de.xzise.xwarp.wrappers.permission;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.xwarp.Warp.Visibility;

public enum PricePermissions implements Permission<Double>, VisibilityPermission {

    // Prices (warp)
    WARP_PRICES_TO_PRIVATE("warp.prices.to.private", Visibility.PRIVATE),
    WARP_PRICES_TO_PUBLIC("warp.prices.to.public", Visibility.PUBLIC),
    WARP_PRICES_TO_GLOBAL("warp.prices.to.global", Visibility.GLOBAL),

    // Prices (create)
    WARP_PRICES_CREATE_PRIVATE("warp.prices.create.private", Visibility.PRIVATE),
    WARP_PRICES_CREATE_PUBLIC("warp.prices.create.public", Visibility.PUBLIC),
    WARP_PRICES_CREATE_GLOBAL("warp.prices.create.global", Visibility.GLOBAL),
    ;
    
    public final String name;
    public final double def;
    public final Visibility visibility;

    private PricePermissions(String name) {
        this(name, null);
    }
    
    private PricePermissions(String name, double def) {
        this(name, def, null);
    }
    
    private PricePermissions(String name, Visibility visibility) {
        this(name, 0, visibility);
    }
    
    private PricePermissions(String name, double def, Visibility visibility) {
        this.name = name;
        this.def = def;
        this.visibility = visibility;
    }
    
    public static PricePermissions getType(String name) {
        for (PricePermissions type : PricePermissions.values()) {
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
    public Double getDefault() {
        return this.def;
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }

}
