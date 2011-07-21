package de.xzise.xwarp.editors;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap.Builder;

import de.xzise.Callback;

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
    
    public final static Callback<String, Editor> NAME_CALLBACK = new Callback<String, Editor>() {

        @Override
        public String call(Editor parameter) {
            return parameter.getName();
        }
    };
    
    public static <K, V extends Enum<?>> com.google.common.collect.ImmutableMap<K, V> createEnumMap(Class<V> enumClass, Callback<K, ? super V> keys) {
        Builder<K, V> builder = com.google.common.collect.ImmutableMap.builder();
        for (V enumValue : enumClass.getEnumConstants()) {
            builder.put(keys.call(enumValue), enumValue);
        }
        return builder.build();
    }
    
    public static <T extends Enum<T> & Editor> Set<T> parseString(String permissions, Class<T> clazz, Set<T> def, Map<Character, T> charMap) {
        Set<T> result = EnumSet.noneOf(clazz);
        parseString(permissions, result, clazz.getEnumConstants(), def, charMap);
        return result;
    }
    
    public static <T extends Editor> void parseString(String permissions, Set<T> result, T[] all, Set<T> def, Map<Character, T> charMap) {
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
