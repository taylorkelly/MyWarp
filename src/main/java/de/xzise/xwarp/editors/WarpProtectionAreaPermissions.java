package de.xzise.xwarp.editors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public enum WarpProtectionAreaPermissions implements Editor {
    UPDATE('l', 0, "update", PermissionTypes.ADMIN_UPDATE, PermissionTypes.EDIT_LOCATION),
    RENAME('m', 1, "rename", PermissionTypes.ADMIN_RENAME, PermissionTypes.EDIT_RENAME),
    UNINVITE('u', 2, "uninvite", PermissionTypes.ADMIN_UNINVITE, PermissionTypes.EDIT_UNINVITE),
    INVITE('i', 3, "invite", PermissionTypes.ADMIN_INVITE, PermissionTypes.EDIT_INVITE),
    GIVE('g', 7, "give", PermissionTypes.ADMIN_CHANGE_OWNER, PermissionTypes.EDIT_CHANGE_OWNER),
    DELETE('d', 8, "delete", PermissionTypes.ADMIN_DELETE, PermissionTypes.EDIT_DELETE),
    OVERWRITE('o', 9, "overwrite", PermissionTypes.ADMIN_TO_ALL, null),
    ADD_EDITOR('a', 10, "add editor", PermissionTypes.ADMIN_EDITORS_ADD, PermissionTypes.EDIT_EDITORS_ADD),
    REMOVE_EDITOR('r', 11, "remove editor", PermissionTypes.ADMIN_EDITORS_REMOVE, PermissionTypes.EDIT_EDITORS_REMOVE),
    LIST('v', 15, "list", PermissionTypes.ADMIN_LIST_CHANGE, PermissionTypes.EDIT_LIST),

    ;
    
    public final char value;
    public final int id;
    public final String name;
    public final PermissionTypes adminPermission;
    public final PermissionTypes defaultPermission;
    
    public static final ImmutableSet<WarpProtectionAreaPermissions> DEFAULT;
    public static final ImmutableMap<Character, WarpProtectionAreaPermissions> CHARACTER_MAP = EditorPermissionUtil.createEnumMap(WarpProtectionAreaPermissions.class, EditorPermissionUtil.VALUE_CALLBACK);
    public static final ImmutableMap<Integer, WarpProtectionAreaPermissions> ID_MAP = EditorPermissionUtil.createEnumMap(WarpProtectionAreaPermissions.class, EditorPermissionUtil.ID_CALLBACK);
    public static final ImmutableMap<String, WarpProtectionAreaPermissions> STRING_MAP = EditorPermissionUtil.createEnumMap(WarpProtectionAreaPermissions.class, EditorPermissionUtil.NAME_CALLBACK);
    
    private WarpProtectionAreaPermissions(char value, int id, String name, PermissionTypes adminPermission, PermissionTypes defaultPermission) {
        this.value = value;
        this.id = id;
        this.name = name;
        this.adminPermission = adminPermission;
        this.defaultPermission = defaultPermission;
    }

    static {
        DEFAULT = ImmutableSet.of(UPDATE, RENAME, UNINVITE, INVITE, OVERWRITE);
    }

    public static ImmutableSet<WarpProtectionAreaPermissions> parseString(String permissions) {
        return EditorPermissionUtil.parseString(permissions, WarpProtectionAreaPermissions.class, DEFAULT, CHARACTER_MAP);
    }

    @Override
    public PermissionTypes getDefault() {
        return this.defaultPermission;
    }

    @Override
    public PermissionTypes getAdmin() {
        return this.adminPermission;
    }

    @Override
    public char getValue() {
        return this.value;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
