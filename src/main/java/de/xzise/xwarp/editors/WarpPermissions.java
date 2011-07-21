package de.xzise.xwarp.editors;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.xzise.xwarp.PermissionDouble;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public enum WarpPermissions implements PermissionDouble, Editor {
    UPDATE('l', 0, "update", PermissionTypes.ADMIN_UPDATE, PermissionTypes.EDIT_LOCATION),
    RENAME('r', 1, "rename", PermissionTypes.ADMIN_RENAME, PermissionTypes.EDIT_RENAME),
    UNINVITE('u', 2, "uninvite", PermissionTypes.ADMIN_UNINVITE, PermissionTypes.EDIT_UNINVITE),
    INVITE('i', 3, "invite", PermissionTypes.ADMIN_INVITE, PermissionTypes.EDIT_INVITE),
    PRIVATE('0', 4, "private", PermissionTypes.ADMIN_PRIVATE, PermissionTypes.CREATE_PRIVATE),
    PUBLIC('1', 5, "public", PermissionTypes.ADMIN_PUBLIC, PermissionTypes.CREATE_PUBLIC),
    GLOBAL('2', 6, "global", PermissionTypes.ADMIN_GLOBAL, PermissionTypes.CREATE_GLOBAL),
    GIVE('g', 7, "give", PermissionTypes.ADMIN_CHANGE_OWNER, PermissionTypes.EDIT_CHANGE_OWNER),
    DELETE('d', 8, "delete", PermissionTypes.ADMIN_DELETE, PermissionTypes.EDIT_DELETE),
    WARP('w', 9, "warp", PermissionTypes.ADMIN_TO_ALL, null),
    ADD_EDITOR('a', 10, "add editor", PermissionTypes.ADMIN_EDITORS_ADD, PermissionTypes.EDIT_EDITORS_ADD),
    REMOVE_EDITOR('f', 11, "remove editor", PermissionTypes.ADMIN_EDITORS_REMOVE, PermissionTypes.EDIT_EDITORS_REMOVE),
    MESSAGE('m', 12, "message", PermissionTypes.ADMIN_MESSAGE, PermissionTypes.EDIT_MESSAGE),
    PRICE('p', 13, "price", PermissionTypes.ADMIN_PRICE, PermissionTypes.EDIT_PRICE),
    FREE('c', 14, "free", PermissionTypes.ADMIN_FREE, PermissionTypes.EDIT_FREE),
    LIST('v', 15, "list", PermissionTypes.ADMIN_LIST_CHANGE, PermissionTypes.EDIT_LIST),

    ;

    public final char value;
    public final int id;
    public final String name;
    public final PermissionTypes adminPermission;
    public final PermissionTypes defaultPermission;

    public static final ImmutableSet<WarpPermissions> DEFAULT;
    public static final ImmutableMap<Character, WarpPermissions> CHARACTER_MAP = EditorPermissionUtil.createEnumMap(WarpPermissions.class, EditorPermissionUtil.VALUE_CALLBACK);
    public static final ImmutableMap<Integer, WarpPermissions> ID_MAP = EditorPermissionUtil.createEnumMap(WarpPermissions.class, EditorPermissionUtil.ID_CALLBACK);
    public static final ImmutableMap<String, WarpPermissions> STRING_MAP = EditorPermissionUtil.createEnumMap(WarpPermissions.class, EditorPermissionUtil.NAME_CALLBACK);

    private WarpPermissions(char value, int id, String name, PermissionTypes adminPermission, PermissionTypes defaultPermission) {
        this.value = value;
        this.id = id;
        this.name = name;
        this.adminPermission = adminPermission;
        this.defaultPermission = defaultPermission;
    }

    static {
        DEFAULT = ImmutableSet.of(UPDATE, RENAME, UNINVITE, INVITE, WARP);
    }

    public static WarpPermissions getById(int id) {
        return ID_MAP.get(id);
    }

    public static WarpPermissions getByChar(char ch) {
        return CHARACTER_MAP.get(ch);
    }

    public static Set<WarpPermissions> parseString(String permissions) {
        return EditorPermissionUtil.parseString(permissions, WarpPermissions.class, DEFAULT, CHARACTER_MAP);
    }

    public static void parseString(String permissions, Set<WarpPermissions> result) {
        EditorPermissionUtil.parseString(permissions, result, WarpPermissions.values(), DEFAULT, CHARACTER_MAP);
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
