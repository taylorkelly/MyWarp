package de.xzise.xwarp.editors;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import de.xzise.Callback;
import de.xzise.ImmutableMap;

public class EditorPermissionUtil {

    private EditorPermissionUtil() {}
    
    public final static Callback<Character, Editor> VALUE_CALLBACK = new Callback<Character, Editor>() {

        @Override
        public Character call(Editor parameter) {
            return parameter.getValue();
        }
    };
    
    public final static Callback<Integer, Editor> ID_CALLBACK = new Callback<Integer, Editor>() {

        @Override
        public Integer call(Editor parameter) {
            return parameter.getId();
        }
    };
    
    public static <T extends Enum<T> & Editor> Set<T> parseString(String permissions, Class<T> clazz, Set<T> def, ImmutableMap<Character, T> charMap) {
        Set<T> result = EnumSet.noneOf(clazz);
        parseString(permissions, result, clazz.getEnumConstants(), def, charMap);
        return result;
    }

    public static <T extends Enum<T> & Editor> void parseString(String permissions, Set<T> result, T[] all, Set<T> def, ImmutableMap<Character, T> charMap) {
        result.clear();
        boolean remove = false;

        for (char c : permissions.toLowerCase().toCharArray()) {
            if (c == '/') {
                remove = true;
            } else if (c == '*') {
                if (remove) {
                    result.removeAll(Arrays.asList(all));
                } else {
                    result.addAll(Arrays.asList(all));
                }
            } else if (c == 's') {
                if (remove) {
                    result.removeAll(def);
                } else {
                    result.addAll(def);
                }
            } else {
                T e = charMap.get(c);
                if (e != null) {
                    if (remove) {
                        result.remove(e);
                    } else {
                        result.add(e);
                    }
                } else {
                    // TODO: How to react?
                }
            }
        }
    }
    
}
