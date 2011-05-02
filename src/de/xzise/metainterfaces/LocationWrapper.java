package de.xzise.metainterfaces;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationWrapper implements Moveable {

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
    
    public static Location moveX(Location location, double delta) {
        location.setX(location.getX() + delta);
        return location;
    }
    
    public static Location moveY(Location location, double delta) {
        location.setY(location.getY() + delta);
        return location;
    }
    
    public static Location moveZ(Location location, double delta) {
        location.setZ(location.getZ() + delta);
        return location;
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

    @Override
    public Moveable moveX(double delta) {
        moveX(this.location, delta);
        return this;
    }

    @Override
    public Moveable moveY(double delta) {
        moveY(this.location, delta);
        return this;
    }

    @Override
    public Moveable moveZ(double delta) {
        moveZ(this.location, delta);
        return this;
    }

}
