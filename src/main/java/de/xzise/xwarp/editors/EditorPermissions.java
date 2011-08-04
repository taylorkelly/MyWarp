package de.xzise.xwarp.editors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableSet.Builder;

import de.xzise.Callback;
import de.xzise.MinecraftUtil;

public class EditorPermissions<T extends Enum<T> & Editor> {

    public enum Type {
        PLAYER(0, "player"), GROUP(1, "group"), PERMISSIONS(2, "permission");

        private static final Map<Integer, Type> TYPES = new HashMap<Integer, EditorPermissions.Type>();
        private static final Map<String, Type> NAMES = MinecraftUtil.createReverseEnumMap(Type.class, new Callback<String, Type>() {
            @Override
            public String call(Type parameter) {
                return parameter.name.toLowerCase();
            }
        });

        public static final Callback<Type, String> NAMES_CALLBACK = new Callback<Type, String>() {

            @Override
            public Type call(String parameter) {
                return parseName(parameter);
            }
        };

        static {
            for (Type type : Type.values()) {
                TYPES.put(type.id, type);
            }
        }

        public final int id;
        public final String name;

        private Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static Type parseInt(int id) {
            return TYPES.get(id);
        }

        public static Type parseName(String name) {
            return NAMES.get(name.toLowerCase());
        }
    }

    public enum Table {
        WARP(0), PROTECTION_AREA(1);

        private static final Map<Integer, Type> TYPES = new HashMap<Integer, EditorPermissions.Type>();

        static {
            for (Type type : Type.values()) {
                TYPES.put(type.id, type);
            }
        }

        public final int id;

        private Table(int id) {
            this.id = id;
        }

        public static Type parseInt(int id) {
            return TYPES.get(id);
        }
    }

    private final Map<T, Boolean> permissions;

    public EditorPermissions(Class<T> clazz) {
        this.permissions = Maps.newEnumMap(clazz);
    }

    public static <T extends Enum<T> & Editor> EditorPermissions<T> create(Class<T> clazz) {
        return new EditorPermissions<T>(clazz);
    }

    public String getPermissionString() {
        ImmutableSet<T> pms = this.getByValue(true);
        char[] editorPermissions = new char[pms.size()];
        int i = 0;
        for (T t : pms) {
            editorPermissions[i++] = t.getValue();
        }
        return new String(editorPermissions);
    }

    public ImmutableSet<T> getByValue(boolean value) {
        Builder<T> builder = ImmutableSet.builder();
        for (Map.Entry<T, Boolean> entry : this.permissions.entrySet()) {
            if ((entry.getValue() == null && !value) || (value == entry.getValue())) {
                builder.add(entry.getKey());
            }
        }
        return builder.build();
    }

    public boolean get(T permission) {
        Boolean bool = this.permissions.get(permission);
        return bool == null ? false : bool;
    }

    public boolean put(T key, boolean value) {
        Boolean bool = this.permissions.put(key, value);
        return bool == null ? false : bool;
    }

    public Boolean remove(T key) {
        return this.put(key, false);
    }

    public void putAll(EditorPermissions<T> p) {
        this.permissions.putAll(p.permissions);
    }

    public void putSet(Set<T> permissions, boolean reset) {
        if (reset) {
            this.permissions.clear();
        }
        for (T permission : permissions) {
            this.put(permission, true);
        }
    }
}
