package de.xzise.xwarp.wrappers.permission;

import org.bukkit.permissions.PermissionDefault;

import de.xzise.wrappers.permissions.Permission;
import de.xzise.wrappers.permissions.SuperPerm;

public enum GeneralPermissions implements Permission<Boolean>, SuperPerm {
    RELOAD("reload", false, "Allows you to reload all warps and protection areas"),
    EXPORT("export", false, "Allows you to export all warps and protection areas"),
    IMPORT("import", false, "Allows you to import all warps and protection areas"), 
    ;

    public final String name;
    public final boolean def;
    public final String description;

    private GeneralPermissions(String name, boolean def, String description) {
        this.name = "xwarp.admin." + name;
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
