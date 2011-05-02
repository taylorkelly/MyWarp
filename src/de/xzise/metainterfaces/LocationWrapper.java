package de.xzise.metainterfaces;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationWrapper implements Moveable<LocationWrapper> {

    private final FixedLocation location;
    private final String worldName;
    private World world;
    
    public LocationWrapper(Location location) {
        this(new FixedLocation(location));
    }
    
    public LocationWrapper(FixedLocation location) {
        this(location, location.world.getName());
    }
    
    public LocationWrapper(FixedLocation location, String world) {
        this.location = location;
        this.worldName = world == null ? location.world.getName() : world;
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
        if (this.world == null && this.location.world == null && world.getName().equals(this.worldName)) {
            this.world = world;
        }
    }
    
    public FixedLocation getLocation() {
        return this.location;
    }
    
    public String getWorld() {
        return this.worldName;
    }

    public boolean isValid() {
        return this.world != null;
    }

    @Override
    public LocationWrapper moveX(double delta) {
        return new LocationWrapper(this.location.moveX(delta));
    }

    @Override
    public LocationWrapper moveY(double delta) {
        return new LocationWrapper(this.location.moveY(delta));
    }

    @Override
    public LocationWrapper moveZ(double delta) {
        return new LocationWrapper(this.location.moveZ(delta));
    }

}
