package de.xzise.xwarp.editors;

import java.util.HashMap;
import java.util.Map;

import de.xzise.xwarp.dataconnections.YmlConnection.Callback;

public class ReverseEnumMap<K, V extends Enum<V>> {
    
    private final Map<K, V> map;
    
    public ReverseEnumMap(Class<V> enumClass, Callback<K, ? super V> keys) {
        this.map = new HashMap<K, V>();
        
        for (V enumValue : enumClass.getEnumConstants()) {
            this.map.put(keys.call(enumValue), enumValue);
        }
    }
    
    public V get(K key) {
        return this.map.get(key);
    }

}
