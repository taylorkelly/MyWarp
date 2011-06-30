package de.xzise.xwarp;

import java.util.List;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.xwarp.warpable.Positionable;

public class WarpProtectionArea {

    private final FixedLocation firstCorner;
    private final FixedLocation secondCorner;
    private final String name;
    private String owner;
    private List<String> allowed;
    
    public WarpProtectionArea(FixedLocation firstEdge, FixedLocation secondEdge, String name, String owner) {
        if (this.firstCorner.world.equals(this.secondCorner.world)) {
            this.firstCorner = firstEdge;
            this.secondCorner = secondEdge;
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
        if (location.world.equals(firstCorner.world)) {
            double lowerX = Math.min(firstCorner.x, secondCorner.x);
            double upperX = Math.max(firstCorner.x, secondCorner.x);
            double lowerY = Math.min(firstCorner.y, secondCorner.y);
            double upperY = Math.max(firstCorner.y, secondCorner.y);
            double lowerZ = Math.min(firstCorner.z, secondCorner.z);
            double upperZ = Math.max(firstCorner.z, secondCorner.z);
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
    
}
