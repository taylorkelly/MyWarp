package de.xzise.metainterfaces;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * A class which acts like a normal location, but is immutable.
 * @author Fabian Neundorf
 */
public class FixedLocation implements Moveable<FixedLocation> {
    
    public final World world;
    public final double x;
    public final double y;
    public final double z;
    public final float pitch;
    public final float yaw;

    public FixedLocation(Location location) {
        this(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
    public FixedLocation(final World world, final double x, final double y, final double z, final float yaw, final float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public FixedLocation(FixedLocation location, double xDelta, double yDelta, double zDelta) {
        this(location.world, location.x + xDelta, location.y + yDelta, location.z + zDelta, location.yaw, location.pitch);
    }
    
    public Location toLocation() {
        return new Location(this.world, this.x, this.y, this.z, this.yaw, this.pitch);
    }
    
    /**
     * Gets the block at the represented location
     *
     * @return Block at the represented location
     */
    public Block getBlock() {
        return world.getBlockAt(this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }
    
    /**
     * Gets the floored value of the X component, indicating the block that
     * this location is contained with.
     *
     * @return block X
     */
    public int getBlockX() {
        return Location.locToBlock(x);
    }
    
    /**
     * Gets the floored value of the Y component, indicating the block that
     * this location is contained with.
     *
     * @return block y
     */
    public int getBlockY() {
        return Location.locToBlock(y);
    }
    
    /**
     * Gets the floored value of the Z component, indicating the block that
     * this location is contained with.
     *
     * @return block z
     */
    public int getBlockZ() {
        return Location.locToBlock(z);
    }
    
    /**
     * Gets a Vector pointing in the direction that this Location is facing
     *
     * @return Vector
     */
    public Vector getDirection() {
        return getDirection(this.yaw, this.pitch);
    }
    
    /**
     * Constructs a new {@link Vector} based on this Location
     *
     * @return New Vector containing the coordinates represented by this Location
     */
    public Vector toVector() {
        return new Vector(x, y, z);
    }
    
    /**
     * Gets a Vector pointing in the direction that this Location is facing
     *
     * @param yaw The rotation at the x-axis in degrees.
     * @param pitch The rotation at the z-axis in degrees.
     *
     * @return Vector
     */
    public static Vector getDirection(double yaw, double pitch) {
        Vector vector = new Vector();

        double rotX = Math.toRadians(yaw);
        double rotY = Math.toRadians(pitch);

        vector.setY(-Math.sin(rotY));

        double h = Math.cos(rotY);
        vector.setX(-h*Math.sin(rotX));
        vector.setZ(h*Math.cos(rotX));

        return vector;
    }
    
    public FixedLocation move(double xDelta, double yDelta, double zDelta) {
        return new FixedLocation(this, xDelta, yDelta, zDelta);
    }
    
    @Override
    public FixedLocation moveX(double delta) {
        return this.move(delta, 0, 0);
    }

    @Override
    public FixedLocation moveY(double delta) {
        return this.move(0, delta, 0);
    }

    @Override
    public FixedLocation moveZ(double delta) {
        return this.move(0, 0, delta);
    }
    
}
