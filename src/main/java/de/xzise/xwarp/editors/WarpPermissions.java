package de.xzise.xwarp.editors;

import java.util.HashSet;
import java.util.Set;

import de.xzise.ImmutableMap;
import de.xzise.xwarp.PermissionDouble;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public enum WarpPermissions implements PermissionDouble, Editor {
    UPDATE('l', 0, PermissionTypes.ADMIN_UPDATE, PermissionTypes.EDIT_LOCATION),
    RENAME('r', 1, PermissionTypes.ADMIN_RENAME, PermissionTypes.EDIT_RENAME),
    UNINVITE('u', 2, PermissionTypes.ADMIN_UNINVITE, PermissionTypes.EDIT_UNINVITE),
    INVITE('i', 3, PermissionTypes.ADMIN_INVITE, PermissionTypes.EDIT_INVITE),
    PRIVATE('0', 4, PermissionTypes.ADMIN_PRIVATE, PermissionTypes.CREATE_PRIVATE),
    PUBLIC('1', 5, PermissionTypes.ADMIN_PUBLIC, PermissionTypes.CREATE_PUBLIC),
    GLOBAL('2', 6, PermissionTypes.ADMIN_GLOBAL, PermissionTypes.CREATE_GLOBAL),
    GIVE('g', 7, PermissionTypes.ADMIN_CHANGE_OWNER, PermissionTypes.EDIT_CHANGE_OWNER),
    DELETE('d', 8, PermissionTypes.ADMIN_DELETE, PermissionTypes.EDIT_DELETE),
    WARP('w', 9, PermissionTypes.ADMIN_TO_ALL, null),
    ADD_EDITOR('a', 10, PermissionTypes.ADMIN_EDITORS_ADD, PermissionTypes.EDIT_EDITORS_ADD),
    REMOVE_EDITOR('f', 11, PermissionTypes.ADMIN_EDITORS_REMOVE, PermissionTypes.EDIT_EDITORS_REMOVE),
    MESSAGE('m', 12, PermissionTypes.ADMIN_MESSAGE, PermissionTypes.EDIT_MESSAGE),
    PRICE('p', 13, PermissionTypes.ADMIN_PRICE, PermissionTypes.EDIT_PRICE),
    FREE('c', 14, PermissionTypes.ADMIN_FREE, PermissionTypes.EDIT_FREE),
    LIST('v', 15, PermissionTypes.ADMIN_LIST_CHANGE, PermissionTypes.EDIT_LIST),

    ;

    public final char value;
    public final int id;
    public final PermissionTypes adminPermission;
    public final PermissionTypes defaultPermission;

    public static final Set<WarpPermissions> DEFAULT;
    public static final ImmutableMap<Character, WarpPermissions> CHARACTER_MAP = new ImmutableMap<Character, WarpPermissions>(WarpPermissions.class, EditorPermissionUtil.VALUE_CALLBACK);
    public static final ImmutableMap<Integer, WarpPermissions> ID_MAP = new ImmutableMap<Integer, WarpPermissions>(WarpPermissions.class, EditorPermissionUtil.ID_CALLBACK);

    private WarpPermissions(char value, int id, PermissionTypes adminPermission, PermissionTypes defaultPermission) {
        this.value = value;
        this.id = id;
        this.adminPermission = adminPermission;
        this.defaultPermission = defaultPermission;
    }

    static {
        DEFAULT = new HashSet<WarpPermissions>();
        DEFAULT.add(UPDATE);
        DEFAULT.add(RENAME);
        DEFAULT.add(UNINVITE);
        DEFAULT.add(INVITE);
        DEFAULT.add(WARP);
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
}
