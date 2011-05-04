package de.xzise.xwarp;

import java.util.List;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.xwarp.warpable.Positionable;

public class WarpProtectionArea {

    private final FixedLocation firstEdge;
    private final FixedLocation secondEdge;
    private String owner;
    private List<String> allowed;
    
    public WarpProtectionArea(FixedLocation firstEdge, FixedLocation secondEdge, String owner) {
        if (this.firstEdge.world.equals(this.secondEdge.world)) {
            this.firstEdge = firstEdge;
            this.secondEdge = secondEdge;
            this.owner = owner;
        } else {
            throw new IllegalArgumentException("The edges have to be in the same world.");
        }
    }
    
    public boolean isWithIn(Positionable positionable) {
        return this.isWithIn(new FixedLocation(positionable.getLocation()));
    }
    
    public boolean isWithIn(FixedLocation location) {
        if (location.world.equals(firstEdge.world)) {
            double lowerX = Math.min(firstEdge.x, secondEdge.x);
            double upperX = Math.max(firstEdge.x, secondEdge.x);
            double lowerY = Math.min(firstEdge.y, secondEdge.y);
            double upperY = Math.max(firstEdge.y, secondEdge.y);
            double lowerZ = Math.min(firstEdge.z, secondEdge.z);
            double upperZ = Math.max(firstEdge.z, secondEdge.z);
            double x = location.x;
            double y = location.y;
            double z = location.z;
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
