package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.xzise.bukkit.util.MemorySectionFromMap;
import de.xzise.bukkit.util.callback.Callback;
import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.DefaultWarpObject;
import de.xzise.xwarp.DefaultWarpObject.EditorPermissionEntry;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.WorldWrapper;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.Warp.Visibility;
import de.xzise.xwarp.editors.Editor;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.editors.WarpPermissions;
import de.xzise.xwarp.editors.WarpProtectionAreaPermissions;

public class YmlConnection implements WarpProtectionConnection {

    //@formatter:off
    /*
     * xwarp:
     *   version: 0
     *   protectionareas:
     *     - name: 'foo'
     *       owner: 'xZise'
     *       creator: 'xZise'
     *       editors:
     *         - name: 'somebody'
     *           type: 'player'
     *           permissions:
     *             - 'overwrite'
     *       world: 'world'
     *       corners:
     *         - x: 0.0
     *           y: 1.0
     *           z: 0.0
     *         - x: 10.0
     *           y: 3.0
     *           z: 10.0
     *   warps:
     *     - name: 'foo'
     *       owner: 'xZise'
     *       creator: 'xZise'
     *       editors:
     *         - name: 'somebody'
     *           type: 'player'
     *           permissions:
     *             - 'location'
     *             - 'invite'
     *       x: 0.0
     *       y: 65.0
     *       z: -0.5
     *       yaw: 0.0
     *       pitch: 0.0
     *       world: 'world'
     *       visibility: 'public'
     *       listed: 'true'
     *       price: 0.0 # < 0 → def
     *       cooldown: 0 # < 0 → def
     *       warmup: 0 # < 0 → def
     *       welcome: 'Welcome!'
     * 
     */
    //@formatter:on

    private static final Callback<Warp, MemorySection> NODE_TO_WARP = new Callback<Warp, MemorySection>() {
        @Override
        public Warp call(MemorySection parameter) {
            return getWarp(parameter);
        }
    };

    private static final Callback<WarpProtectionArea, MemorySection> NODE_TO_WPA = new Callback<WarpProtectionArea, MemorySection>() {

        @Override
        public WarpProtectionArea call(MemorySection parameter) {
            return getWarpProtectionArea(parameter);
        }
    };

    private static final String WARP_PATH = "xwarp.warps";
    private static final String WPA_PATH = "xwarp.protectionareas";

    private YamlConfiguration config;
    private File file;

    @Override
    public boolean load(File file) {
        this.file = file;
        if (!file.exists()) {
            this.clear();
        }
        this.config = new YamlConfiguration();
        try {
            this.config.load(file);
        } catch (FileNotFoundException e) {
            XWarp.logger.severe("Unable to load warps.yml as it doesn't exist.", e);
        } catch (IOException e) {
            XWarp.logger.severe("Unable to load warps.yml.", e);
        } catch (InvalidConfigurationException e) {
            XWarp.logger.severe("Unable to load warps.yml because the configuration seems to be invalid!", e);
        }
        return file.canWrite();
    }

    @Override
    public boolean create(File file) {
        if (this.load(file)) {
            this.clear();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void free() {
        // TODO Auto-generated method stub
    }

    @Override
    public String getFilename() {
        return "warps.yml";
    }

    @Override
    public void clear() {
        try {
            FileWriter writer = new FileWriter(this.file);
            try {
                writer.write("xwarp:\n");
                writer.write("  version: 0\n");
                writer.write("  protectionareas: []\n");
                writer.write("  warps: []\n");
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            XWarp.logger.severe("Unable to write the file", e);
        }
    }

    public <T extends WarpObject<?>> IdentificationInterface<T> createIdentification(T warp) {
        return NameIdentification.create(warp);
    }

    @Override
    public IdentificationInterface<Warp> createWarpIdentification(Warp warp) {
        return NameIdentification.create(warp);
    }

    public static Warp getWarp(MemorySection node) {
        String name = node.getString("name");
        String owner = node.getString("owner");
        String creator = node.getString("creator");

        List<? extends ConfigurationSection> editorNodes = MemorySectionFromMap.getSectionList(node, "editors");
        Map<EditorPermissions.Type, Map<String, EditorPermissions<WarpPermissions>>> allPermissions = getEditorPermissions(editorNodes, WarpPermissions.STRING_MAP, WarpPermissions.class);

        // Location:
        Double x = getDouble(node, "x");
        Double y = getDouble(node, "y");
        Double z = getDouble(node, "z");
        Float yaw = getFloat(node, "yaw");
        Float pitch = getFloat(node, "pitch");
        String world = node.getString("world");
        World worldObject = Bukkit.getServer().getWorld(world);

        Visibility visibility = Visibility.parseString(node.getString("visibility"));
        boolean listed = node.getBoolean("listed", true);
        double price = node.getDouble("price", -1);
        int cooldown = node.getInt("cooldown", -1);
        int warmup = node.getInt("warmup", -1);
        String welcomeMessage = node.getString("welcome");

        Warp warp = new Warp(name, creator, owner, new LocationWrapper(new FixedLocation(worldObject, x, y, z, yaw, pitch), world));
        warp.setWelcomeMessage(welcomeMessage);
        for (Entry<EditorPermissions.Type, Map<String, EditorPermissions<WarpPermissions>>> typeEntry : allPermissions.entrySet()) {
            for (Entry<String, EditorPermissions<WarpPermissions>> editorEntry : typeEntry.getValue().entrySet()) {
                warp.getEditorPermissions(editorEntry.getKey(), true, typeEntry.getKey()).putAll(editorEntry.getValue());
            }
        }
        warp.setVisibility(visibility);
        warp.setListed(listed);
        warp.setPrice(price);
        warp.setCoolDown(cooldown);
        warp.setWarmUp(warmup);

        return warp;
    }

    @Override
    public List<Warp> getWarps() {
        return getList(WARP_PATH, NODE_TO_WARP);
    }

    public static Double getDouble(ConfigurationSection node, String path) {
        if (node.isDouble(path)) {
            return node.getDouble(path);
        } else {
            return null;
        }
    }

    public static Float getFloat(ConfigurationSection node, String path) {
        Double d = getDouble(node, path);
        if (d != null) {
            return d.floatValue();
        } else {
            return null;
        }
    }

    private static <T extends Enum<T> & Editor> Map<String, Object> warpObjectToMap(DefaultWarpObject<T> object) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", object.getName());
        map.put("owner", object.getOwner());
        map.put("creator", object.getCreator());
        map.put("world", object.getWorld());
        Collection<EditorPermissionEntry<T>> editorPermissionEntries = object.getEditorPermissionsList();
        List<Map<String, Object>> editorPermissionMaps = Lists.newArrayListWithCapacity(editorPermissionEntries.size());
        for (EditorPermissionEntry<T> entry : editorPermissionEntries) {
            editorPermissionMaps.add(editorPermissionToMap(entry.editorPermissions, entry.name, entry.type));
        }
        map.put("editors", editorPermissionMaps);
        return map;
    }

    private static <T extends Enum<T> & Editor> List<String> getPermissionsList(EditorPermissions<T> editorPermissions) {
        ImmutableSet<T> perms = editorPermissions.getByValue(true);
        List<String> permNames = Lists.newArrayListWithCapacity(perms.size());
        for (T p : perms) {
            permNames.add(p.getName());
        }
        return permNames;
    }

    private static <T extends Enum<T> & Editor> Map<String, Object> editorPermissionToMap(EditorPermissions<T> perm, String name, EditorPermissions.Type type) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", name);
        map.put("type", type.name);
        map.put("permissions", getPermissionsList(perm));
        return map;
    }

    @Override
    public void addWarp(Warp... warps) {
        List<Object> rawNodes = this.config.getList(WARP_PATH);

        for (Warp warp : warps) {
            Map<String, Object> warpMap = warpObjectToMap(warp);
            fixedLocToMap(warpMap, warp.getLocation(), false, true);
            warpMap.put("visibility", warp.getVisibility().name);
            warpMap.put("listed", warp.isListed());
            warpMap.put("price", warp.getPrice());
            warpMap.put("cooldown", warp.getCoolDown());
            warpMap.put("warmup", warp.getWarmUp());
            String rawMessage = warp.getRawWelcomeMessage();
            if (rawMessage != null) {
                warpMap.put("welcome", rawMessage);
            }
            rawNodes.add(warpMap);
        }

        this.save();
    }

    private static Map<String, Object> nodeToMap(MemorySection node) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : node.getKeys(false)) {
            map.put(key, node.get(key));
        }
        return map;
    }

    public static boolean bool(Boolean b, boolean nullIsTrue) {
        return b == null ? nullIsTrue : b;
    }

    public static class WarpObjectCallback<T extends WarpObject<?>> implements Callback<Boolean, MemorySection> {

        public final IdentificationInterface<T> id;
        public final Callback<T, MemorySection> warpObjectGetter;

        public WarpObjectCallback(IdentificationInterface<T> id, Callback<T, MemorySection> warpObjectGetter) {
            this.id = id;
            this.warpObjectGetter = warpObjectGetter;
        }

        public static <T extends WarpObject<?>> WarpObjectCallback<T> create(IdentificationInterface<T> id, Callback<T, MemorySection> warpObjectGetter) {
            return new WarpObjectCallback<T>(id, warpObjectGetter);
        }

        @Override
        public Boolean call(MemorySection parameter) {
            return !this.id.isIdentificated(this.warpObjectGetter.call(parameter));
        }

    }

    public static void removeFromList(MemorySection node, String key, Callback<Boolean, MemorySection> callback) {
        List<? extends MemorySection> nodes = MemorySectionFromMap.getSectionList(node, key);
        System.out.println("Node size: " + nodes.size() + " @" + key);
        List<Map<String, Object>> mapList = Lists.newArrayListWithCapacity(Math.max(nodes.size() - 1, 0));
        for (MemorySection singleNode : nodes) {
            Map<String, Object> a = nodeToMap(singleNode);
            if (bool(callback.call(singleNode), false)) {
                mapList.add(a);
                System.out.println("Added node #" + mapList.size() + " a.keySet() =>" + a.keySet());
            } else {
                System.out.println("Skiped node w/ a.keySet() =>" + a.keySet());
            }
        }
        node.set(key, mapList);
    }

    @Override
    public void deleteWarp(Warp warp) {
        removeFromList(this.config, WARP_PATH, WarpObjectCallback.create(NameIdentification.create(warp), NODE_TO_WARP));
        this.save();
    }

    private <T extends WarpObject<?>> MemorySection getNode(IdentificationInterface<T> id, String path, Callback<T, MemorySection> nodeToWarpObject) {
        List<? extends MemorySection> sections = MemorySectionFromMap.getSectionList(this.config, path);
        for (MemorySection section : sections) {
            T w = nodeToWarpObject.call(section);
            if (id.isIdentificated(w))
                return section;
        }
        return null;
    }

    private void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            XWarp.logger.severe("Unable to save warps.yml file!", e);
        }
    }

    private MemorySection getWarpNode(IdentificationInterface<Warp> id) {
        return getNode(id, WARP_PATH, NODE_TO_WARP);
    }

    private MemorySection getWarpProtectionAreaNode(IdentificationInterface<WarpProtectionArea> id) {
        return getNode(id, WPA_PATH, NODE_TO_WPA);
    }

    private void updateField(ConfigurationSection node, String path, Object value) {
        node.set(path, value);
        this.save();
    }

    private void updateWarpField(IdentificationInterface<Warp> id, String path, Object value) {
        this.updateField(this.getWarpNode(id), path, value);
    }

    private void updateWPAField(IdentificationInterface<WarpProtectionArea> id, String path, Object value) {
        this.updateField(this.getWarpProtectionAreaNode(id), path, value);
    }

    private void updateWPAField(WarpProtectionArea area, String path, Object value) {
        this.updateWPAField(NameIdentification.create(area), path, value);
    }

    private void updateWarpField(Warp warp, String path, Object value) {
        this.updateWarpField(NameIdentification.create(warp), path, value);
    }

    @Override
    public void updateCreator(Warp warp) {
        this.updateWarpField(warp, "creator", warp.getCreator());
    }

    @Override
    public void updateOwner(Warp warp, IdentificationInterface<Warp> identification) {
        this.updateWarpField(identification, "owner", warp.getOwner());
    }

    @Override
    public void updateName(Warp warp, IdentificationInterface<Warp> identification) {
        this.updateWarpField(identification, "name", warp.getName());
    }

    @Override
    public void updateMessage(Warp warp) {
        this.updateWarpField(warp, "creator", warp.getRawWelcomeMessage());
    }

    @Override
    public void updateVisibility(Warp warp) {
        this.updateWarpField(warp, "creator", warp.getVisibility().name);
    }

    @Override
    public void updateLocation(Warp warp) {
        ConfigurationSection node = getWarpNode(NameIdentification.create(warp));
        LocationWrapper locWrap = warp.getLocationWrapper();
        String world = locWrap.getWorld();
        FixedLocation loc = locWrap.getLocation();
        node.set("x", loc.x);
        node.set("y", loc.y);
        node.set("z", loc.z);
        node.set("yaw", loc.yaw);
        node.set("pitch", loc.pitch);
        node.set("world", world);
        this.save();
    }

    private <T extends Enum<T> & Editor> void updateEditor(MemorySection warpObjectNode, DefaultWarpObject<T> warpObject, final String name, final EditorPermissions.Type type) {

        final Callback<Boolean, MemorySection> editorCheck = new Callback<Boolean, MemorySection>() {
            @Override
            public Boolean call(MemorySection parameter) {
                String nodeName = parameter.getString("name");
                String nodeType = parameter.getString("type");
                return name.equalsIgnoreCase(nodeName) && type.equals(EditorPermissions.Type.parseName(nodeType));
            }
        };

        EditorPermissions<T> perms = warpObject.getEditorPermissions(name, false, type);
        if (perms == null) {
            removeFromList(warpObjectNode, "editors", editorCheck);
        } else {
            boolean found = false;
            List<? extends MemorySection> editorNodes = MemorySectionFromMap.getSectionList(warpObjectNode, "editors");
            List<Map<String, Object>> rawEditorNodes = Lists.newArrayListWithExpectedSize(editorNodes.size());
            for (MemorySection editorNode : editorNodes) {
                if (editorCheck.call(editorNode)) {
                    if (found) {
                        XWarp.logger.severe("Found at least two editor entries for name '" + name + "' and type '" + type + "'!");
                    } else {
                        // Got it!
                        editorNode.set("permissions", getPermissionsList(perms));
                        found = true;
                    }
                }
                rawEditorNodes.add(nodeToMap(editorNode));
            }
            // Add entry
            if (!found) {
                rawEditorNodes.add(editorPermissionToMap(perms, name, type));
            }
            warpObjectNode.set("editors", rawEditorNodes);
        }

        this.save();
    }

    @Override
    public void updateEditor(Warp warp, final String name, final EditorPermissions.Type type) {
        MemorySection warpNode = getWarpNode(NameIdentification.create(warp));
        this.updateEditor(warpNode, warp, name, type);
    }

    @Override
    public void updatePrice(Warp warp) {
        this.updateWarpField(warp, "creator", warp.getPrice());
    }

    private static <T extends Enum<T> & Editor> Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>> getEditorPermissions(List<? extends ConfigurationSection> editorNodes, Map<String, T> names, Class<T> clazz) {
        Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>> editorPermissions = Maps.newEnumMap(EditorPermissions.Type.class);
        for (ConfigurationSection editorNode : editorNodes) {
            String editorName = editorNode.getString("name");
            String editorType = editorNode.getString("type");

            EditorPermissions<T> permissions = EditorPermissions.create(clazz);

            List<String> editorPermissionPermssions = editorNode.getStringList("permissions");
            if (editorPermissionPermssions != null) {
                for (String editorPermission : editorPermissionPermssions) {
                    T perms = names.get(editorPermission.toLowerCase());

                    if (perms == null) {
                        // Unknown permission
                    } else {
                        permissions.put(perms, true);
                    }
                }
            }

            EditorPermissions.Type type = Type.parseName(editorType);

            if (type != null) {
                Map<String, EditorPermissions<T>> editor = editorPermissions.get(type);
                if (editor == null) {
                    editor = Maps.newHashMap();
                    editorPermissions.put(type, editor);
                }
                editor.put(editorName.toLowerCase(), permissions);
            }
        }
        return editorPermissions;
    }

    public static WarpProtectionArea getWarpProtectionArea(final MemorySection node) {
        String name = node.getString("name");
        String owner = node.getString("owner");
        String creator = node.getString("creator");
        WorldWrapper worldObject = new WorldWrapper(node.getString("world"));

        List<? extends ConfigurationSection> editorNodes = MemorySectionFromMap.getSectionList(node, "editors");
        Map<EditorPermissions.Type, Map<String, EditorPermissions<WarpProtectionAreaPermissions>>> editorPermissions = getEditorPermissions(editorNodes, WarpProtectionAreaPermissions.STRING_MAP, WarpProtectionAreaPermissions.class);

        // Corners:
        List<? extends ConfigurationSection> cornerNodes = MemorySectionFromMap.getSectionList(node, "corners");
        FixedLocation[] corners = new FixedLocation[cornerNodes.size()];

        if (corners.length < 2) {
            // At least two corners!
        } else if (corners.length != 2) {
            // As long as only cuboids are possible
        } else {
            int i = 0;
            for (ConfigurationSection cornerNode : cornerNodes) {
                Double x = getDouble(cornerNode, "x");
                Double y = getDouble(cornerNode, "y");
                Double z = getDouble(cornerNode, "z");
                if (x != null && y != null && z != null) {
                    corners[i++] = new FixedLocation(x, y, z);
                } else {
                    // Invalid corner
                }
            }
        }

        WarpProtectionArea warpProtectionArea = new WarpProtectionArea(worldObject, corners[0], corners[1], name, owner, creator);
        for (Entry<EditorPermissions.Type, Map<String, EditorPermissions<WarpProtectionAreaPermissions>>> allPermissionsEntry : editorPermissions.entrySet()) {
            for (Entry<String, EditorPermissions<WarpProtectionAreaPermissions>> editorEntry : allPermissionsEntry.getValue().entrySet()) {
                warpProtectionArea.getEditorPermissions(editorEntry.getKey(), true, allPermissionsEntry.getKey()).putAll(editorEntry.getValue());
            }
        }

        return warpProtectionArea;
    }

    private <T> List<T> getList(String path, Callback<T, MemorySection> converter) {
        List<? extends MemorySection> sections = MemorySectionFromMap.getSectionList(this.config, path);
        ArrayList<T> result = new ArrayList<T>(sections.size());
        for (MemorySection section : sections) {
            T t = converter.call(section);
            if (t != null)
                result.add(t);
        }
        result.trimToSize();
        return result;
    }

    @Override
    public List<WarpProtectionArea> getProtectionAreas() {
        return getList(WPA_PATH, NODE_TO_WPA);
    }

    private static void fixedLocToMap(Map<String, Object> locMap, FixedLocation location, boolean world, boolean direction) {
        locMap.put("x", location.x);
        locMap.put("y", location.y);
        locMap.put("z", location.z);
        if (direction) {
            locMap.put("yaw", location.yaw);
            locMap.put("pitch", location.pitch);
        }
        if (world) {
            locMap.put("world", location.world.getName());
        }
    }

    private static Map<String, Object> fixedLocToMap(FixedLocation location, boolean world, boolean direction) {
        Map<String, Object> locMap = Maps.newHashMap();
        fixedLocToMap(locMap, location, world, direction);
        return locMap;
    }

    @Override
    public void addProtectionArea(WarpProtectionArea... areas) {
        List<Object> rawNodes = this.config.getList(WPA_PATH);

        for (WarpProtectionArea wpa : areas) {
            Map<String, Object> wpaMap = warpObjectToMap(wpa);
            List<Map<String, Object>> corners = Lists.newArrayListWithCapacity(2);
            corners.add(fixedLocToMap(wpa.getCorner(0), false, false));
            corners.add(fixedLocToMap(wpa.getCorner(1), false, false));
            wpaMap.put("corners", corners);
            rawNodes.add(wpaMap);
        }
        this.save();
    }

    @Override
    public void deleteProtectionArea(WarpProtectionArea area) {
        removeFromList(this.config, WPA_PATH, WarpObjectCallback.create(NameIdentification.create(area), NODE_TO_WPA));
        this.save();
    }

    @Override
    public void updateEditor(WarpProtectionArea warp, String name, Type type) {
        MemorySection warpNode = getWarpProtectionAreaNode(NameIdentification.create(warp));
        this.updateEditor(warpNode, warp, name, type);
    }

    @Override
    public void updateCreator(WarpProtectionArea area) {
        this.updateWPAField(NameIdentification.create(area), "creator", area.getCreator());
    }

    @Override
    public void updateOwner(WarpProtectionArea warp, IdentificationInterface<WarpProtectionArea> identification) {
        this.updateWPAField(identification, "owner", warp.getOwner());
    }

    @Override
    public void updateName(WarpProtectionArea warp, IdentificationInterface<WarpProtectionArea> identification) {
        this.updateWPAField(identification, "name", warp.getName());
    }

    @Override
    public void updateWorld(WarpProtectionArea area) {
        this.updateWPAField(area, "world", area.getWorld());
    }

    @Override
    public IdentificationInterface<WarpProtectionArea> createWarpProtectionAreaIdentification(WarpProtectionArea area) {
        return NameIdentification.create(area);
    }

    @Override
    public void updateCoolDown(Warp warp) {
        this.updateWarpField(warp, "cooldown", warp.getCoolDown());
    }

    @Override
    public void updateWarmUp(Warp warp) {
        this.updateWarpField(warp, "warmup", warp.getWarmUp());
    }

}
