package de.xzise.xwarp.wrappers.permission;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.google.common.collect.ImmutableSet;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.wrappers.permissions.SuperPerm;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.Warp.Visibility;

public enum PermissionTypes implements Permission<Boolean>, VisibilityPermission, SuperPerm {
    // Warp via command
    TO_GLOBAL("to.global", true, "Allows you to warp to a global warp with a command"),
    TO_OWN("to.own", true, "Allows you to warp to your own warp with a command"),
    TO_INVITED("to.invited", true, "Allows to warp to a invited warp with a command"),
    TO_OTHER("to.other", true, "Allows to warp to a public warp with a command"),

    // Use warp signs
    SIGN_WARP_GLOBAL("sign.to.global", true, "Allows you to use a warp sign to a global warp"),
    SIGN_WARP_OWN("sign.to.own", true, "Allows you to use a warp sign to your own warp"),
    SIGN_WARP_INVITED("sign.to.invited", true, "Allows you to use a warp sign to a invited warp"),
    SIGN_WARP_OTHER("sign.to.other", true, "Allows you to use a warp sign to a public warp"),

    // Create warp signs
    CREATE_SIGN_PRIVATE("sign.create.private", true, "Allows you to create warp signs to a private warp", Visibility.PRIVATE),
    CREATE_SIGN_PUBLIC("sign.create.public", true, "Allows you to create warp signs to a public warp", Visibility.PUBLIC),
    CREATE_SIGN_GLOBAL("sign.create.global", true, "Allows you to create warp signs to a global warp", Visibility.GLOBAL),
    SIGN_CREATE_UNKNOWN("sign.create.unknown", true, "Allows you to create warp signs to a warp which didn't exists"),

    // Create warps
    CREATE_PRIVATE("create.private", true, "Allows you to create private warps", Visibility.PRIVATE),
    CREATE_PUBLIC("create.public", true, "Allows you to create public warps", Visibility.PUBLIC),
    CREATE_GLOBAL("create.global", true, "Allows you to create global warps", Visibility.GLOBAL),

    // Edit own warps
    EDIT_DELETE("edit.delete", true, "Allows you to delete your own warps"),
    EDIT_INVITE("edit.invite.add", true, "Allows you to invite others to your warps"),
    EDIT_UNINVITE("edit.invite.delete", true, "Allows you to uninvite others to your warps"),
    EDIT_MESSAGE("edit.message", true, "Allows you to change the welcome message of your warps"),
    EDIT_LOCATION("edit.update", true, "Allows you to relocate your own warps"),
    EDIT_RENAME("edit.rename", true, "Allows you to rename your own warps"),
    // EDIT_(PRIVATE|PUBLIC|GLOBAL) == CREATE_*
    EDIT_EDITORS_ADD("edit.editors.add", true, "Allows you to add an editor to your warps"),
    EDIT_EDITORS_REMOVE("edit.editors.remove", true, "Allows you to remove an editor to your warps"),
    EDIT_CHANGE_OWNER("edit.owner", true, "Allows you to change the owner of your warps"),
    EDIT_CHANGE_CREATOR("edit.creator", false, "Allows you to change the creator of your warps"),
    EDIT_PRICE("edit.price.set", true, "Allows you to change the price of own warps"),
    EDIT_FREE("edit.price.free", false, "Allows you to make your warps completely free"),
    EDIT_LIST("edit.list", false, "Allows you to change if your warps are listed"),
    EDIT_COOLDOWN("edit.cooldown", true, "Allows you to set the cooldown of your warps"),
    EDIT_WARMUP("edit.warmup", true, "Allows you to set the warmup of your warps"),

    // Access to list
    CMD_LIST("command.list", true, "Allows you to execute the warp list command"),
    CMD_SEARCH("command.search", true, "Allows you to execute the warp search command"),
    CMD_INFO("command.info", true, "Allows you to execute the warp info command"),

    // Edit warp owned by others
    ADMIN_DELETE("admin.delete", false, "Allows you to delete any warp"),
    ADMIN_INVITE("admin.invite", false, "Allows you to invite others to any warp"),
    ADMIN_UNINVITE("admin.uninvite", false, "Allows you to uninvite others to any warp"),
    ADMIN_MESSAGE("admin.message", false, "Allows you to change the welcome message of any warp"),
    ADMIN_UPDATE("admin.update", false, "Allows you to relocate any warp"),
    ADMIN_RENAME("admin.rename", false, "Allows you to rename any warps"),
    ADMIN_PRIVATE("admin.private", false, "Allows you to change the visibility to private of any warp"),
    ADMIN_PUBLIC("admin.public", false, "Allows you to change the visibility to public of any warp"),
    ADMIN_GLOBAL("admin.global", false, "Allows you to change the visibility to global of any warp"),
    ADMIN_EDITORS_ADD("admin.editors.add", false, "Allows you to add an editor to any warp"),
    ADMIN_EDITORS_REMOVE("admin.editors.remove", false, "Allows you to remove an editor to any warp"),
    ADMIN_CHANGE_OWNER("admin.give.owner", false, "Allows you to change the owner of any warp"),
    ADMIN_CHANGE_CREATOR("admin.changecreator", false, "Allows you to change the creator of any warp"),
    ADMIN_PRICE("admin.price.set", false, "Allows you to change the price of any warp"),
    ADMIN_FREE("admin.price.free", false, "Allows you to make any warp completely free"),
    ADMIN_LIST_CHANGE("admin.list.change", false, "Allows you to change if any warp is listed"),
    ADMIN_COOLDOWN("admin.cooldown", false, "Allows you to set the cooldown of any warp"),
    ADMIN_WARMUP("admin.warmup", false, "Allows you to set the warmup of any warp"),

    ADMIN_LIST_VIEW("admin.list.view", false, "Allows you to list also not listed warps"),
    ADMIN_TO_ALL("admin.to.all", false, "Allows you to warp any warp"),
    ADMIN_WARP_OTHERS("admin.warp.others", false, "Allows you to warp other players"),
    ADMIN_CHANGE_WORLD("admin.changeworld", false, "Allows you to change the world of all warps in a specific world.");
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
        this.name = "xwarp.warp." + name;
        this.def = def;
        this.description = description;
        this.visibility = visibility;
    }

    public void register(PluginManager pluginManager) throws ClassNotFoundException {
        try {
        pluginManager.addPermission(new org.bukkit.permissions.Permission(this.name, this.description, this.def ? PermissionDefault.TRUE : PermissionDefault.OP));
        } catch (IllegalArgumentException e) {
            XWarp.logger.warning("Couldn't register the permission '" + this.name + "'!", e);
        }
    }

    @Override
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

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return this.def ? PermissionDefault.TRUE : PermissionDefault.OP;
    }
}