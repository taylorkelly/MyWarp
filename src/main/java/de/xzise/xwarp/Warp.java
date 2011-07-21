package de.xzise.xwarp;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.WarpPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.warpable.WarperFactory;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;
import de.xzise.xwarp.wrappers.permission.WarpEditorPermission;
import de.xzise.xwarp.wrappers.permission.WorldPermission;

public class Warp extends DefaultWarpObject<WarpPermissions> {

    public enum Visibility {
        PRIVATE((byte) 0, "private"), PUBLIC((byte) 1, "public"), GLOBAL((byte) 2, "global");

        private static final Map<String, Visibility> names = new HashMap<String, Warp.Visibility>();
        
        static {
            for (Visibility v : Visibility.values()) {
                names.put(v.name, v);
            }
        }
        
        public final byte level;
        public final String name;

        private Visibility(byte level, String name) {
            this.level = level;
            this.name = name;
        }
        
        public byte getInt(boolean listed) {
            byte v = (byte) (this.level & 0x7F);
            if (listed) {
                v |= 1 << 7;
            }
            return v;
        }

        public static Visibility parseString(String string) {
            return names.get(string);
        }
        
        private static Visibility parseCleanedLevel(byte level) {
            for (Visibility visibility : Visibility.values()) {
                if (visibility.level == level) {
                    return visibility;
                }
            }
            return null;
        }

        public static Visibility parseLevel(int level) {
            // Bit 31 - 08 = unused
            // Bit      07 = !listed (→ bit set = not listed)
            // Bit 06 - 00 = visibility
            return parseCleanedLevel((byte) (level & 0x7F));
        }
        
        public static boolean isListed(int level) {
            // Bit 31 - 08 = unused
            // Bit      07 = !listed (→ bit set = not listed)
            // Bit 06 - 00 = visibility
            return (level & 0x80) == 0;
        }
    }

    public int index;
    private LocationWrapper location;
    /** This price value will be transfered to the owner. */
    private double price;
    private int cooldown;
    private int warmup;
    private boolean listed;
    private String welcomeMessage;
    private Visibility visibility;

    public static int nextIndex = 1;

    public Warp(int index, String name, String creator, String owner, LocationWrapper wrapper, Visibility visibility, Map<EditorPermissions.Type, Map<String, EditorPermissions<WarpPermissions>>> editorPermissions, String welcomeMessage) {
        super(name, owner, creator, editorPermissions, WarpPermissions.class);
        this.index = index;
        this.location = wrapper;
        this.visibility = visibility;
        this.welcomeMessage = welcomeMessage;
        this.listed = true;
        if (index > nextIndex)
            nextIndex = index;
        nextIndex++;
    }

    public Warp(String name, String creator, String owner, LocationWrapper wrapper) {
        this(nextIndex, name, creator, owner, wrapper, Visibility.PUBLIC, null, null);
    }

    public Warp(String name, Player creator) {
        this(name, creator.getName(), creator.getName(), new LocationWrapper(creator.getLocation()));
    }

    public Warp(String name, Location location) {
        this(name, "", "No Player", new LocationWrapper(location));
    }
    
    public boolean isInvited(String name, boolean checkPlayerOnly) {
        EditorPermissions<WarpPermissions> ep = this.getEditorPermissions(name, Type.PLAYER);
        if (ep != null && ep.get(WarpPermissions.WARP)) {
            return true;
        }
        
        if (!checkPlayerOnly) {
            //TODO: Implement groups
            //TODO: Implement permissions
        }
        
        return false;
    }

    public void assignNewId() {
        this.index = nextIndex++;
    }

    public boolean playerCanWarp(CommandSender sender, boolean viaSign) {
        if (MyWarp.permissions.permission(sender, new WarpEditorPermission(this, WarpPermissions.WARP))) {
            return true;
        }
        
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

        if (name != null && this.getOwner().equals(name) && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_OWN : PermissionTypes.TO_OWN))
            return true;
        if (name != null && this.isInvited(name, false) && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_INVITED : PermissionTypes.TO_INVITED))
            return true;
        if (this.visibility == Visibility.PUBLIC && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_OTHER : PermissionTypes.TO_OTHER))
            return true;
        if (this.visibility == Visibility.GLOBAL && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_GLOBAL : PermissionTypes.TO_GLOBAL))
            return true;
        return MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_TO_ALL);
    }

    public boolean playerCanWarp(Warpable player) {
        return playerCanWarp(player, true) || playerCanWarp(player, false);
    }
    
    /**
     * Returns if the location is save.
     * @return if the location is save. Is false if invalid.
     */
    public boolean isSave() {
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
            
            if (save == null) {
                if (comma <= 0.05D) {
                    save = checkOpaqueMaterials(lower, higher);
                } else {
                    if (comma > 0.5D) {
                        save = checkMaterials(lower, Material.STEP);
                    }
                    save = ((comma > 0.5D && checkMaterials(lower, Material.STEP)) || checkOpaqueMaterials(lower)) && checkOpaqueMaterials(higher, top);
                }
            }

            return save;
        } else {
            return false;
        }
    }
    
    private static boolean checkOpaqueMaterials(Material... materials) {
        //TODO: Move to Minecraft Util?
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

    public boolean isOwn(String name) {
        return this.getOwner().equals(name);
    }

    public boolean isCreator(String name) {
        return this.getCreator().equals(name);
    }

    public void invite(String player) {
        this.getEditorPermissions(player, Type.PLAYER).put(WarpPermissions.WARP, true);
    }

    public void uninvite(String inviteeName) {
        this.getEditorPermissions(inviteeName, Type.PLAYER).put(WarpPermissions.WARP, false);
    }

    public boolean canModify(CommandSender sender, boolean defaultModification, PermissionTypes defaultPermission, PermissionTypes adminPermission) {
        if (defaultPermission != null) {
            return ((defaultModification && MyWarp.permissions.permission(sender, defaultPermission)) || MyWarp.permissions.permission(sender, adminPermission));
        } else {
            return (defaultModification || MyWarp.permissions.permission(sender, adminPermission));
        }
    }
    
    public boolean canModify(CommandSender sender, WarpPermissions permission) {
        Player player = WarperFactory.getPlayer(sender);
        boolean canModify = false;
        if (player != null) {
            canModify = this.playerCanModify(player, permission);
        }

        return this.canModify(sender, canModify, permission.defaultPermission, permission.adminPermission);
    }
    
    public boolean playerCanModify(Player player, WarpPermissions permission) {
        if (this.isOwn(player.getName()))
            return true;
        EditorPermissions<WarpPermissions> ep = this.getEditorPermissions(player.getName().toLowerCase(), Type.PLAYER);
        if (ep != null && ep.get(permission)) {
            return true;
        }
        String[] groups = MyWarp.permissions.getGroup(player.getWorld().getName(), player.getName());
        for (String group : groups) {
            EditorPermissions<WarpPermissions> groupPerm = this.getEditorPermissions(group, Type.GROUP);
            if (groupPerm != null && groupPerm.get(permission)) {
                return true;
            }    
        }
        return false;
    }

    public boolean list(CommandSender sender) {
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

    public FixedLocation getLocation() {
        return this.location.getLocation();
    }
    
    public LocationWrapper getLocationWrapper() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.setLocation(new FixedLocation(location));
    }
    
    public void setLocation(FixedLocation location) {
        this.location = new LocationWrapper(location);
    }

    public void setWelcomeMessage(String message) {
        this.welcomeMessage = message;
    }
    
    public String getWelcomeMessage() {
        if (this.welcomeMessage == null) {
            return "Welcome to '" + this.getName() + "'!";
        } else {
            return this.welcomeMessage;
        }
    }
    
    /**
     * Returns the raw welcome message. If the message is null, it shall return
     * the default message. If not null the warp specific message. If set to
     * nothing (result.isEmpty() is true) it show no message at all.
     * @return the raw welcome message.
     */
    public String getRawWelcomeMessage() {
        return this.welcomeMessage;
    }

    public void addEditor(String name, String permissions, EditorPermissions.Type type) {
        this.getEditorPermissions(name, true, type).parseString(WarpPermissions.parseString(permissions), true);
    }

    public static final Comparator<Warp> WARP_NAME_COMPARATOR = new StringComparator<Warp>() {

        @Override
        protected String getValue(Warp warp) {
            return warp.getName();
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
            return w.getName().equals(this.getName()) && w.getOwner().equals(this.getOwner());
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
    public String getWorld() {
        return this.location.getWorld();
    }
    
    public void setWarmUp(int warmup) {
        this.warmup = warmup;
    }
    
    public int getWarmUp() {
        return this.warmup;
    }
    
    public void setCoolDown(int cooldown) {
        this.cooldown = cooldown;
    }
    
    public int getCoolDown() {
        return this.cooldown;
    }
    
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

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