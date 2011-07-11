package de.xzise.xwarp;

import java.util.List;

import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.command.CommandSender;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.warpable.Positionable;

public class WarpProtectionArea implements WarpObject {

    private final LocationWrapper firstCorner;
    private final LocationWrapper secondCorner;
    private final String name;
    private String owner;
    private String creator;
    private List<String> allowed;
    
    public WarpProtectionArea(FixedLocation firstCorner, FixedLocation secondCorner, String name, String owner) {
        if (firstCorner.world.equals(secondCorner.world)) {
            this.firstCorner = new LocationWrapper(firstCorner);
            this.secondCorner = new LocationWrapper(secondCorner);
            this.owner = owner;
            this.name = name;
        } else {
            throw new IllegalArgumentException("The corners have to be in the same world.");
        }
    }
    
    public boolean isWithIn(Positionable positionable) {
        return this.isWithIn(new FixedLocation(positionable.getLocation()));
    }
    
    public boolean isWithIn(FixedLocation location) {
        if (location.world.getName().equals(firstCorner.getWorld())) {
            FixedLocation firstCornerBuf = this.firstCorner.getLocation();
            FixedLocation secondCornerBuf = this.secondCorner.getLocation();
            double lowerX = Math.min(firstCornerBuf.x, secondCornerBuf.x);
            double upperX = Math.max(firstCornerBuf.x, secondCornerBuf.x);
            double lowerY = Math.min(firstCornerBuf.y, secondCornerBuf.y);
            double upperY = Math.max(firstCornerBuf.y, secondCornerBuf.y);
            double lowerZ = Math.min(firstCornerBuf.z, secondCornerBuf.z);
            double upperZ = Math.max(firstCornerBuf.z, secondCornerBuf.z);
            double x = location.x;
            double y = location.y;
            double z = location.z;
            return lowerX <= x && x <= upperX && lowerY <= y && y <= upperY && lowerZ <= z && z <= upperZ;
        } else {
            return false;
        }
    }
    
    public static boolean isWithIn(final FixedLocation firstCorner, final FixedLocation secondCorner, final FixedLocation testLocation) {
        if (testLocation.world.equals(firstCorner.world) && firstCorner.world.equals(secondCorner.world)) {
            double lowerX = Math.min(firstCorner.x, secondCorner.x);
            double upperX = Math.max(firstCorner.x, secondCorner.x);
            double lowerY = Math.min(firstCorner.y, secondCorner.y);
            double upperY = Math.max(firstCorner.y, secondCorner.y);
            double lowerZ = Math.min(firstCorner.z, secondCorner.z);
            double upperZ = Math.max(firstCorner.z, secondCorner.z);
            double x = testLocation.x;
            double y = testLocation.y;
            double z = testLocation.z;
            return lowerX <= x && x <= upperX && lowerY <= y && y <= upperY && lowerZ <= z && z <= upperZ;
        } else {
            return false;
        } 
    }
    
    public boolean isAllowed(String name) {
    //TODO: Allow positionable & differ between the visibility?
        if (name.equals(this.owner)) {
            return true;
        } else {
            return this.allowed.contains(name);
        }
    }
    
    public String getName() {
        return this.name;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public String getCreator() {
        return this.creator;
    }

    @Override
    public String getWorld() {
        return null;
    }

    @Override
    public Visibility getVisibility() {
        return null;
    }

    @Override
    public boolean listWarp(CommandSender sender) {
        // TODO Auto-generated method stub
        return false;
    }
}
