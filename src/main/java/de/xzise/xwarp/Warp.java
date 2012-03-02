package de.xzise.xwarp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dynmap.markers.Marker;

import de.xzise.MinecraftUtil;
import de.xzise.StringComparator;
import de.xzise.bukkit.util.callback.Callback;
import de.xzise.collections.ArrayReferenceList;
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
        PRIVATE((byte) 0, "private"),
        PUBLIC((byte) 1, "public"),
        GLOBAL((byte) 2, "global");

        private static final Map<String, Visibility> NAMES = MinecraftUtil.createReverseEnumMap(Visibility.class, new Callback<String, Visibility>() {
            public String call(Visibility visibility) {
                return visibility.name;
            }
        });
        private static final Map<Byte, Visibility> LEVELS = MinecraftUtil.createReverseEnumMap(Visibility.class, new Callback<Byte, Visibility>() {
            @Override
            public Byte call(Visibility visibility) {
                return visibility.level;
            }
        });

        public final byte level;
        public final String name;

        private Visibility(byte level, String name) {
            this.level = level;
            this.name = name;
        }

        public static Visibility parseString(String string) {
            return NAMES.get(string);
        }

        public static Visibility getByLevel(byte level) {
            return LEVELS.get(level);
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
    private Marker marker;
    private MarkerManager manager;

    private static int markerId = 0;
    public static int nextIndex = 1;

    public Warp(int index, String name, String creator, String owner, LocationWrapper wrapper, Visibility visibility, Map<EditorPermissions.Type, Map<String, EditorPermissions<WarpPermissions>>> editorPermissions, String welcomeMessage) {
        super(name, owner, creator, editorPermissions, WarpPermissions.class, WarpPermissions.WARP);
        this.index = index;
        this.location = wrapper;
        this.visibility = visibility;
        this.welcomeMessage = welcomeMessage;
        this.listed = true;
        this.warmup = -1;
        this.cooldown = -1;
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
        if (!XWarp.permissions.permission(sender, worldPermission.getPermission(this.getLocationWrapper().getWorld()))) {
            return false;
        }

        if (XWarp.permissions.permission(sender, new WarpEditorPermission(this, WarpPermissions.WARP)))
            return true;
        if (name != null && this.getOwner().equals(name) && XWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_OWN : PermissionTypes.TO_OWN))
            return true;
        if (name != null && this.hasPermission(name, WarpPermissions.WARP) && XWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_INVITED : PermissionTypes.TO_INVITED))
            return true;
        if (this.visibility == Visibility.PUBLIC && XWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_OTHER : PermissionTypes.TO_OTHER))
            return true;
        if (this.visibility == Visibility.GLOBAL && XWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_GLOBAL : PermissionTypes.TO_GLOBAL))
            return true;
        return XWarp.permissions.permission(sender, PermissionTypes.ADMIN_TO_ALL);
    }

    public boolean playerCanWarp(Warpable player) {
        return playerCanWarp(player, true) || playerCanWarp(player, false);
    }

    public boolean isSave() {
        return this.isSave(null);
    }

    /**
     * Returns if the location is save.
     * 
     * @return if the location is save. Is false if invalid.
     */
    public boolean isSave(CommandSender sender) {
        // TODO: Check if the player can fall through: Check below if there is a
        // torch (not on ground), wall sign

        if (this.location.isValid()) {
            Location location = this.getLocation().toLocation();
            Block block = location.getBlock().getRelative(BlockFace.UP, 2);

            Material[] materials = new Material[7];
            int i = 0;
            while (i < materials.length) {
                materials[i++] = block.getType();
                block = block.getRelative(BlockFace.DOWN);
            }

            Material top = materials[0];
            Material higher = materials[1];
            Material lower = materials[2];

            Boolean save = null;
            double comma = MinecraftUtil.getDecimalPlaces(location.getY());

            msg(sender, "Comma: " + comma);
            if (save == null) {
                if (comma < 0.05D) {
                    save = checkOpaqueMaterials(lower, higher);
                } else {
                    save = ((comma >= 0.49D && checkMaterials(lower, Material.STEP)) || checkOpaqueMaterials(lower)) && checkOpaqueMaterials(higher, top);
                }
            }

            if (save == null && sender != null) {
                sender.sendMessage("The save value is null!");
                save = false;
            }
            msg(sender, "Materials: " + Arrays.toString(materials));

            return save;
        } else {
            return false;
        }
    }

    private static void msg(CommandSender sender, String msg) {
        if (sender != null) {
            sender.sendMessage(msg);
        }
    }

    private static boolean checkOpaqueMaterials(Material... materials) {
        // TODO: Move to Minecraft Util?
        // @formatter:off
        return checkMaterials(materials,
                // "Solids" blocks
                Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.SNOW,
                // Plants
                Material.SAPLING, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.SUGAR_CANE_BLOCK, Material.CROPS, Material.LONG_GRASS, Material.DEAD_BUSH,
                // Torches/Redstone
                Material.TORCH, Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_WIRE,
                // Diodes
                Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON,
                // Signs
                Material.SIGN_POST, Material.WALL_SIGN,
                // Rails
                Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL,
                // Switches
                Material.LEVER, Material.STONE_PLATE, Material.WOOD_PLATE, Material.STONE_BUTTON,
                // Doors
                Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK, Material.TRAP_DOOR,
                Material.LADDER,
                Material.LEVER, Material.STONE_BUTTON,
                Material.PORTAL,
                Material.CAKE_BLOCK);
        // @formatter:on
    }

    private static boolean checkMaterials(Material material, Material... allowed) {
        return ArrayReferenceList.contains(material, allowed);
    }

    private static boolean checkMaterials(Material[] materials, Material... allowed) {
        for (Material material : materials) {
            if (!ArrayReferenceList.contains(material, allowed)) {
                return false;
            }
        }
        return true;
    }

    public void setLocation(Positionable positionable) {
        this.setLocation(positionable.getLocation());
    }

    public void uninvite(String inviteeName) {
        this.getEditorPermissions(inviteeName, Type.PLAYER).put(WarpPermissions.WARP, false);
    }

    public boolean isListed(CommandSender sender) {
        boolean accessable = false;
        // Admin permissions
        if (XWarp.permissions.permissionOr(sender, PermissionTypes.getDefaultPermissions(false)))
            accessable = true;

        Warpable warpable = WarperFactory.getWarpable(sender);

        boolean own = false;
        if (warpable != null) {
            // Can warp
            if (this.playerCanWarp(warpable))
                accessable = true;
            Player player = WarperFactory.getPlayer(warpable);
            if (player != null) {
                // Creator permissions
                if (this.isOwn(player.getName())) {
                    own = true;
                    accessable = true;
                }
            }
        }

        if (this.isListed() || XWarp.permissions.permission(sender, PermissionTypes.ADMIN_LIST_VIEW) || (own && XWarp.permissions.permission(sender, PermissionTypes.LIST_OWN))) {
            return accessable;
        } else {
            return false;
        }
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
        this.updateMarker();
    }

    public void setWelcomeMessage(String message) {
        this.welcomeMessage = message;
    }

    /**
     * Returns the raw welcome message. If the message is null, it shall return
     * the default message. If not null the warp specific message. If set to
     * nothing (result.isEmpty() is true) it show no message at all.
     * 
     * @return the raw welcome message.
     */
    public String getRawWelcomeMessage() {
        return this.welcomeMessage;
    }

    public void addEditor(String name, String permissions, EditorPermissions.Type type) {
        this.addEditor(name, type, WarpPermissions.parseString(permissions));
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

    public void setWorld(String worldName, World world) {
        this.location.setWorld(worldName, world);
        this.updateMarker();
    }

    public void updateMarker() {
        if (this.marker != null) {
            this.marker.setLabel(this.getName());
            FixedLocation loc = this.getLocation();
            this.marker.setLocation(this.getWorld(), loc.x, loc.y, loc.z);
        }
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
        this.checkMarker();
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public String getType() {
        return "warp";
    }

    @Override
    public boolean isValid() {
        return this.location.isValid();
    }

    private void checkMarker() {
        if (this.manager != null) {
            final boolean visible = this.manager.getMarkerSet() != null && this.manager.getMarkerIcon() != null && this.manager.getMarkerVisibilities().contains(this.getVisibility());
            if (marker != null) {
                this.marker.deleteMarker();
                this.marker = null;
            }
            if (visible) {
                FixedLocation loc = this.getLocation();
                this.marker = this.manager.getMarkerSet().createMarker("xwarp.warp.obj" + markerId++, this.getName(), this.getWorld(), loc.x, loc.y, loc.z, this.manager.getMarkerIcon(), false);
            }
        } else if (marker != null) {
            this.marker.deleteMarker();
            this.marker = null;
        }
    }

    public void setMarkerManager(MarkerManager manager) {
        this.manager = manager;
        this.checkMarker();
    }
}