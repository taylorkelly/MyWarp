package de.xzise.xwarp.wrappers.permission;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.xwarp.Warp;

public class WarpToWarpPermission implements Permission<Boolean> {

    private final static String PREFIX = "warp.to.warp.";
    
    private final String name;
    
    public WarpToWarpPermission(Warp warp) {
        this.name = PREFIX + warp.getOwner() + "." + warp.getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean getDefault() {
        return false;
    }

}
