package de.xzise.xwarp;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditorPermissions implements Map<Permissions, Boolean> {

    private final boolean[] values;

    public EditorPermissions() {
        this.values = new boolean[Permissions.values().length];
    }

    public String getPermissionString() {
        Permissions[] pms = this.getByValue(true);
        char[] editorPermissions = new char[pms.length];
        for (int j = 0; j < pms.length; j++) {
            editorPermissions[j] = pms[j].value;
        }
        return new String(editorPermissions);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof Permissions;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value instanceof Boolean) {
            boolean v = (Boolean) value;
            for (boolean b : this.values) {
                if (b == v) {
                    return true;
                }
            }
        }
        return false;
    }

    public Permissions[] getByValue(boolean value) {
        List<Permissions> result = new ArrayList<Permissions>();
        for (Entry<Permissions, Boolean> entry : this.entrySet()) {
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

    @Override
    public Boolean get(Object key) {
        if (key instanceof Permissions) {
            return get((Permissions) key);
        }
        return null;
    }

    public boolean get(Permissions permission) {
        return this.values[permission.id];
    }

    @Override
    public Boolean put(Permissions key, Boolean value) {
        boolean old = this.values[key.id];
        this.values[key.id] = value == null ? false : value;
        return old;
    }

    @Override
    public Boolean remove(Object key) {
        if (key instanceof Permissions) {
            return put((Permissions) key, false);
        } else {
            return null;
        }
    }

    @Override
    public void putAll(Map<? extends Permissions, ? extends Boolean> m) {
        for (Entry<? extends Permissions, ? extends Boolean> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        Arrays.fill(this.values, false);
    }
    
    @Override
    public Set<Permissions> keySet() {
        Set<Permissions> set = new HashSet<Permissions>();
        for (Permissions permissions : Permissions.values()) {
            set.add(permissions);
        }
        return set;
    }

    @Override
    public Collection<Boolean> values() {
        List<Boolean> list = new ArrayList<Boolean>(this.values.length);
        for (int i = 0; i < this.values.length; i++) {
            list.add(this.values[i]);
        }
        return list;
    }
    
    public static class AbstractEntry<Key, Value> implements Entry<Key, Value> {
        private Key k;
        private Value v;

        public AbstractEntry(Key k, Value v) {
            this.k = k;
            this.v = v;
        }

        @Override
        public Key getKey() {
            return this.k;
        }

        @Override
        public Value getValue() {
            return this.v;
        }

        @Override
        public Value setValue(Value value) {
            Value old = this.v;
            this.v = value;
            return old;
        }
    }

    @Override
    public Set<java.util.Map.Entry<Permissions, Boolean>> entrySet() {
        Set<java.util.Map.Entry<Permissions, Boolean>> set = new HashSet<Map.Entry<Permissions, Boolean>>();
        for (Permissions permissions : Permissions.values()) {
            set.add(new AbstractMap.SimpleImmutableEntry<Permissions, Boolean>(permissions, this.values[permissions.id]));
        }
        return set;
    }

    public void parseString(String string, boolean reset) {
        Set<Permissions> p = Permissions.parseString(string);
        if (reset) {
            Arrays.fill(this.values, false);
        }
        for (Permissions permissions : p) {
            this.values[permissions.id] = true;
        }
    }

}
