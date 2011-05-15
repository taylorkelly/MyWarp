package de.xzise.xwarp.wrappers.permission;

import java.util.ArrayList;
import java.util.List;

import me.taylorkelly.mywarp.Warp.Visibility;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.wrappers.permissions.VisibilityPermission;

public enum PermissionTypes implements Permission<Boolean>, VisibilityPermission {
    
    // Warp to global warps
    TO_GLOBAL("warp.to.global", true),
    // Warp to own warps
    TO_OWN("warp.to.own", true),
    // Warp to invited warps
    TO_INVITED("warp.to.invited", true),
    // Warp to public warps
    TO_OTHER("warp.to.other", true),

    // Warp with sign to global
    SIGN_WARP_GLOBAL("warp.sign.to.global", true),
    // Warp to own warps
    SIGN_WARP_OWN("warp.sign.to.own", true),
    // Warp to invited warps
    SIGN_WARP_INVITED("warp.sign.to.invited", true),
    // Warp to public warps
    SIGN_WARP_OTHER("warp.sign.to.other", true),
    
    // Create warp sign to private warp
    CREATE_SIGN_PRIVATE("warp.sign.create.private", true, Visibility.PRIVATE),
    // Create warp sign to public warp
    CREATE_SIGN_PUBLIC("warp.sign.create.public", true, Visibility.PUBLIC),
    // Create warp sign to global warp
    CREATE_SIGN_GLOBAL("warp.sign.create.global", true, Visibility.GLOBAL),
    // Create warp sign to warp which doesn't exists
    SIGN_CREATE_UNKNOWN("warp.sign.create.unknown", true),

    // Create/Edit private warps
    CREATE_PRIVATE("warp.create.private", true, Visibility.PRIVATE),
    // Create/Edit public warps
    CREATE_PUBLIC("warp.create.public", true, Visibility.PUBLIC),
    // Create/Edit global warps
    CREATE_GLOBAL("warp.create.global", true, Visibility.GLOBAL),
    
    // Edit own warps
    EDIT_DELETE("warp.edit.delete", true),
    EDIT_INVITE("warp.edit.invite.add", true),
    EDIT_UNINVITE("warp.edit.invite.delete", true),
    EDIT_MESSAGE("warp.edit.message", true),
    EDIT_LOCATION("warp.edit.update", true),
    EDIT_RENAME("warp.edit.rename", true),
    // EDIT_(PRIVATE|PUBLIC|GLOBAL) == CREATE_*
    EDIT_EDITORS_ADD("warp.edit.editors.add", true),
    EDIT_EDITORS_REMOVE("warp.edit.editors.remove", true),
    EDIT_CHANGE_OWNER("warp.edit.owner", true),
    EDIT_CHANGE_CREATOR("warp.edit.creator", false),
    EDIT_PRICE("warp.edit.price.set", true),
    EDIT_FREE("warp.edit.price.free", false),
    EDIT_LIST("warp.edit.list", false),

    // Access to list
    CMD_LIST("warp.command.list", true),
    CMD_SEARCH("warp.command.search", true),
    CMD_INFO("warp.command.info", true),

    // Delete all warps
    ADMIN_DELETE("warp.admin.delete", false),
    // Invite to all warps
    ADMIN_INVITE("warp.admin.invite", false),
    // Uninvite to all warps
    ADMIN_UNINVITE("warp.admin.uninvite", false),
    // Edit the welcome message of all warps
    ADMIN_MESSAGE("warp.admin.message", false),
    // Update all warps
    ADMIN_UPDATE("warp.admin.update", false),
    // Rename all warps
    ADMIN_RENAME("warp.admin.rename", false),
    // Make other's warp privates
    ADMIN_PRIVATE("warp.admin.private", false),
    // Make other's warp public
    ADMIN_PUBLIC("warp.admin.public", false),
    // Make other's warps global
    ADMIN_GLOBAL("warp.admin.global", false),
    // Warp to all warps
    ADMIN_TO_ALL("warp.admin.to.all", false),
    // Reload database
    ADMIN_RELOAD("warp.admin.reload", false),
    // Converts from hmod file
    ADMIN_CONVERT("warp.admin.convert", false),
    // Export warps
    ADMIN_EXPORT("warp.admin.export", false),
    // Converts from hmod file
    ADMIN_EDITORS_REMOVE("warp.admin.editors.remove", false),
    // Converts from hmod file
    ADMIN_EDITORS_ADD("warp.admin.editors.add", false),
    // Give away all warps
    ADMIN_CHANGE_OWNER("warp.admin.give.owner", false),
    // Change the creator
    ADMIN_CHANGE_CREATOR("warp.admin.changecreator", false),
    // Warp other players
    ADMIN_WARP_OTHERS("warp.admin.warp.others", false),
    // Change the price
    ADMIN_PRICE("warp.admin.price.set", false),
    // Change the price to free
    ADMIN_FREE("warp.admin.price.free", false),
    // Hide from list/Show on list
    ADMIN_LIST_CHANGE("warp.admin.list.change", false),
    // Hide from list/Show on list
    ADMIN_LIST_VIEW("warp.admin.list.view", false),
    ;

    // Maybe upcoming permissions:
    // Different admin permissions for each warp (only edit public warps
    // e.g.)

    public final String name;
    public final boolean def;
    public final Visibility visibility;

    private PermissionTypes(String name, boolean def) {
        this(name, def, null);
    }
    
    private PermissionTypes(String name, boolean def, Visibility visibility) {
        this.name = name;
        this.def = def;
        this.visibility = visibility;
    }
    
    public Boolean getDefault() {
        return this.def;
    }
    
    public static PermissionTypes getType(String name) {
        for (PermissionTypes type : PermissionTypes.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static List<Permission<Boolean>> getDefaultPermissions(boolean def) {
        List<Permission<Boolean>> permissions = new ArrayList<Permission<Boolean>>();
        for (Permission<Boolean> permission : PermissionTypes.values()) {
            if (permission.getDefault() == def) {
                permissions.add(permission);
            }
        }
        return permissions;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }
}