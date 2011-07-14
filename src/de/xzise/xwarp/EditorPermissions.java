package de.xzise.xwarp;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditorPermissions {
    
    public enum Type {
        PLAYER(0),
        GROUP(1);
        
        private static final Map<Integer, Type> TYPES = new HashMap<Integer, EditorPermissions.Type>();
        
        static {
            for (Type type : Type.values()) {
                TYPES.put(type.id, type);
            }
        }
        
        public final int id;
        
        private Type(int id) {
            this.id = id;
        }
        
        public static Type parseInt(int id) {
            return TYPES.get(id);
        }
    }

    private final Map<Permissions, Boolean> permissions = new EnumMap<Permissions, Boolean>(Permissions.class);

    public String getPermissionString() {
        Permissions[] pms = this.getByValue(true);
        char[] editorPermissions = new char[pms.length];
        for (int j = 0; j < pms.length; j++) {
            editorPermissions[j] = pms[j].value;
        }
        return new String(editorPermissions);
    }

    public Permissions[] getByValue(boolean value) {
        List<Permissions> result = new ArrayList<Permissions>();
        for (Map.Entry<Permissions, Boolean> entry : this.permissions.entrySet()) {
            if (entry.getValue() == null) {
                if (!value) {
                    result.add(entry.getKey());
                }
            } else if (value == entry.getValue()) {
                result.add(entry.getKey());
            }
        }
        return result.toArray(new Permissions[0]);
    }

    public boolean get(Permissions permission) {
        Boolean bool = this.permissions.get(permission);
        return bool == null ? false : bool;
    }

    public boolean put(Permissions key, boolean value) {
        Boolean bool = this.permissions.put(key, value);
        return bool == null ? false : bool;
    }

    public Boolean remove(Permissions key) {
        return this.put((Permissions) key, false);
    }

    public void putAll(EditorPermissions p) {
        this.permissions.putAll(p.permissions);
    }

    public void parseString(String string, boolean reset) {
        Set<Permissions> p = Permissions.parseString(string);
        if (reset) {
            this.permissions.clear();
        }
        for (Permissions permissions : p) {
            this.put(permissions, true);
        }
    }

}
