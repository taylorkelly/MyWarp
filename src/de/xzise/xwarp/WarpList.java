package de.xzise.xwarp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;

import me.taylorkelly.mywarp.Warp.Visibility;

public class WarpList<T extends WarpObject> {

    private class GlobalMap<globalMapObject extends WarpObject> {

        private globalMapObject global;
        private final Map<String, globalMapObject> all;

        public GlobalMap() {
            this.global = null;
            this.all = new HashMap<String, globalMapObject>();
        }

        public void put(globalMapObject warpObject) {
            if (warpObject.getVisibility() == Visibility.GLOBAL) {
                this.global = warpObject;
            }
            this.all.put(warpObject.getCreator().toLowerCase(), warpObject);
        }

        public void delete(globalMapObject warp) {
            if (warp.equals(this.global)) {
                this.global = null;
            }
            this.all.remove(warp.getCreator().toLowerCase());
        }

        public globalMapObject getWarpObject(String playerName) {
            if (this.global != null) {
                return this.global;
            }

            if (this.all.size() == 1) {
                return this.all.values().iterator().next();
            } else if (playerName != null && !playerName.isEmpty()) {
                return this.all.get(playerName.toLowerCase());
            } else {
                return null;
            }
        }

        public void updateGlobal(globalMapObject warpObject) {
            if (this.global == null && warpObject.getVisibility() == Visibility.GLOBAL) {
                this.global = warpObject;
            } else if (warpObject.equals(this.global) && warpObject.getVisibility() != Visibility.GLOBAL) {
                this.global = null;
            }
        }

        public void clear() {
            this.all.clear();
            this.global = null;
        }
    }

    // Warps sorted by owner, name
    private final Map<String, Map<String, T>> personal;
    // Warps sorted by creator
    private final Map<String, List<T>> creatorMap;
    // Warps sorted by name
    private final Map<String, GlobalMap<T>> global;

    public WarpList() {
        this.personal = new HashMap<String, Map<String, T>>();
        this.global = new HashMap<String, GlobalMap<T>>();
        this.creatorMap = new HashMap<String, List<T>>();
    }

    public void loadList(Collection<T> warpObjects) {
        for (Map<?, ?> personalWarps : this.personal.values()) {
            personalWarps.clear();
        }
        for (List<?> creatorWarps : this.creatorMap.values()) {
            creatorWarps.clear();
        }
        for (GlobalMap<?> globalWarps : this.global.values()) {
            globalWarps.clear();
        }

        // Load elements here
        for (T warpObject : warpObjects) {
            this.addWarp(warpObject);
        }
    }

    public void addWarp(T warpObject) {
        GlobalMap<T> namedWarps = this.global.get(warpObject.getName().toLowerCase());
        if (namedWarps == null) {
            namedWarps = new GlobalMap<T>();
            this.global.put(warpObject.getName().toLowerCase(), namedWarps);
        }
        namedWarps.put(warpObject);

        Map<String, T> personalWarps = this.personal.get(warpObject.getOwner().toLowerCase());
        if (personalWarps == null) {
            personalWarps = new HashMap<String, T>();
            this.personal.put(warpObject.getOwner().toLowerCase(), personalWarps);
        }
        personalWarps.put(warpObject.getName().toLowerCase(), warpObject);

        if (MinecraftUtil.isSet(warpObject.getCreator())) {
            List<T> creatorWarps = this.creatorMap.get(warpObject.getCreator().toLowerCase());
            if (creatorWarps == null) {
                creatorWarps = new ArrayList<T>();
                this.creatorMap.put(warpObject.getCreator().toLowerCase(), creatorWarps);
            }
            creatorWarps.add(warpObject);
        }
    }

    public void deleteWarp(T warp) {
        this.global.get(warp.getName().toLowerCase()).delete(warp);
        if (MinecraftUtil.isSet(warp.getCreator())) {
            this.creatorMap.get(warp.getCreator().toLowerCase()).remove(warp);
        }
        this.personal.get(warp.getOwner().toLowerCase()).remove(warp.getName().toLowerCase());
    }

    public void updateOwner(T warp, String preOwner) {
        this.personal.get(preOwner.toLowerCase()).remove(warp.getName().toLowerCase());
        Map<String, T> personalWarps = this.personal.get(warp.getOwner().toLowerCase());
        if (personalWarps == null) {
            personalWarps = new HashMap<String, T>();
            this.personal.put(warp.getOwner().toLowerCase(), personalWarps);
        }
        personalWarps.put(warp.getName().toLowerCase(), warp);
    }

    public void updateVisibility(T warp) {
        this.global.get(warp.getName().toLowerCase()).updateGlobal(warp);
    }

    /**
     * Returns the number of warps a player has created.
     * 
     * @param creator
     *            The creator of the warps. Has to be not null.
     * @param visibility
     *            The visibility of the warps. Set to null if want to show all
     *            visibilities.
     * @param world
     *            The world the warps has to be in. If null, it checks all
     *            worlds.
     * @return The number of warps the player has created (with the desired
     *         visibility).
     */
    public int getNumberOfWarps(String creator, Visibility visibility, String world) {
        int number = 0;
        if (MinecraftUtil.isSet(creator)) {
            List<T> warpObjects = this.creatorMap.get(creator.toLowerCase());
            if (warpObjects != null) {
                for (T warpObject : warpObjects) {
                    if ((visibility == null || warpObject.getVisibility() == visibility) && (world == null || warpObject.getWorld().equals(world))) {
                        number++;
                    }
                }
            }
        }
        return number;
    }

    public T getWarpObject(String name, String owner, String playerName) {
        if (owner == null || owner.isEmpty()) {
            GlobalMap<T> namedWarps = this.global.get(name.toLowerCase());
            if (namedWarps != null) {
                return namedWarps.getWarpObject(playerName);
            } else {
                return null;
            }
        } else {
            Map<String, T> ownerWarps = this.personal.get(owner.toLowerCase());
            if (ownerWarps != null) {
                return ownerWarps.get(name.toLowerCase());
            }
            return null;
        }
    }

    public T getWarpObject(String name) {
        return this.getWarpObject(name, null, null);
    }

    public List<T> getWarps() {
        List<T> result = new ArrayList<T>();
        for (Map<String, T> map : this.personal.values()) {
            result.addAll(map.values());
        }
        return result;
    }

    public List<T> getWarps(String owner) {
        Map<String, T> personalWarps = this.personal.get(owner.toLowerCase());
        if (personalWarps != null) {
            return new ArrayList<T>(personalWarps.values());
        } else {
            return new ArrayList<T>(0);
        }
    }

    /**
     * Returns the number of warps the player can modify/use.
     * 
     * @param player
     *            The given player.
     * @return The number of warps the player can modify/use.
     */
    public int getSize(CommandSender sender) {
        int size = 0;
        for (Map<String, T> map : this.personal.values()) {
            size += this.getSize(sender, map);
        }
        return size;
    }

    public int getSize(CommandSender sender, String creator) {
        if (creator == null || creator.isEmpty()) {
            return this.getSize(sender);
        } else {
            Map<String, T> map = this.personal.get(creator.toLowerCase());
            return map == null ? 0 : this.getSize(sender, map);
        }
    }

    private int getSize(CommandSender sender, Map<String, T> map) {
        if (sender == null) {
            return map.size();
        } else {
            int size = 0;
            for (T warp : map.values()) {
                if (warp.listWarp(sender)) {
                    size++;
                }
            }
            return size;
        }
    }

}
