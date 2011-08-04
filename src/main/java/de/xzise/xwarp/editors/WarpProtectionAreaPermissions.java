package de.xzise.xwarp.editors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.xzise.xwarp.wrappers.permission.WPAPermissions;

public enum WarpProtectionAreaPermissions implements Editor {
    RENAME('m', 1, "rename", WPAPermissions.ADMIN_RENAME, WPAPermissions.EDIT_RENAME),
    UNINVITE('u', 2, "uninvite", WPAPermissions.ADMIN_UNINVITE, WPAPermissions.EDIT_UNINVITE),
    INVITE('i', 3, "invite", WPAPermissions.ADMIN_INVITE, WPAPermissions.EDIT_INVITE),
    GIVE('g', 7, "give", WPAPermissions.ADMIN_CHANGE_OWNER, WPAPermissions.EDIT_CHANGE_OWNER),
    DELETE('d', 8, "delete", WPAPermissions.ADMIN_DELETE, WPAPermissions.EDIT_DELETE),
    OVERWRITE('o', 9, "overwrite", WPAPermissions.ADMIN_IGNORE_PROTECTION_AREA, null),
    ADD_EDITOR('a', 10, "add editor", WPAPermissions.ADMIN_EDITORS_ADD, WPAPermissions.EDIT_EDITORS_ADD),
    REMOVE_EDITOR('r', 11, "remove editor", WPAPermissions.ADMIN_EDITORS_REMOVE, WPAPermissions.EDIT_EDITORS_REMOVE),
    LIST('v', 15, "list", WPAPermissions.ADMIN_LIST_CHANGE, WPAPermissions.EDIT_LIST),

    ;
    
    public final char value;
    public final int id;
    public final String name;
    public final WPAPermissions adminPermission;
    public final WPAPermissions defaultPermission;
    
    public static final ImmutableSet<WarpProtectionAreaPermissions> DEFAULT;
    public static final ImmutableMap<Character, WarpProtectionAreaPermissions> CHARACTER_MAP = EditorPermissionUtil.createEnumMap(WarpProtectionAreaPermissions.class, EditorPermissionUtil.VALUE_CALLBACK);
    public static final ImmutableMap<Integer, WarpProtectionAreaPermissions> ID_MAP = EditorPermissionUtil.createEnumMap(WarpProtectionAreaPermissions.class, EditorPermissionUtil.ID_CALLBACK);
    public static final ImmutableMap<String, WarpProtectionAreaPermissions> STRING_MAP = EditorPermissionUtil.createEnumMap(WarpProtectionAreaPermissions.class, EditorPermissionUtil.NAME_CALLBACK);
    
    private WarpProtectionAreaPermissions(char value, int id, String name, WPAPermissions adminPermission, WPAPermissions defaultPermission) {
        this.value = value;
        this.id = id;
        this.name = name;
        this.adminPermission = adminPermission;
        this.defaultPermission = defaultPermission;
    }

    static {
        DEFAULT = ImmutableSet.of(RENAME, UNINVITE, INVITE, OVERWRITE);
    }

    public static ImmutableSet<WarpProtectionAreaPermissions> parseString(String permissions) {
        return EditorPermissionUtil.parseString(permissions, WarpProtectionAreaPermissions.class, DEFAULT, CHARACTER_MAP);
    }

    @Override
    public WPAPermissions getDefault() {
        return this.defaultPermission;
    }

    @Override
    public WPAPermissions getAdmin() {
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
