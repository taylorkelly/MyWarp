package de.xzise.xwarp.dataconnections;

import java.io.File;
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
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.xzise.Callback;
import de.xzise.MinecraftUtil;
import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.DefaultWarpObject;
import de.xzise.xwarp.DefaultWarpObject.EditorPermissionEntry;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.WorldWrapper;
import de.xzise.xwarp.Warp.Visibility;
import de.xzise.xwarp.editors.Editor;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.editors.WarpPermissions;
import de.xzise.xwarp.editors.WarpProtectionAreaPermissions;

import me.taylorkelly.mywarp.MyWarp;

public class YmlConnection implements WarpProtectionConnection {

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

    private static final Callback<Warp, ConfigurationNode> NODE_TO_WARP = new Callback<Warp, ConfigurationNode>() {
        @Override
        public Warp call(ConfigurationNode parameter) {
            return getWarp(parameter);
        }
    };
    
    private static final Callback<WarpProtectionArea, ConfigurationNode>  NODE_TO_WPA = new Callback<WarpProtectionArea, ConfigurationNode>() {

        @Override
        public WarpProtectionArea call(ConfigurationNode parameter) {
            return getWarpProtectionArea(parameter);
        }
    };
    
    private static final String WARP_PATH = "xwarp.warps";
    private static final String WPA_PATH = "xwarp.protectionareas";
    
    private Configuration config;
    private File file;
    
    @Override
    public boolean load(File file) {
        this.file = file;
        this.config = new Configuration(file);
        if (file.exists()) {
            this.config.load();
            return true;
        } else {
            return file.canWrite();
        }
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
                writer.write("xwarp:");
                writer.write("  version: 0");
                writer.write("  protectionareas: []");
                writer.write("  warps: []");
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            MyWarp.logger.severe("Unable to write the file", e);
        }
    }
    
    public <T extends WarpObject<?>> IdentificationInterface<T> createIdentification(T warp) {
        return NameIdentification.create(warp);
    }

    @Override
    public IdentificationInterface<Warp> createWarpIdentification(Warp warp) {
        return NameIdentification.create(warp);
    }

    public static Warp getWarp(ConfigurationNode node) {
        String name = node.getString("name");
        String owner = node.getString("owner");
        String creator = node.getString("creator");
        
        List<ConfigurationNode> editorNodes = node.getNodeList("editors", null);
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

    public static Double getDouble(ConfigurationNode node, String path) {
        Object o = node.getProperty(path);
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        } else {
            return null;
        }
    }
    
    public static Float getFloat(ConfigurationNode node, String path) {
        Object o = node.getProperty(path);
        if (o instanceof Number) {
            return ((Number) o).floatValue();
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
    
    private static <T extends Enum<T> & Editor> Map<String, Object> editorPermissionToMap(EditorPermissions<T> perm, String name, EditorPermissions.Type type) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", name);
        map.put("type", type.name[0]);
        T[] perms = perm.getByValue(true);
        List<String> permNames = Lists.newArrayListWithCapacity(perms.length);
        for (T p : perms) {
            permNames.add(p.getName());
        }
        map.put("permissions", permNames);
        return map;
    }
    
    @Override
    public void addWarp(Warp... warps) {
        List<Object> rawNodes = this.config.getList(WPA_PATH);
        
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
        
        this.config.save();
    }

    private static Map<String, Object> nodeToMap(ConfigurationNode node) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : node.getKeys()) {
            map.put(key, node.getProperty(key));
        }
        return map;
    }
    
    public static boolean bool(Boolean b, boolean nullIsTrue) {
        return b == null ? nullIsTrue : b;
    }
    
    public static class WarpObjectCallback<T extends WarpObject<?>> implements Callback<Boolean, ConfigurationNode> {
        
        public final IdentificationInterface<T> id;
        public final Callback<T, ConfigurationNode> warpObjectGetter;

        public WarpObjectCallback(IdentificationInterface<T> id, Callback<T, ConfigurationNode> warpObjectGetter) {
            this.id = id;
            this.warpObjectGetter = warpObjectGetter;
        }
        
        public static <T extends WarpObject<?>> WarpObjectCallback<T> create(IdentificationInterface<T> id, Callback<T, ConfigurationNode> warpObjectGetter) {
            return new WarpObjectCallback<T>(id, warpObjectGetter);
        }

        @Override
        public Boolean call(ConfigurationNode parameter) {
            return !id.isIdentificated(warpObjectGetter.call(parameter));
        }
        
    }
    
    public static void removeFromList(ConfigurationNode node, String key, Callback<Boolean, ConfigurationNode> callback) {
        List<ConfigurationNode> nodes = node.getNodeList(key, null);
        List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>(Math.max(nodes.size() - 1, 0));
        for (ConfigurationNode singleNode : nodes) {
            if (bool(callback.call(singleNode), false)) {
                mapList.add(nodeToMap(node));
            }
        }
        node.setProperty(key, mapList);
    }
    
    @Override
    public void deleteWarp(Warp warp) {
        removeFromList(this.config, WARP_PATH, WarpObjectCallback.create(NameIdentification.create(warp), NODE_TO_WARP));
        this.config.save();
    }

    private <T extends WarpObject<?>> ConfigurationNode getNode(IdentificationInterface<T> id, String path, Callback<T, ConfigurationNode> nodeToWarpObject) {
        List<ConfigurationNode> nodes = this.config.getNodeList(path, null);
        for (ConfigurationNode node : nodes) {
            T w = nodeToWarpObject.call(node);
            if (id.isIdentificated(w))
                return node;
        }
        return null;
    }
    
    private ConfigurationNode getWarpNode(IdentificationInterface<Warp> id) {
        return getNode(id, WARP_PATH, NODE_TO_WARP);
    }
    
    private ConfigurationNode getWarpProtectionAreaNode(IdentificationInterface<WarpProtectionArea> id) {
        return getNode(id, WPA_PATH, NODE_TO_WPA);
    }
    
    private void updateField(ConfigurationNode node, String path, Object value) {
        node.setProperty(path, value);
        this.config.save();
    }
    
    private void updateWarpField(IdentificationInterface<Warp> id, String path, Object value) {
        updateField(this.getWarpNode(id), path, value);
    }
    
    private void updateWPAField(IdentificationInterface<WarpProtectionArea> id, String path, Object value) {
        updateField(this.getWarpProtectionAreaNode(id), path, value);
    }
    
    private void updateWarpField(Warp warp, String path, Object value) {
        updateWarpField(NameIdentification.create(warp), path, value);
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
        this.updateWarpField(warp, "creator", warp.getWelcomeMessage());
    }

    @Override
    public void updateVisibility(Warp warp) {
        this.updateWarpField(warp, "creator", warp.getVisibility().name);
    }

    @Override
    public void updateLocation(Warp warp) {
        ConfigurationNode node = getWarpNode(NameIdentification.create(warp));
        LocationWrapper locWrap = warp.getLocationWrapper();
        String world = locWrap.getWorld();
        FixedLocation loc = locWrap.getLocation();
        node.setProperty("x", loc.x);
        node.setProperty("x", loc.y);
        node.setProperty("z", loc.z);
        node.setProperty("yaw", loc.yaw);
        node.setProperty("pitch", loc.pitch);
        node.setProperty("world", world);
        this.config.save();
    }
    
    private <T extends Enum<T> & Editor> void updateEditor(ConfigurationNode warpObjectNode, DefaultWarpObject<T> warpObject, final String name, final EditorPermissions.Type type) {
        
        final Callback<Boolean, ConfigurationNode> editorCheck = new Callback<Boolean, ConfigurationNode>() {
            @Override
            public Boolean call(ConfigurationNode parameter) {
                String nodeName = parameter.getString("name");
                String nodeType = parameter.getString("type");
                return name.equalsIgnoreCase(nodeName) && type.equals(EditorPermissions.Type.parseName(nodeType));
            }
        };  
        
        EditorPermissions<T> perms = warpObject.getEditorPermissions(name, false, type);
        if (perms == null) {
            removeFromList(warpObjectNode, "editors", editorCheck);
        } else {
            List<ConfigurationNode> editorNodes = warpObjectNode.getNodeList("editors", null);
            for (ConfigurationNode editorNode : editorNodes) {
                if (editorCheck.call(editorNode)) {
                    // Got it!
                    T[] warpPermissions = perms.getByValue(true);
                    String[] warpPermissionsNames = new String[warpPermissions.length];
                    // Fill
                    int i = 0;
                    for (T warpPerm : warpPermissions) {
                        warpPermissionsNames[i++] = warpPerm.getName();
                    }
                    editorNode.setProperty("permissions", warpPermissionsNames);
                }
            }
        }
        
        this.config.save();
    }

    @Override
    public void updateEditor(Warp warp, final String name, final EditorPermissions.Type type) {
        ConfigurationNode warpNode = getWarpNode(NameIdentification.create(warp));
        this.updateEditor(warpNode, warp, name, type);
    }

    @Override
    public void updatePrice(Warp warp) {
        this.updateWarpField(warp, "creator", warp.getPrice());
    }
    
    private static <T extends Enum<T> & Editor> Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>> getEditorPermissions(List<ConfigurationNode> editorNodes, Map<String, T> names, Class<T> clazz) {
        Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>> editorPermissions = MinecraftUtil.createEnumMap(EditorPermissions.Type.class);
        for (ConfigurationNode editorNode : editorNodes) {
            String editorName = editorNode.getString("name");
            String editorType = editorNode.getString("type");
            
            EditorPermissions<T> permissions = EditorPermissions.create(clazz);
            
            List<String> editorPermissionPermssions = editorNode.getStringList("permissions", null);
            for (String editorPermission : editorPermissionPermssions) {
                T perms = names.get(editorPermission.toLowerCase());
                
                if (perms == null) {
                    // Unknown permission
                } else {
                    permissions.put(perms, true);
                }
            }
            
            EditorPermissions.Type type = null;
            if (editorType.equalsIgnoreCase("player")) {
                type = Type.PLAYER;
            } else if (editorType.equalsIgnoreCase("group")) {
                type = Type.GROUP;
            } else {
                // Unknown editor type
            }
            
            if (type != null) {
                Map<String, EditorPermissions<T>> editor = editorPermissions.get(type);
                if (editor == null) {
                    editor = MinecraftUtil.createHashMap();
                    editorPermissions.put(type, editor);
                }
                editor.put(editorName.toLowerCase(), permissions);
            }
        }
        return editorPermissions;
    }
    
    public static WarpProtectionArea getWarpProtectionArea(ConfigurationNode node) {
        String name = node.getString("name");
        String owner = node.getString("owner");
        String creator = node.getString("creator");
        WorldWrapper worldObject = new WorldWrapper(node.getString("world"));
        
        List<ConfigurationNode> editorNodes = node.getNodeList("editors", null);
        Map<EditorPermissions.Type, Map<String, EditorPermissions<WarpProtectionAreaPermissions>>> editorPermissions = getEditorPermissions(editorNodes, WarpProtectionAreaPermissions.STRING_MAP, WarpProtectionAreaPermissions.class);
        
        // Corners:
        List<ConfigurationNode> cornerNodes = node.getNodeList("corner", null);
        FixedLocation[] corners = new FixedLocation[cornerNodes.size()];
        
        if (corners.length < 2) {
            // At least two corners!
        } else if (corners.length != 2) {
            // As long as only cuboids are possible
        } else {
            int i = 0;
            for (ConfigurationNode cornerNode : cornerNodes) {
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
    
    private <T> List<T> getList(String path, Callback<T, ConfigurationNode> converter) {
        List<ConfigurationNode> nodes = this.config.getNodeList(path, null);
        ArrayList<T> result = new ArrayList<T>(nodes.size());
        for (ConfigurationNode node : nodes) {
            T t = converter.call(node);
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
    }

    @Override
    public void deleteProtectionArea(WarpProtectionArea area) {
        removeFromList(this.config, WPA_PATH, WarpObjectCallback.create(NameIdentification.create(area), NODE_TO_WPA));
    }

    @Override
    public void updateEditor(WarpProtectionArea warp, String name, Type type) {
        ConfigurationNode warpNode = getWarpProtectionAreaNode(NameIdentification.create(warp));
        this.updateEditor(warpNode, warp, name, type);
    }

    @Override
    public void updateCreator(WarpProtectionArea area) {
        updateWPAField(NameIdentification.create(area), "creator", area.getCreator());
    }

    @Override
    public void updateOwner(WarpProtectionArea warp, IdentificationInterface<WarpProtectionArea> identification) {
        updateWPAField(identification, "owner", warp.getOwner());
    }

    @Override
    public void updateName(WarpProtectionArea warp, IdentificationInterface<WarpProtectionArea> identification) {
        updateWPAField(identification, "name", warp.getName());
    }

    @Override
    public IdentificationInterface<WarpProtectionArea> createWarpProtectionAreaIdentification(WarpProtectionArea area) {
        return NameIdentification.create(area);
    }

}
