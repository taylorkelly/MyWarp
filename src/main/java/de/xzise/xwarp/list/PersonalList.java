package de.xzise.xwarp.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.WarpObject;

public abstract class PersonalList<T extends WarpObject<?>, G extends GlobalMap<T>> {
    
    // WarpObjects sorted by owner, name
    private final Map<String, Map<String, T>> personalMap = new HashMap<String, Map<String,T>>();
    // Warps sorted by creator
    private final Map<String, List<T>> creatorMap = new HashMap<String, List<T>>();
    // Warps sorted by name
    private final Map<String, G> nameMap = new HashMap<String, G>();
    
    private boolean ignoreCase;
    
    public void loadList(Collection<T> warpObjects) {
        for (Map<?, ?> personalWarps : this.personalMap.values()) {
            personalWarps.clear();
        }
        for (List<?> creatorWarps : this.creatorMap.values()) {
            creatorWarps.clear();
        }
        for (GlobalMap<?> globalWarps : this.nameMap.values()) {
            globalWarps.clear();
        }

        // Load elements here
        for (T warpObject : warpObjects) {
            this.addWarpObject(warpObject);
        }
    }
    
    protected abstract G createGlobalMap();
    
    public void addWarpObject(T warpObject) {
        Map<String, T> personalWarps = this.personalMap.get(warpObject.getOwner().toLowerCase());
        if (personalWarps == null) {
            personalWarps = new HashMap<String, T>();
            this.personalMap.put(warpObject.getOwner().toLowerCase(), personalWarps);
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
        
        G namedWarps = this.nameMap.get(warpObject.getName().toLowerCase());
        if (namedWarps == null) {
            namedWarps = this.createGlobalMap();
            this.nameMap.put(warpObject.getName().toLowerCase(), namedWarps);
        }
        namedWarps.put(warpObject);
    }
    
    public void deleteWarpObject(T warp) {
        if (MinecraftUtil.isSet(warp.getCreator())) {
            this.creatorMap.get(warp.getCreator().toLowerCase()).remove(warp);
        }
        this.personalMap.get(warp.getOwner().toLowerCase()).remove(warp.getName().toLowerCase());
        this.nameMap.get(warp.getName().toLowerCase()).delete(warp);
    }
    
    public void updateOwner(T warp, String preOwner) {
        this.personalMap.get(preOwner.toLowerCase()).remove(warp.getName().toLowerCase());
        Map<String, T> personalWarps = this.personalMap.get(warp.getOwner().toLowerCase());
        if (personalWarps == null) {
            personalWarps = new HashMap<String, T>();
            this.personalMap.put(warp.getOwner().toLowerCase(), personalWarps);
        }
        personalWarps.put(warp.getName().toLowerCase(), warp);
    }

    /**
     * Returns the number of warps a player has created.
     * 
     * @param creator
     *            The creator of the warps. Has to be not null.
     * @param world
     *            The world the warps has to be in. If null, it checks all
     *            worlds.
     * @return The number of warps the player has created (with the desired
     *         visibility).
     */
    public int getNumberOfWarps(String creator, String world) {
        int number = 0;
        if (MinecraftUtil.isSet(creator)) {
            List<T> warpObjects = this.creatorMap.get(creator.toLowerCase());
            if (warpObjects != null) {
                for (T warpObject : warpObjects) {
                    if (world == null || warpObject.getWorld().equals(world)) {
                        number++;
                    }
                }
            }
        }
        return number;
    }

    public T getWarpObject(String name, String owner, String playerName) {
        T warpObject = null;
        if (MinecraftUtil.isSet(owner)) {
            Map<String, T> ownerWarps = this.personalMap.get(owner.toLowerCase());
            if (ownerWarps != null) {
                warpObject = ownerWarps.get(name.toLowerCase());
            }
        } else {
            GlobalMap<T> namedWarps = this.nameMap.get(name.toLowerCase());
            if (namedWarps != null) {
                warpObject = namedWarps.getWarpObject(playerName);
            }
        }
        if (warpObject != null && (this.ignoreCase || (warpObject.getName().equals(name) && (!MinecraftUtil.isSet(owner) || warpObject.getOwner().equals(owner))))) {
            return warpObject;
        } else {
            return null;
        }
    }

    public T getWarpObject(String name) {
        return this.getWarpObject(name, null, null);
    }

    public List<T> getWarpObjects() {
        List<T> result = new ArrayList<T>();
        for (Map<String, T> map : this.personalMap.values()) {
            result.addAll(map.values());
        }
        return result;
    }

    public List<T> getWarps(String owner) {
        Map<String, T> personalWarps = this.personalMap.get(owner.toLowerCase());
        if (personalWarps != null) {
            return new ArrayList<T>(personalWarps.values());
        } else {
            return new ArrayList<T>(0);
        }
    }

    /**
     * Returns the number of warps the player can modify/use.
     * 
     * @param sender
     *            The given player.
     * @return The number of warps the player can modify/use.
     */
    public int getSize(CommandSender sender) {
        int size = 0;
        for (Map<String, T> map : this.personalMap.values()) {
            size += this.getSize(sender, map);
        }
        return size;
    }

    public int getSize(CommandSender sender, String creator) {
        if (creator == null || creator.isEmpty()) {
            return this.getSize(sender);
        } else {
            Map<String, T> map = this.personalMap.get(creator.toLowerCase());
            return map == null ? 0 : this.getSize(sender, map);
        }
    }

    private int getSize(CommandSender sender, Map<String, T> map) {
        if (sender == null) {
            return map.size();
        } else {
            int size = 0;
            for (T warp : map.values()) {
                if (warp.list(sender)) {
                    size++;
                }
            }
            return size;
        }
    }
    
    protected List<T> getByCreator(String name) {
        return this.creatorMap.get(name.toLowerCase());
    }
    
    protected G getByName(String name) {
        return this.nameMap.get(name.toLowerCase());
    }

    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

}
