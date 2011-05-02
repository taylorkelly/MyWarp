package me.taylorkelly.mywarp;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.EditorPermissions;
import de.xzise.xwarp.Permissions;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;
import de.xzise.xwarp.PermissionWrapper.WorldPermission;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.warpable.WarpablePlayer;
import de.xzise.xwarp.warpable.WarperFactory;

public class Warp {

    public enum Visibility {
        PRIVATE(0), PUBLIC(1), GLOBAL(2);

        public final int level;

        private Visibility(int level) {
            this.level = level;
        }

        public static Visibility parseLevel(int level) {
            for (Visibility visibility : Visibility.values()) {
                if (visibility.level == level) {
                    return visibility;
                }
            }
            return null;
        }
    }

    public int index;
    public String name;
    private String creator;
    private LocationWrapper location;
    /** This price value will be transfered to the owner. */
    private int price;
    private String owner;
    public Visibility visibility;
    public String welcomeMessage;
    public Map<String, EditorPermissions> editors;

    public static int nextIndex = 1;

    public Warp(int index, String name, String creator, String owner, LocationWrapper wrapper, Visibility visibility, Map<String, EditorPermissions> permissions, String welcomeMessage) {
        this.index = index;
        this.name = name;
        this.creator = creator;
        this.owner = owner;
        this.location = wrapper;
        this.visibility = visibility;
        if (permissions == null) {
            this.editors = new HashMap<String, EditorPermissions>();
        } else {
            this.editors = new HashMap<String, EditorPermissions>(permissions);
        }
        this.welcomeMessage = welcomeMessage;
        if (index > nextIndex)
            nextIndex = index;
        nextIndex++;
    }

    public Warp(String name, String creator, String owner, LocationWrapper wrapper) {
        this(nextIndex, name, creator, owner, wrapper, Visibility.PUBLIC, null, "Welcome to '" + name + "'");
    }

    public Warp(String name, Player creator) {
        this(name, creator.getName(), creator.getName(), new LocationWrapper(creator.getLocation()));
    }

    public Warp(String name, Location location) {
        this(name, "", "No Player", new LocationWrapper(location));
    }

    public boolean playerIsInvited(String name) {
        EditorPermissions ep = this.editors.get(name.toLowerCase());
        if (ep != null) {
            return ep.get(Permissions.WARP);
        } else {
            return false;
        }
    }

    public void assignNewId() {
        this.index = nextIndex++;
    }

    public boolean playerCanWarp(CommandSender sender, boolean viaSign) {
        Player player = WarperFactory.getPlayer(sender);
        String name = null;
        WorldPermission worldPermission = WorldPermission.TO_WORLD;
        if (player != null) {
            name = player.getName();
            if (player.getWorld().equals(this.getLocation().getWorld())) {
                worldPermission = WorldPermission.WITHIN_WORLD;
            }
        }
        
        // If the player isn't allowed to warp to/within the world cancel here!
        if (!MyWarp.permissions.hasWorldPermission(sender, worldPermission, this.getLocationWrapper().getWorld(), true)) {
            return false;
        }

        if (name != null && this.owner.equals(name) && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_OWN : PermissionTypes.TO_OWN))
            return true;
        if (name != null && this.playerIsInvited(name) && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_INVITED : PermissionTypes.TO_INVITED))
            return true;
        if (this.visibility == Visibility.PUBLIC && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_OTHER : PermissionTypes.TO_OTHER))
            return true;
        if (this.visibility == Visibility.GLOBAL && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_GLOBAL : PermissionTypes.TO_GLOBAL))
            return true;
        return MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_TO_ALL);
    }

    public boolean playerCanWarp(Warpable player) {
        // TODO: More elegant version?
        return playerCanWarp(player, true) || playerCanWarp(player, false);
    }
    
    /**
     * Returns if the location is save.
     * @return if the location is save. Is false if invalid.
     */
    public boolean isSave()
    {
        if (this.location.isValid()) {
            Location location = this.getLocation();
            Material lower = location.getBlock().getType();
            LocationWrapper.moveY(location, 1.0D);
            Material higher = location.getBlock().getType();

            return checkOpaqueMaterials(lower, higher);
        } else {
            return false;
        }
    }
    
    private static boolean checkOpaqueMaterials(Material... materials) {
        //TODO: Reorganize
        return checkMaterials(materials, Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.SAPLING, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.TORCH, Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_WIRE, Material.CROPS, Material.SIGN_POST, Material.LADDER, Material.RAILS, Material.WALL_SIGN, Material.LEVER, Material.STONE_PLATE, Material.WOOD_PLATE, Material.STONE_BUTTON, Material.SNOW, Material.WOODEN_DOOR, Material.PORTAL, Material.SUGAR_CANE_BLOCK, Material.IRON_DOOR, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.CAKE_BLOCK);
    }
    
    private static boolean checkMaterials(Material[] materials, Material... allowed) {
        for (Material material : materials) {
            if (!MinecraftUtil.contains(material, allowed)) {
                return false;
            }
        }
        return true;
    }

    public void setLocation(Positionable positionable) {
        this.setLocation(positionable.getLocation());
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public boolean isOwn(String name) {
        return this.owner.equals(name);
    }

    public boolean isCreator(String name) {
        return this.creator.equals(name);
    }

    public void invite(String player) {
        this.getPermissions(player).put(Permissions.WARP, true);
    }

    public void uninvite(String inviteeName) {
        this.getPermissions(inviteeName).put(Permissions.WARP, false);
    }

    public boolean playerCanModify(Player player, Permissions permission) {
        if (this.isOwn(player.getName()))
            return true;
        EditorPermissions ep = this.editors.get(player.getName().toLowerCase());
        if (ep != null) {
            return ep.get(permission);
        }
        return false;
    }

    public boolean listWarp(CommandSender sender) {

        // Admin permissions
        if (MyWarp.permissions.hasAdminPermission(sender))
            return true;

        Warpable warpable = WarperFactory.getWarpable(sender);

        if (warpable != null) {
            // Can warp
            if (this.playerCanWarp(warpable))
                return true;
            if (sender instanceof WarpablePlayer) {
                // Creator permissions
                if (this.isOwn(((WarpablePlayer) sender).getName()))
                    return true;
            }
        }

        return false;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setMessage(String message) {
        this.welcomeMessage = message;
    }

    public Location getLocation() {
        return this.location.getLocation().clone();
    }
    
    public LocationWrapper getLocationWrapper() {
        return this.location;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setLocation(Location location) {
        this.location = new LocationWrapper(location);
    }

    public EditorPermissions getEditorPermissions(String name) {
        EditorPermissions player = this.editors.get(name.toLowerCase());
        if (player == null) {
            return null;
        }
        return player;
    }

    public String[] getEditors() {
        return this.editors.keySet().toArray(new String[0]);
    }

    private EditorPermissions getPermissions(String name) {
        EditorPermissions player = this.editors.get(name.toLowerCase());
        if (player == null) {
            player = new EditorPermissions();
            this.editors.put(name.toLowerCase(), player);
        }
        return player;
    }

    public void addEditor(String name, String permissions) {
        this.getPermissions(name).parseString(permissions, true);
    }

    public void removeEditor(String name) {
        this.editors.remove(name.toLowerCase());
    }

    public static final WarpComparator WARP_NAME_COMPARATOR = new WarpComparator() {

        @Override
        public int compare(Warp warp1, Warp warp2) {
            return warp1.name.compareToIgnoreCase(warp2.name);
        }

    };

    public static final WarpComparator WARP_INDEX_COMPARATOR = new WarpComparator() {

        @Override
        public int compare(Warp warp1, Warp warp2) {
            return new Integer(warp1.index).compareTo(warp2.index);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o instanceof Warp) {
            Warp w = (Warp) o;
            return w.name.equals(this.name) && w.owner.equals(this.owner);
        } else {
            return false;
        }
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return this.price;
    }
}

interface WarpComparator extends Comparator<Warp> {
}
