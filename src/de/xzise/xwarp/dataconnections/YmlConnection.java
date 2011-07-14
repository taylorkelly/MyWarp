package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.EditorPermissions;
import de.xzise.xwarp.Permissions;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;

public class YmlConnection implements DataConnection {

    /*
     * xwarp:
     *   version: 0
     *   protectionareas:
     *     - name: foo
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

    @Override
    public IdentificationInterface createIdentification(Warp warp) {
        return new NameIdentification(warp);
    }

    public static Warp getWarp(ConfigurationNode node) {
        String name = node.getString("name");
        String owner = node.getString("owner");
        String creator = node.getString("creator");
        
        List<ConfigurationNode> editorNodes = node.getNodeList("editors", null);
        Map<String, EditorPermissions> groupPermissions = new HashMap<String, EditorPermissions>();
        Map<String, EditorPermissions> playerPermissions = new HashMap<String, EditorPermissions>();
        for (ConfigurationNode editorNode : editorNodes) {
            String editorName = editorNode.getString("name");
            String editorType = editorNode.getString("type");
            
            EditorPermissions permissions = new EditorPermissions();
            
            List<String> editorPermissions = editorNode.getStringList("permissions", null);
            for (String editorPermission : editorPermissions) {
                Permissions perms = null;
                
                if (editorPermission.equalsIgnoreCase("location")) {
                    perms = Permissions.UPDATE;
                } else if (editorPermission.equalsIgnoreCase("rename")) {
                    perms = Permissions.RENAME;
                } else if (editorPermission.equalsIgnoreCase("uninvite")) {
                    perms = Permissions.UNINVITE;
                } else if (editorPermission.equalsIgnoreCase("invite")) {
                    perms = Permissions.INVITE;
                } else if (editorPermission.equalsIgnoreCase("private")) {
                    perms = Permissions.PRIVATE;
                } else if (editorPermission.equalsIgnoreCase("public")) {
                    perms = Permissions.PUBLIC;
                } else if (editorPermission.equalsIgnoreCase("global")) {
                    perms = Permissions.GLOBAL;
                } else if (editorPermission.equalsIgnoreCase("give")) {
                    perms = Permissions.GIVE;
                } else if (editorPermission.equalsIgnoreCase("delete")) {
                    perms = Permissions.DELETE;
                } else if (editorPermission.equalsIgnoreCase("warp")) {
                    perms = Permissions.WARP;
                } else if (editorPermission.equalsIgnoreCase("add editor")) {
                    perms = Permissions.ADD_EDITOR;
                } else if (editorPermission.equalsIgnoreCase("remove editor")) {
                    perms = Permissions.REMOVE_EDITOR;
                } else if (editorPermission.equalsIgnoreCase("message")) {
                    perms = Permissions.MESSAGE;
                } else if (editorPermission.equalsIgnoreCase("price")) {
                    perms = Permissions.PRICE;
                } else if (editorPermission.equalsIgnoreCase("free")) {
                    perms = Permissions.FREE;
                } else if (editorPermission.equalsIgnoreCase("list")) {
                    perms = Permissions.LIST;
                }
                
                if (perms == null) {
                    // Unknown permission
                } else {
                    permissions.put(perms, true);
                }
            }
            
            if (editorType.equalsIgnoreCase("player")) {
                playerPermissions.put(editorName, permissions);
            } else if (editorType.equalsIgnoreCase("group")) {
                groupPermissions.put(editorName, permissions);
            } else {
                // Unknown editor type
            }
        }

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
        for (Entry<String, EditorPermissions> permissionEntry : playerPermissions.entrySet()) {
            warp.getPlayerEditorPermissions(permissionEntry.getKey(), true).putAll(permissionEntry.getValue());
        }
        for (Entry<String, EditorPermissions> permissionEntry : groupPermissions.entrySet()) {
            warp.getGroupEditorPermissions(permissionEntry.getKey(), true).putAll(permissionEntry.getValue());
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
        List<ConfigurationNode> nodes = this.config.getNodeList("xwarp.warps", null);
        List<Warp> warps = new ArrayList<Warp>(nodes.size());
        for (ConfigurationNode node : nodes) {
            Warp w = getWarp(node);
            if (w != null)
                warps.add(w);
        }
        return warps;
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
    
    @Override
    public void addWarp(Warp... warp) {
        // TODO Auto-generated method stub

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
    
    public static interface Callback<Result, Parameter> {
        Result call(Parameter parameter);
    }
    
    public static class WarpCallback implements Callback<Boolean, ConfigurationNode> {
        
        public final IdentificationInterface id;

        public WarpCallback(IdentificationInterface id) {
            this.id = id;
        }

        @Override
        public Boolean call(ConfigurationNode parameter) {
            return !id.isIdentificated(getWarp(parameter));
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
        removeFromList(this.config, "xwarp.warps", new WarpCallback(new NameIdentification(warp)));
    }

    private ConfigurationNode getNode(IdentificationInterface id) {
        List<ConfigurationNode> nodes = this.config.getNodeList("xwarp.warps", null);
        for (ConfigurationNode node : nodes) {
            Warp w = getWarp(node);
            if (id.isIdentificated(w))
                return node;
        }
        return null;
    }
    
    private void updateField(IdentificationInterface id, String path, Object value) {
        ConfigurationNode node = this.getNode(id);
        node.setProperty(path, value);
        this.config.save();
    }
    
    private void updateField(Warp warp, String path, Object value) {
        updateField(new NameIdentification(warp), path, value);
    }
    
    @Override
    public void updateCreator(Warp warp) {
        this.updateField(warp, "creator", warp.getCreator());
    }

    @Override
    public void updateOwner(Warp warp, IdentificationInterface identification) {
        this.updateField(identification, "owner", warp.getOwner());
    }

    @Override
    public void updateName(Warp warp, IdentificationInterface identification) {
        this.updateField(identification, "name", warp.name);
    }

    @Override
    public void updateMessage(Warp warp) {
        this.updateField(warp, "creator", warp.getWelcomeMessage());
    }

    @Override
    public void updateVisibility(Warp warp) {
        this.updateField(warp, "creator", warp.getVisibility().name);
    }

    @Override
    public void updateLocation(Warp warp) {
        ConfigurationNode node = getNode(new NameIdentification(warp));
        LocationWrapper locWrap = warp.getLocationWrapper();
        String world = locWrap.getWorld();
        FixedLocation loc = locWrap.getLocation();
        node.setProperty("x", loc.x);
        node.setProperty("x", loc.y);
        node.setProperty("z", loc.z);
        node.setProperty("yaw", loc.yaw);
        node.setProperty("pitch", loc.pitch);
        node.setProperty("world", world);
        config.save();
    }

    @Override
    public void updateEditor(Warp warp, String name) {
        List<ConfigurationNode> editorNodes = getNode(new NameIdentification(warp)).getNodeList("editors", null);
        
    }

    @Override
    public void updatePrice(Warp warp) {
        this.updateField(warp, "creator", warp.getPrice());
    }

}
