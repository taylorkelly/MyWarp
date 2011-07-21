package de.xzise.xwarp.wrappers.permission;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.editors.Editor;

public class WarpEditorPermission implements Permission<Boolean> {

    // xwarp.edit.warp.<owner>.<name>.<operation>
    
    //TODO: Maybe special permissions (e.g. permission to warp)
    
    public final static String PREFIX = "xwarp.edit.warp.";
    
    private final String permission;
    
    public WarpEditorPermission(WarpObject warpObject, Editor permission) {
        this.permission = PREFIX + warpObject.getOwner() + "." + warpObject.getName() + "." + permission.getName();
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
