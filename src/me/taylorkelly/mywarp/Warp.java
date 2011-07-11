package me.taylorkelly.mywarp;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.EditorPermissions;
import de.xzise.xwarp.Permissions;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.warpable.WarperFactory;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;
import de.xzise.xwarp.wrappers.permission.WorldPermission;

public class Warp implements WarpObject {

    public enum Visibility {
        PRIVATE(0, "private"), PUBLIC(1, "public"), GLOBAL(2, "global");

        private static final Map<String, Visibility> names = new HashMap<String, Warp.Visibility>();
        
        static {
            for (Visibility v : Visibility.values()) {
                names.put(v.name, v);
            }
        }
        
        public final int level;
        public final String name;

        private Visibility(int level, String name) {
            this.level = level;
            this.name = name;
        }

        public static Visibility parseString(String string) {
            return names.get(string);
        }

        public static Visibility parseLevel(int level) {
            // Bit 31 - 08 = unused
            // Bit      07 = !listed (→ bit set = not listed)
            // Bit 06 - 00 = visibility
            byte cleanedLevel = (byte) (level & 0x7F);
            for (Visibility visibility : Visibility.values()) {
                if (visibility.level == cleanedLevel) {
                    return visibility;
                }
            }
            return null;
        }
        
        public static boolean isListed(int level) {
            // Bit 31 - 08 = unused
            // Bit      07 = !listed (→ bit set = not listed)
            // Bit 06 - 00 = visibility
            return (level & 0x80) == 0;
        }
    }

    public int index;
    public String name;
    private String creator;
    private LocationWrapper location;
    /** This price value will be transfered to the owner. */
    private double price;
    private boolean listed;
    private String owner;
    private String welcomeMessage;
    public Visibility visibility;
    public Map<String, EditorPermissions> playerEditors;
    public Map<String, EditorPermissions> groupEditors;

    public static int nextIndex = 1;
    
    public static <K, V> Map<K, V> copyMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return new HashMap<K, V>();
        } else {
            return new HashMap<K, V>(map);
        }
    }

    public Warp(int index, String name, String creator, String owner, LocationWrapper wrapper, Visibility visibility, Map<String, EditorPermissions> playerPermission, Map<String, EditorPermissions> groupPermission, String welcomeMessage) {
        this.index = index;
        this.name = name;
        this.creator = creator;
        this.owner = owner;
        this.location = wrapper;
        this.visibility = visibility;
        this.playerEditors = copyMap(playerPermission);
        this.groupEditors = copyMap(groupPermission);
        this.welcomeMessage = welcomeMessage;
        this.listed = true;
        if (index > nextIndex)
            nextIndex = index;
        nextIndex++;
    }

    public Warp(String name, String creator, String owner, LocationWrapper wrapper) {
        this(nextIndex, name, creator, owner, wrapper, Visibility.PUBLIC, null, null, null);
    }

    public Warp(String name, Player creator) {
        this(name, creator.getName(), creator.getName(), new LocationWrapper(creator.getLocation()));
    }

    public Warp(String name, Location location) {
        this(name, "", "No Player", new LocationWrapper(location));
    }

    public boolean playerIsInvited(String name) {
        EditorPermissions ep = this.playerEditors.get(name.toLowerCase());
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
        Positionable pos = WarperFactory.getPositionable(sender);
        String name = null;
        WorldPermission worldPermission = WorldPermission.TO_WORLD;
        if (player != null) {
            name = player.getName();   
        }
        if (pos != null) {
            if (pos.getLocation().getWorld().getName().equals(this.getLocationWrapper().getWorld())) {
                worldPermission = WorldPermission.WITHIN_WORLD;
            }
        }
        
        // If the player isn't allowed to warp to/within the world cancel here!
        if (!MyWarp.permissions.permission(sender, worldPermission.getPermission(this.getLocationWrapper().getWorld(), true))) {
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
        //TODO: Check if the player can fall through: Check below if there is a torch (not on ground), wall sign
        
        if (this.location.isValid()) {
            Location location = this.getLocation().toLocation();
            Material lower = location.getBlock().getType();
            LocationWrapper.moveX(location, 1.0D);
            Material higher = location.getBlock().getType();
            LocationWrapper.moveX(location, 1.0D);
            Material top = location.getBlock().getType();
            
            Boolean save = null;
            double comma = MinecraftUtil.getDecimalPlaces(location.getY());
            
            //TODO: Determine comma for “step height”
            if (comma > 0.5D) {
                if (checkMaterials(lower, Material.STEP) && checkOpaqueMaterials(higher)) {
                    save = true;
                }
            }
            
            if (save == null) {
                //TODO: Determine comma
                if (comma <= 0.01D) {
                    save = checkOpaqueMaterials(lower, higher);
                } else {
                    save = checkOpaqueMaterials(lower, higher, top);
                }
            }

            return save;
        } else {
            return false;
        }
    }
    
    private static boolean checkOpaqueMaterials(Material... materials) {
        // TODO: Reorganize
        // @formatter:off
        return checkMaterials(materials,
                // "Solids" blocks
                Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.SNOW,
                // Plants
                Material.SAPLING, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.SUGAR_CANE_BLOCK, Material.CROPS,
                // Torches/Redstone
                Material.TORCH, Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_WIRE,
                // Signs
                Material.SIGN_POST, Material.WALL_SIGN,
                // Rails
                Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL,
                // Switches
                Material.LEVER, Material.STONE_PLATE, Material.WOOD_PLATE, Material.STONE_BUTTON,
                // Doors
                Material.WOODEN_DOOR, Material.IRON_DOOR,
                Material.LADDER,
                Material.PORTAL,
                Material.CAKE_BLOCK);
        // @formatter:on
    }
    
    private static boolean checkMaterials(Material material, Material... allowed) {
        return MinecraftUtil.contains(material, allowed);
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

    public boolean canModify(CommandSender sender, boolean defaultModification, PermissionTypes defaultPermission, PermissionTypes adminPermission) {
        if (defaultPermission != null) {
            return ((defaultModification && MyWarp.permissions.permission(sender, defaultPermission)) || MyWarp.permissions.permission(sender, adminPermission));
        } else {
            return (defaultModification || MyWarp.permissions.permission(sender, adminPermission));
        }
    }
    
    public boolean canModify(CommandSender sender, Permissions permission) {
        Player player = WarperFactory.getPlayer(sender);
        boolean canModify = false;
        if (player != null) {
            canModify = this.playerCanModify(player, permission);
        }

        return this.canModify(sender, canModify, permission.defaultPermission, permission.adminPermission);
    }
    
    public boolean playerCanModify(Player player, Permissions permission) {
        if (this.isOwn(player.getName()))
            return true;
        EditorPermissions ep = this.playerEditors.get(player.getName().toLowerCase());
        if (ep != null) {
            return ep.get(permission);
        }
        String group = MyWarp.permissions.getGroup(player.getWorld().getName(), player.getName());
        EditorPermissions grpPerm = this.groupEditors.get(group.toLowerCase());
        if (grpPerm != null) {
            return grpPerm.get(permission);
        }
        return false;
    }

    public boolean listWarp(CommandSender sender) {
        if (!this.isListed() && !MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_LIST_VIEW)) {
            return false;
        }
        
        // Admin permissions
        if (MyWarp.permissions.permissionOr(sender, PermissionTypes.getDefaultPermissions(false)))
            return true;

        Warpable warpable = WarperFactory.getWarpable(sender);

        if (warpable != null) {
            // Can warp
            if (this.playerCanWarp(warpable))
                return true;
            Player player = WarperFactory.getPlayer(warpable);
            if (player != null) {
                // Creator permissions
                if (this.isOwn(player.getName()))
                    return true;
            }
        }

        return false;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public FixedLocation getLocation() {
        return this.location.getLocation();
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
        this.setLocation(new FixedLocation(location));
    }
    
    public void setLocation(FixedLocation location) {
        this.location = new LocationWrapper(location);
    }

    public EditorPermissions getEditorPermissions(String name) {
        EditorPermissions player = this.playerEditors.get(name.toLowerCase());
        if (player == null) {
            return null;
        }
        return player;
    }

    public String[] getEditors() {
        return this.playerEditors.keySet().toArray(new String[0]);
    }

    public void setWelcomeMessage(String message) {
        this.welcomeMessage = message;
    }
    
    public String getWelcomeMessage() {
        if (this.welcomeMessage == null) {
            return "Welcome to '" + name + "'!";
        } else {
            return this.welcomeMessage;
        }
    }
    
    public String getRawWelcomeMessage() {
        return this.welcomeMessage;
    }
    
    private EditorPermissions getPermissions(String name) {
        EditorPermissions player = this.playerEditors.get(name.toLowerCase());
        if (player == null) {
            player = new EditorPermissions();
            this.playerEditors.put(name.toLowerCase(), player);
        }
        return player;
    }

    public void addEditor(String name, String permissions) {
        this.getPermissions(name).parseString(permissions, true);
    }

    public void removeEditor(String name) {
        this.playerEditors.remove(name.toLowerCase());
    }

    public static final Comparator<Warp> WARP_NAME_COMPARATOR = new StringComparator<Warp>() {

        @Override
        protected String getValue(Warp warp) {
            return warp.name;
        }

    };

    public static final Comparator<Warp> WARP_INDEX_COMPARATOR = new Comparator<Warp>() {

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

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return this.price;
    }

    public void setListed(boolean listed) {
        this.listed = listed;
    }

    public boolean isListed() {
        return listed;
    }

    public boolean isFree() {
        return this.price < 0;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getWorld() {
        return this.location.getWorld();
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }
}

abstract class StringComparator<T> implements Comparator<T> {
    
    private final Collator collator;
    
    public StringComparator(Collator collator) {
        this.collator = collator;
    }
    
    public StringComparator() {
        this(Collator.getInstance());
        this.collator.setStrength(Collator.SECONDARY);
    }
    
    protected abstract String getValue(T t);
    
    @Override
    public int compare(T t1, T t2) {
        return this.collator.compare(this.getValue(t1), this.getValue(t2));
    }
}