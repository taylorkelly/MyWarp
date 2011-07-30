package de.xzise.xwarp.wrappers.permission;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.google.common.collect.ImmutableSet;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.xwarp.Warp.Visibility;

public enum PermissionTypes implements Permission<Boolean>, VisibilityPermission {
    // Warp via command
    TO_GLOBAL("to.global", true, "Warp to global warps via commands"),
    TO_OWN("to.own", true, "Warp to own warps via commands"),
    TO_INVITED("to.invited", true, "Warp to invited warp via commands"),
    TO_OTHER("to.other", true, "Warp to public warps via commands"),

    // Use warp signs
    SIGN_WARP_GLOBAL("sign.to.global", true, "Warp to global warps via a warp sign"),
    SIGN_WARP_OWN("sign.to.own", true, "Warp to global own via a warp sign"),
    SIGN_WARP_INVITED("sign.to.invited", true, "Warp to invited warps via a warp sign"),
    SIGN_WARP_OTHER("sign.to.other", true, "Warp to public warps via a warp sign"),

    // Create warp signs
    CREATE_SIGN_PRIVATE("sign.create.private", true, "Create warp signs to private warps", Visibility.PRIVATE),
    CREATE_SIGN_PUBLIC("sign.create.public", true, "Create warp signs to public warps", Visibility.PUBLIC),
    CREATE_SIGN_GLOBAL("sign.create.global", true, "Create warp signs to global warps", Visibility.GLOBAL),
    SIGN_CREATE_UNKNOWN("sign.create.unknown", true, "Create warp signs to warps which doesn't exists"),

    // Create warps
    CREATE_PRIVATE("create.private", true, "Create private warps", Visibility.PRIVATE),
    CREATE_PUBLIC("create.public", true, "Create public warps", Visibility.PUBLIC),
    CREATE_GLOBAL("create.global", true, "Create global warps", Visibility.GLOBAL),

    // Edit own warps
    EDIT_DELETE("edit.delete", true, "Delete own warps"),
    EDIT_INVITE("edit.invite.add", true, "Invite to own warps"),
    EDIT_UNINVITE("edit.invite.delete", true, "Uninvite from own warps"),
    EDIT_MESSAGE("edit.message", true, "Change welcome message of own warps"),
    EDIT_LOCATION("edit.update", true, "Relocate own warps"),
    EDIT_RENAME("edit.rename", true, "Rename own warps"),
    // EDIT_(PRIVATE|PUBLIC|GLOBAL) == CREATE_*
    EDIT_EDITORS_ADD("edit.editors.add", true, "Add an editor to own warps"),
    EDIT_EDITORS_REMOVE("edit.editors.remove", true, "Removes an editor from own warps"),
    EDIT_CHANGE_OWNER("edit.owner", true, "Change owner of own warps"),
    EDIT_CHANGE_CREATOR("edit.creator", false, "Change creator of own warps (Handle with care!)"),
    EDIT_PRICE("edit.price.set", true, "Change price of own warps"),
    EDIT_FREE("edit.price.free", false, "Make own warps completely free (Handle with care!)"),
    EDIT_LIST("edit.list", false, "Change if owned warps are listed"),

    // Access to list
    CMD_LIST("command.list", true, "Execute the warp list command"),
    CMD_SEARCH("command.search", true, "Execute the warp search command"),
    CMD_INFO("command.info", true, "Execute the warp info command"),

    // Edit warp owned by others
    ADMIN_DELETE("admin.delete", false, "Delete all warps"),
    ADMIN_INVITE("admin.invite", false, "Invite to all warps"),
    ADMIN_UNINVITE("admin.uninvite", false, "Uninvite to all warps"),
    ADMIN_MESSAGE("admin.message", false, "Change welcome message of all warps"),
    ADMIN_UPDATE("admin.update", false, "Relocate all warps"),
    ADMIN_RENAME("admin.rename", false, "Rename all warps"),
    ADMIN_PRIVATE("admin.private", false, "Change visibility to private to all warps"),
    ADMIN_PUBLIC("admin.public", false, "Change visibility to public to all warps"),
    ADMIN_GLOBAL("admin.global", false, "Change visibility to global to all warps"),
    ADMIN_EDITORS_ADD("admin.editors.add", false, "Add editors to all warps"),
    ADMIN_EDITORS_REMOVE("admin.editors.remove", false, "Remove editors from all warps"),
    ADMIN_CHANGE_OWNER("admin.give.owner", false, "Change owner of all warps"),
    ADMIN_CHANGE_CREATOR("admin.changecreator", false, "Change creator of all warps"),
    ADMIN_LIST_CHANGE("admin.list.change", false, "Change if all warps are listed"),
    ADMIN_PRICE("admin.price.set", false, "Change price of all warps"),
    ADMIN_FREE("admin.price.free", false, "Make all warps completely free"),

    ADMIN_TO_ALL("admin.to.all", false, "Warp to all warps"),
    ADMIN_RELOAD("admin.reload", false, "Reload from database"),
//    ADMIN_CONVERT("warp.admin.convert", false),
    ADMIN_EXPORT("admin.export", false, "Export warps"),
    ADMIN_IMPORT("admin.export", false, "Import warps"),
    ADMIN_WARP_OTHERS("admin.warp.others", false, "Warp other players"),
    ADMIN_LIST_VIEW("admin.list.view", false, "List also not listed warps"),

    ADMIN_IGNORE_PROTECTION_AREA("admin.area.ignore", false, "Ignore warp protection areas"),
    ;

    // Maybe upcoming permissions:
    // Different admin permissions for each warp (only edit public warps
    // e.g.)

    public final String name;
    public final boolean def;
    public final String description;
    public final Visibility visibility;

    public final static ImmutableSet<PermissionTypes> WARP_TO_PERMISSIONS = ImmutableSet.of(ADMIN_TO_ALL, TO_GLOBAL, TO_INVITED, TO_OTHER, TO_OWN);

    private PermissionTypes(String name, boolean def, String description) {
        this(name, def, description, null);
    }
    
    private PermissionTypes(String name, boolean def, String description, Visibility visibility) {
        this.name = "xwarp." + name;
        this.def = def;
        this.description = description;
        this.visibility = visibility;
    }

    public void register(PluginManager pluginManager) throws ClassNotFoundException {
        pluginManager.addPermission(new org.bukkit.permissions.Permission(this.name, this.description, this.def ? PermissionDefault.TRUE : PermissionDefault.OP));
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