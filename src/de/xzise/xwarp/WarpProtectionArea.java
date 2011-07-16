package de.xzise.xwarp;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.command.CommandSender;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.WarpPermissions;
import de.xzise.xwarp.editors.WarpProtectionAreaPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.warpable.Positionable;

public class WarpProtectionArea implements WarpObject {

    public final int index;
    private final WorldWrapper world;
    private final FixedLocation firstCorner;
    private final FixedLocation secondCorner;
    private final String name;
    private String owner;
    private String creator;
    private Map<EditorPermissions.Type, Map<String, EditorPermissions<WarpProtectionAreaPermissions>>> editors;
    
    public static int nextIndex = 1;
    
    public WarpProtectionArea(WorldWrapper world, FixedLocation firstCorner, FixedLocation secondCorner, String name, String owner) {
        this(nextIndex, world, firstCorner, secondCorner, name, owner);
    }
    
    public WarpProtectionArea(int index, WorldWrapper world, FixedLocation firstCorner, FixedLocation secondCorner, String name, String owner) {
        this.index = index;
        this.world = world;
        this.firstCorner = firstCorner;
        this.secondCorner = secondCorner;
        this.owner = owner;
        this.name = name;
        this.editors = new EnumMap<EditorPermissions.Type, Map<String, EditorPermissions<WarpProtectionAreaPermissions>>>(EditorPermissions.Type.class);
        if (index > nextIndex)
            nextIndex = index;
        nextIndex++;
    }
    
    public boolean isWithIn(Positionable positionable) {
        return this.isWithIn(new FixedLocation(positionable.getLocation()));
    }
    
    public boolean isWithIn(FixedLocation location) {
        if (this.isValid() && location.world.equals(world.getWorld())) {
            double lowerX = Math.min(this.firstCorner.x, this.secondCorner.x);
            double upperX = Math.max(this.firstCorner.x, this.secondCorner.x);
            double lowerY = Math.min(this.firstCorner.y, this.secondCorner.y);
            double upperY = Math.max(this.firstCorner.y, this.secondCorner.y);
            double lowerZ = Math.min(this.firstCorner.z, this.secondCorner.z);
            double upperZ = Math.max(this.firstCorner.z, this.secondCorner.z);
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
        //TODO: Allow positionable
        if (name.equals(this.owner)) {
            return true;
        } else {
            EditorPermissions<WarpProtectionAreaPermissions> ep = this.editors.get(Type.PLAYER).get(name.toLowerCase());
            
            //TODO: Revisit the world parameter!
            String grp = MyWarp.permissions.getGroup(world.getWorldName(), name);
            
            
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
        return this.world.getWorldName();
    }

    @Override
    public boolean listWarp(CommandSender sender) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isValid() {
        return this.world.isValid();
    }
}
