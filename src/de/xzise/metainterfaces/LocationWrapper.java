package de.xzise.metainterfaces;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationWrapper {

    private final Location location;
    private final String world;
    
    public LocationWrapper(Location location) {
        this(location, location.getWorld().getName());
    }
    
    public LocationWrapper(Location location, String world) {
        this.location = location.clone();
        this.world = world == null ? location.getWorld().getName() : world;
    }
    
    public static LocationWrapper create(Location location, String world) {
        if (world != null) {
            return new LocationWrapper(location, world);
        } else {
            return new LocationWrapper(location);
        }
    }
    
    public void setWorld(World world) {
        if (this.location.getWorld() == null && world.getName().equals(this.world)) {
            this.location.setWorld(world);
        }
    }
    
    public Location getLocation() {
        return this.location.clone();
    }
    
    public String getWorld() {
        return this.world;
    }

    public boolean isValid() {
        return this.location.getWorld() != null;
    }

}
