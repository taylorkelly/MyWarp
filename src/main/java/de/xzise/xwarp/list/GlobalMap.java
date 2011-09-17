package de.xzise.xwarp.list;

import java.util.HashMap;
import java.util.Map;

import de.xzise.xwarp.WarpObject;

public class GlobalMap<T extends WarpObject<?>> {

    private final Map<String, T> all = new HashMap<String, T>();

    public void put(T warpObject) {
        this.all.put(warpObject.getOwner().toLowerCase(), warpObject);
    }

    public void delete(T warp) {
        this.all.remove(warp.getOwner().toLowerCase());
    }

    public T getWarpObject(String playerName) {
        if (this.all.size() == 1) {
            return this.all.values().iterator().next();
        } else if (playerName != null && !playerName.isEmpty()) {
            return this.all.get(playerName.toLowerCase());
        } else {
            return null;
        }
    }

    public void clear() {
        this.all.clear();
    }
}