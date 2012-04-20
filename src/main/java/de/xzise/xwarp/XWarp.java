package de.xzise.xwarp;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
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

    public String name;
    public String version;
    private boolean enableCanceled = true;

    public XWarp() {
        super();
    }

    @Override
    public void onDisable() {
        if (!this.enableCanceled) {
            this.dataConnection.free();
            XWarp.logger.disableMsg();
        }
    }

    private void disable(String message) {
        this.enableCanceled = true;
        this.getServer().getLogger().severe("[" + this.getDescription().getName() + "] " + message);
        this.getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onEnable() {
        try {
            if (MinecraftUtil.needUpdate(1, 3)) {
                this.disable("You need to update Bukkit Plugin Utilities to at least 1.3.0!");
                return;
            }
        } catch (NoSuchMethodError e) {
            this.disable("You need to update Bukkit Plugin Utilities to at least 1.3.0!");
            return;
        } catch (NoClassDefFoundError e) {
            this.disable("No Bukkit Plugin Utilities found!");
            return;
        }
        this.enableCanceled = false;
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

        this.permissionHandler = new PermissionsHandler(this.getServer().getPluginManager(), this.getServer().getServicesManager(), properties.getPermissionsPlugin(), logger);
        permissions = this.permissionHandler;
        this.economyHandler = new EconomyHandler(this.getServer().getPluginManager(), this.getServer().getServicesManager(), properties.getEconomyPlugin(), properties.getEconomyBaseAccount(), logger);

        WarpManager warpManager = new WarpManager(this, this.economyHandler, properties, this.dataConnection);
        WPAManager wpaManager = new WPAManager(this, this.dataConnection, properties);

        warpManager.setWPAManager(wpaManager);

        XWServerListener serverListener = new XWServerListener(properties, this.getServer().getPluginManager(), warpManager, this.permissionHandler, this.economyHandler);

        // Create commands
        WarpCommandMap wcm = null;
        WPACommandMap wpacm = null;
        ManageCommandMap mcm = null;
        try {
            wcm = new WarpCommandMap(warpManager, this.economyHandler, this.getServer(), this.dataConnection, this.getDataFolder(), properties);
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

        XWPlayerListener playerListener = new XWPlayerListener(warpManager, properties, wpacm.createCommand);
        XWBlockListener blockListener = new XWBlockListener(warpManager);
        XWWorldListener worldListener = new XWWorldListener(this.getServer().getPluginManager(), warpManager, wpaManager);

        // Unless an event is called, to tell all enabled plugins
        serverListener.load();

        // All worlds loaded before this plugin have to registered manually.
        for (World world : this.getServer().getWorlds()) {
            WorldPermission.register(world.getName(), this.getServer().getPluginManager());
        }

        serverListener.register(this);
        registerEvents(this, worldListener, playerListener, blockListener, new XWEntityListener(properties, warpManager.getWarmUp()));
        XWarp.logger.enableMsg();
    }

    private static void registerEvents(final Plugin plugin, final Listener... listeners) {
        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    private void updateFiles() {
        File file = new File("MyWarp", "warps.db");
        File folder = new File("MyWarp");
        file.renameTo(new File(this.getDataFolder(), "warps.db"));
        folder.delete();
    }
}
