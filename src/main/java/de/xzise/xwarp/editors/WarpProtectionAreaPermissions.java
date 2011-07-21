package de.xzise.xwarp.editors;

import java.util.HashSet;
import java.util.Set;

import de.xzise.Callback;
import de.xzise.ImmutableMap;
import de.xzise.xwarp.PermissionDouble;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public enum WarpProtectionAreaPermissions implements PermissionDouble, Editor {
    UPDATE('l', 0, PermissionTypes.ADMIN_UPDATE, PermissionTypes.EDIT_LOCATION),
    RENAME('m', 1, PermissionTypes.ADMIN_RENAME, PermissionTypes.EDIT_RENAME),
    UNINVITE('u', 2, PermissionTypes.ADMIN_UNINVITE, PermissionTypes.EDIT_UNINVITE),
    INVITE('i', 3, PermissionTypes.ADMIN_INVITE, PermissionTypes.EDIT_INVITE),
    GIVE('g', 7, PermissionTypes.ADMIN_CHANGE_OWNER, PermissionTypes.EDIT_CHANGE_OWNER),
    DELETE('d', 8, PermissionTypes.ADMIN_DELETE, PermissionTypes.EDIT_DELETE),
    OVERWRITE('o', 9, PermissionTypes.ADMIN_TO_ALL, null),
    ADD_EDITOR('a', 10, PermissionTypes.ADMIN_EDITORS_ADD, PermissionTypes.EDIT_EDITORS_ADD),
    REMOVE_EDITOR('r', 11, PermissionTypes.ADMIN_EDITORS_REMOVE, PermissionTypes.EDIT_EDITORS_REMOVE),
    LIST('v', 15, PermissionTypes.ADMIN_LIST_CHANGE, PermissionTypes.EDIT_LIST),

    ;
    
    public final char value;
    public final int id;
    public final PermissionTypes adminPermission;
    public final PermissionTypes defaultPermission;
    
    @Deprecated
    public final static Callback<WarpProtectionAreaPermissions, Character> PERM_TO_CHAR_CALLBACK = new Callback<WarpProtectionAreaPermissions, Character>() {

        @Override
        public WarpProtectionAreaPermissions call(Character parameter) {
            return CHARACTER_MAP.get(parameter);
        }
    };

    @Deprecated
    public final static Callback<WarpProtectionAreaPermissions, Integer> PERM_TO_ID_CALLBACK = new Callback<WarpProtectionAreaPermissions, Integer>() {

        @Override
        public WarpProtectionAreaPermissions call(Integer parameter) {
            return ID_MAP.get(parameter);
        }
    };
    
    public static final Set<WarpProtectionAreaPermissions> DEFAULT;
    public static final ImmutableMap<Character, WarpProtectionAreaPermissions> CHARACTER_MAP = new ImmutableMap<Character, WarpProtectionAreaPermissions>(WarpProtectionAreaPermissions.class, EditorPermissionUtil.VALUE_CALLBACK);
    public static final ImmutableMap<Integer, WarpProtectionAreaPermissions> ID_MAP = new ImmutableMap<Integer, WarpProtectionAreaPermissions>(WarpProtectionAreaPermissions.class, EditorPermissionUtil.ID_CALLBACK);

    private WarpProtectionAreaPermissions(char value, int id, PermissionTypes adminPermission, PermissionTypes defaultPermission) {
        this.value = value;
        this.id = id;
        this.adminPermission = adminPermission;
        this.defaultPermission = defaultPermission;
    }

    static {
        DEFAULT = new HashSet<WarpProtectionAreaPermissions>();
        DEFAULT.add(UPDATE);
        DEFAULT.add(RENAME);
        DEFAULT.add(UNINVITE);
        DEFAULT.add(INVITE);
        DEFAULT.add(OVERWRITE);
    }

    public static WarpProtectionAreaPermissions getById(int id) {
        return ID_MAP.get(id);
    }

    public static WarpProtectionAreaPermissions getByChar(char ch) {
        return CHARACTER_MAP.get(ch);
    }

    public static Set<WarpProtectionAreaPermissions> parseString(String permissions) {
        return EditorPermissionUtil.parseString(permissions, WarpProtectionAreaPermissions.class, DEFAULT, CHARACTER_MAP);
    }

    public static void parseString(String permissions, Set<WarpProtectionAreaPermissions> result) {
        EditorPermissionUtil.parseString(permissions, result, WarpProtectionAreaPermissions.values(), DEFAULT, CHARACTER_MAP);
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
