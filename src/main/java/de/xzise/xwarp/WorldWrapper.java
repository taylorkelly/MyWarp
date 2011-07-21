package de.xzise.xwarp;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldWrapper {

    private World world;
    private final String worldName;
    
    public WorldWrapper(String world) {
        this.worldName = world;
        this.world = Bukkit.getServer().getWorld(world);
    }
    
    public WorldWrapper(World world) {
        this.worldName = world.getName();
        this.world = world;
    }

    public boolean setWorld(World world) {
        if (this.world == null && world.getName().equals(this.worldName)) {
            this.world = world;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean unsetWorld(World world) {
        if (this.world == world && world != null) {
            this.world = null;
            return true;
        } else {
            return false;
        }
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public String getWorldName() {
        return this.worldName;
    }

    public boolean isValid() {
        return this.world != null;
    }
}
