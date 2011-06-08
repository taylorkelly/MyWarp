package de.xzise.metainterfaces;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationWrapper implements Moveable<LocationWrapper> {

    private FixedLocation location;
    private final String worldName;
    
    public LocationWrapper(Location location) {
        this(new FixedLocation(location));
    }
    
    public LocationWrapper(FixedLocation location) {
        this(location, location.world.getName());
    }
    
    public LocationWrapper(FixedLocation location, String world) {
        this.location = location;
        this.worldName = this.location.world != null ? location.world.getName() : world;
        if (worldName == null) {
            throw new IllegalArgumentException("Nullary world got.");
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
    
    public boolean setWorld(World world) {
        if (this.location.world == null && world.getName().equals(this.worldName)) {
            this.location = new FixedLocation(world, this.location.x, this.location.y, this.location.z, this.location.yaw, this.location.pitch);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean unsetWorld(World world) {
        if (this.location.world == world && world != null) {
            this.location = new FixedLocation(null, this.location.x, this.location.y, this.location.z, this.location.yaw, this.location.pitch);
            return true;
        } else {
            return false;
        }
    }
    
    public FixedLocation getLocation() {
        return this.location;
    }
    
    public String getWorld() {
        return this.worldName;
    }

    public boolean isValid() {
        return this.location.world != null;
    }

    @Override
    public LocationWrapper moveX(double delta) {
        return new LocationWrapper(this.location.moveX(delta), this.worldName);
    }

    @Override
    public LocationWrapper moveY(double delta) {
        return new LocationWrapper(this.location.moveY(delta), this.worldName);
    }

    @Override
    public LocationWrapper moveZ(double delta) {
        return new LocationWrapper(this.location.moveZ(delta), this.worldName);
    }

}
