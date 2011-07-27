package de.xzise.xwarp.list;

import java.util.HashMap;
import java.util.Map;

import de.xzise.xwarp.WarpObject;

public class GlobalMap<globalMapObject extends WarpObject<?>> {

    private final Map<String, globalMapObject> all = new HashMap<String, globalMapObject>();

    public void put(globalMapObject warpObject) {
        this.all.put(warpObject.getCreator().toLowerCase(), warpObject);
    }

    public void delete(globalMapObject warp) {
        this.all.remove(warp.getCreator().toLowerCase());
    }

    public globalMapObject getWarpObject(String playerName) {
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