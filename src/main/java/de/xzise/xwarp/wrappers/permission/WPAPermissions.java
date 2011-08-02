package de.xzise.xwarp.wrappers.permission;

import org.bukkit.permissions.PermissionDefault;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.wrappers.permissions.SuperPerm;

public enum WPAPermissions implements Permission<Boolean>, SuperPerm {
    // Create wpas
    CREATE("create.private", true, "Create warp protection areas"),

    // Edit own warps
    EDIT_DELETE("edit.delete", true, "Delete own warp protection areas"),
    EDIT_INVITE("edit.invite.add", true, "Invite to own warp protection areas"),
    EDIT_UNINVITE("edit.invite.delete", true, "Uninvite from own warp protection areas"),
    EDIT_RENAME("edit.rename", true, "Rename own warp protection areas"),
    EDIT_EDITORS_ADD("edit.editors.add", true, "Add an editor to own warp protection areas"),
    EDIT_EDITORS_REMOVE("edit.editors.remove", true, "Removes an editor from own warp protection areas"),
    EDIT_CHANGE_OWNER("edit.owner", true, "Change owner of own warp protection areas"),
    EDIT_CHANGE_CREATOR("edit.creator", false, "Change creator of own warp protection areas (Handle with care!)"),
    EDIT_LIST("edit.list", false, "Change if owned warps are listed"),

    // Access to list
    CMD_LIST("command.list", true, "Execute the wpa list command"),
    CMD_SEARCH("command.search", true, "Execute the wpa search command"),
    CMD_INFO("command.info", true, "Execute the wpa info command"),

    // Edit warp owned by others
    ADMIN_DELETE("admin.delete", false, "Delete all warp protection areas"),
    ADMIN_INVITE("admin.invite", false, "Invite to all warp protection areas"),
    ADMIN_UNINVITE("admin.uninvite", false, "Uninvite to all warp protection areas"),
    ADMIN_RENAME("admin.rename", false, "Rename all warp protection areas"),
    ADMIN_EDITORS_ADD("admin.editors.add", false, "Add editors to all warp protection areas"),
    ADMIN_EDITORS_REMOVE("admin.editors.remove", false, "Remove editors from all warp protection areas"),
    ADMIN_CHANGE_OWNER("admin.give.owner", false, "Change owner of all warp protection areas"),
    ADMIN_CHANGE_CREATOR("admin.changecreator", false, "Change creator of all warp protection areas"),
    ADMIN_LIST_CHANGE("admin.list.change", false, "Change if all warp protection areas are listed"),

    ADMIN_CREATE_STOP("admin.create.stop", false, "Stop creation for all warp protection areas"),
    ADMIN_LIST_VIEW("admin.list.view", false, "List also not listed warps"),

    ADMIN_IGNORE_PROTECTION_AREA("admin.area.ignore", false, "Ignore warp protection areas"),

    ;

    public final String name;
    public final boolean def;
    public final String description;

    private WPAPermissions(String name, boolean def, String description) {
        this.name = "xwarp.wpa." + name;
        this.def = def;
        this.description = description;
    }

    @Override
    public Boolean getDefault() {
        return this.def;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return this.def ? PermissionDefault.TRUE : PermissionDefault.OP;
    }

}
