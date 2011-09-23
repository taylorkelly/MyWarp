package de.xzise.xwarp;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.wrappers.permissions.PermissionsHandler;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.commands.ManageCommandMap;
import de.xzise.xwarp.commands.WPACommandMap;
import de.xzise.xwarp.commands.WarpCommandMap;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.listeners.XWBlockListener;
import de.xzise.xwarp.listeners.XWEntityListener;
import de.xzise.xwarp.listeners.XWPlayerListener;
import de.xzise.xwarp.listeners.XWServerListener;
import de.xzise.xwarp.listeners.XWWorldListener;
import de.xzise.xwarp.wrappers.permission.GeneralPermissions;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;
import de.xzise.xwarp.wrappers.permission.WPAPermissions;
import de.xzise.xwarp.wrappers.permission.WorldPermission;

public class XWarp extends JavaPlugin {

    public static PermissionsHandler permissions;
    public static XLogger logger;

    private EconomyHandler economyHandler;
    private PermissionsHandler permissionHandler = permissions;

    private DataConnection dataConnection;
    private WarpManager warpManager;

    public String name;
    public String version;

    public XWarp() {
        super();
    }

    @Override
    public void onDisable() {
        this.dataConnection.free();
        XWarp.logger.disableMsg();
    }

    @Override
    public void onEnable() {
        this.name = this.getDescription().getName();
        this.version = this.getDescription().getVersion();
        logger = new XLogger(this);

        // Register permissions
        MinecraftUtil.register(this.getServer().getPluginManager(), logger, PermissionTypes.values(), WPAPermissions.values(), GeneralPermissions.values());

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }

        if (new File("MyWarp").exists() && new File("MyWarp", "warps.db").exists()) {
            this.updateFiles();
        } else {
            File old = new File("homes-warps.db");
            File newFile = new File(this.getDataFolder(), "warps.db");
            if (old.exists() && !newFile.exists()) {
                XWarp.logger.info("No database found. Copying old database.");
                try {
                    MinecraftUtil.copy(old, newFile);
                } catch (IOException e) {
                    XWarp.logger.severe("Unable to copy database", e);
                }
            }
        }

        PluginProperties properties = new PluginProperties(this.getDataFolder(), this.getServer());

        this.dataConnection = properties.getDataConnection();
        try {
            if (!this.dataConnection.load(properties.getDataConnectionFile())) {
                XWarp.logger.severe("Could not load data. Disabling " + this.name + "!");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } catch (Exception e) {
            XWarp.logger.severe("Could not load data. Disabling " + this.name + "!", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.permissionHandler = new PermissionsHandler(this.getServer().getPluginManager(), properties.getPermissionsPlugin(), logger);
        permissions = this.permissionHandler;
        this.economyHandler = new EconomyHandler(this.getServer().getPluginManager(), properties.getEconomyPlugin(), properties.getEconomyBaseAccount(), logger);
        XWServerListener serverListener = new XWServerListener(properties, this.getServer().getPluginManager(), this.warpManager, this.permissionHandler, this.economyHandler);

        this.warpManager = new WarpManager(this, this.economyHandler, properties, this.dataConnection);
        WPAManager wpaManager = new WPAManager(this, this.dataConnection, properties);

        this.warpManager.setWPAManager(wpaManager);

        // Create commands
        WarpCommandMap wcm = null;
        WPACommandMap wpacm = null;
        ManageCommandMap mcm = null;
        try {
            wcm = new WarpCommandMap(this.warpManager, this.economyHandler, this.getServer(), this.dataConnection, this.getDataFolder(), properties);
            wpacm = new WPACommandMap(wpaManager, this.economyHandler, this.getServer(), this.dataConnection, this.getDataFolder(), properties);
            mcm = new ManageCommandMap(this.economyHandler, serverListener, properties, this.getServer(), this.getDataFolder(), warpManager, wpaManager);
        } catch (IllegalArgumentException iae) {
            XWarp.logger.severe("Couldn't initalize commands. Disabling " + this.name + "!", iae);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getCommand("go").setExecutor(wcm.getCommand(""));
        this.getCommand("xwarp").setExecutor(mcm);
        this.getCommand("warp").setExecutor(wcm);
        this.getCommand("wpa").setExecutor(wpacm);

        XWPlayerListener playerListener = new XWPlayerListener(this.warpManager, properties, wpacm.createCommand);
        XWBlockListener blockListener = new XWBlockListener(this.warpManager);
        XWWorldListener worldListener = new XWWorldListener(this.getServer().getPluginManager(), warpManager, wpaManager);

        // Unless an event is called, to tell all enabled plugins
        this.permissionHandler.load();
        this.economyHandler.load();
        serverListener.load();

        // All worlds loaded before this plugin have to registered manually.
        for (World world : this.getServer().getWorlds()) {
            WorldPermission.register(world.getName(), this.getServer().getPluginManager());
        }

        this.getServer().getPluginManager().registerEvent(Event.Type.WORLD_LOAD, worldListener, Priority.Low, this);
        try {
            this.getServer().getPluginManager().registerEvent(Event.Type.WORLD_UNLOAD, worldListener, Priority.Low, this);
        } catch (NoSuchFieldError e) {
            // No unload on server: No problem at all. Since 834/835 there.
        }
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, new XWEntityListener(properties, warpManager.getWarmUp()), Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Priority.Low, this);
        XWarp.logger.enableMsg();
    }

    private void updateFiles() {
        File file = new File("MyWarp", "warps.db");
        File folder = new File("MyWarp");
        file.renameTo(new File(this.getDataFolder(), "warps.db"));
        folder.delete();
    }
}
