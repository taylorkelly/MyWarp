package de.xzise.xwarp.wrappers.permission;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.editors.Editor;

public class WarpEditorPermission implements Permission<Boolean> {

    // xwarp.<type>.edit.object.<owner>.<name>.<operation>

    public final static String PREFIX = "xwarp.edit.warp.";

    private final String permission;

    public <E extends Editor> WarpEditorPermission(WarpObject<E> warpObject, E permission) {
        this.permission = "xwarp." + warpObject.getType() + ".edit.object." + warpObject.getOwner() + "." + warpObject.getName() + "." + permission.getName();
    }

    @Override
    public String getName() {
        return this.permission;
    }

    @Override
    public Boolean getDefault() {
        return false;
    }

}
