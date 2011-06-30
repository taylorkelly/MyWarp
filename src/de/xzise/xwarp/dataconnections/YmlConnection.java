package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;

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
     *     - name: foo
     *       owner: xZise
     *       creator: xZise
     *       editors:
     *         somebody:
     *           type: player
     *           permissions:
     *             - location
     *             - invite
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
        //TODO: Implement editors
        List<String> editorPlayers = new ArrayList<String>();
        List<String> editorGroups = new ArrayList<String>();
        for (Entry<String, ConfigurationNode> editorEntry : node.getNodes("editors").entrySet()) {
            String editorName = editorEntry.getKey();
            ConfigurationNode editorNode = editorEntry.getValue();
            String editorType = editorNode.getString("type");
            if (editorType.equals("player")) {
                
            } else if (editorEntry.equals("group")) {
                
            } else {
                
            }
        }
        // Location:
        Double x = getDouble(node, "x");
        Double y = getDouble(node, "y");
        Double z = getDouble(node, "z");
        Float yaw = getFloat(node, "yaw");
        Float pitch = getFloat(node, "pitch");
        String world = node.getString("world");
        
        Visibility visibility = Visibility.parseString(node.getString("visibility"));
        Boolean listed = getBool(node, "listed");
        double price = node.getDouble("price", -1);
        double cooldown = node.getDouble("cooldown", -1);
        double warmup = node.getDouble("warmup", -1);
        String welcomeMessage = node.getString("welcome");
        /*
         *       editors:
         *         somebody:
         *           type: player
         *           permissions:
         *             - location
         *             - invite
         */
        
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
    
    public static Boolean getBool(ConfigurationNode node, String path) {
        Object o = node.getProperty(path);
        if (o instanceof Boolean) {
            return ((Boolean) o);
        } else {
            return null;
        }
    }
    
    @Override
    public void addWarp(Warp... warp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteWarp(Warp warp) {

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
        this.updateField(warp, "creator", warp.welcomeMessage);
    }

    @Override
    public void updateVisibility(Warp warp) {
        this.updateField(warp, "creator", warp.visibility.name);
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
        ConfigurationNode editorNode = getNode(new NameIdentification(warp)).getNode("editors." + name);
    }

    @Override
    public void updatePrice(Warp warp) {
        this.updateField(warp, "creator", warp.getPrice());
    }

}
