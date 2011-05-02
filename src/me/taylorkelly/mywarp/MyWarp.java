package me.taylorkelly.mywarp;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.xwarp.CommandMap;
import de.xzise.xwarp.EconomyWrapper;
import de.xzise.xwarp.PermissionWrapper;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.listeners.XWBlockListener;
import de.xzise.xwarp.listeners.XWEntityListener;
import de.xzise.xwarp.listeners.XWPlayerListener;
import de.xzise.xwarp.listeners.XWWorldListener;

public class MyWarp extends JavaPlugin {

    public static PermissionWrapper permissions = new PermissionWrapper();
    public static XLogger logger;

    private EconomyWrapper economyWrapper = new EconomyWrapper();
    private PermissionWrapper permissionsWrapper = permissions;

    private CommandMap commands;
    private DataConnection dataConnection;

    public String name;
    public String version;

    public MyWarp() {
        super();
    }

    @Override
    public void onDisable() {
        this.dataConnection.free();
    }

    @Override
    public void onEnable() {
        this.name = this.getDescription().getName();
        this.version = this.getDescription().getVersion();
        logger = new XLogger(this);

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        if (new File("MyWarp").exists() && new File("MyWarp", "warps.db").exists()) {
            this.updateFiles();
        } else {
            File old = new File("homes-warps.db");
            File newFile = new File(this.getDataFolder(), "warps.db");
            if (old.exists() && !newFile.exists()) {
                MyWarp.logger.info("No database found. Copying old database.");
                try {
                    MinecraftUtil.copy(old, newFile);
                } catch (IOException e) {
                    MyWarp.logger.severe("Unable to copy database", e);
                }
            }
        }

        PluginProperties properties = new PluginProperties(this.getDataFolder(), this.getServer());
        
        this.dataConnection = properties.getDataConnection();
        try {
            if (!this.dataConnection.load(new File(this.getDataFolder(), this.dataConnection.getFilename()))) {
                MyWarp.logger.severe("Could not load data. Disabling " + this.name + "!");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } catch (Exception e) {
            MyWarp.logger.severe("Could not load data. Disabling " + this.name + "!", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        WarpManager warpManager = new WarpManager(this, this.economyWrapper, properties, this.dataConnection);

        // Create commands
        this.commands = null;
        try {
            this.commands = new CommandMap(warpManager, this.economyWrapper, this.getServer(), this.dataConnection, this.getDataFolder(), properties);
        } catch (IllegalArgumentException iae) {
            MyWarp.logger.severe("Couldn't initalize commands. Disabling " + this.name + "!", iae);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getCommand("go").setExecutor(this.commands.getCommand(""));

        XWPlayerListener playerListener = new XWPlayerListener(warpManager, properties);
        XWBlockListener blockListener = new XWBlockListener(warpManager);
        ServerListener serverListner = new ServerListener() {
            @Override
            public void onPluginEnable(PluginEnableEvent event) {
                String name = event.getPlugin().getDescription().getName();
                if (name.equals("Permissions")) {
                    MyWarp.this.permissionsWrapper.init(event.getPlugin());
                } else if (name.equals("iConomy")) {
                    MyWarp.this.economyWrapper.init(event.getPlugin());
                }
            }

            @Override
            public void onPluginDisable(PluginDisableEvent event) {
                String name = event.getPlugin().getDescription().getName();
                if (name.equals("Permissions")) {
                    MyWarp.this.permissionsWrapper.init(null);
                } else if (name.equals("iConomy")) {
                    MyWarp.this.economyWrapper.init(null);
                }
            }
        };

        // Unless an event is called, to tell all enabled plugins
        this.permissionsWrapper.init(this.getServer().getPluginManager().getPlugin("Permissions"));
        this.economyWrapper.init(this.getServer().getPluginManager().getPlugin("iConomy"));
        
        this.getServer().getPluginManager().registerEvent(Event.Type.WORLD_LOAD, new XWWorldListener(warpManager), Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, new XWEntityListener(properties, warpManager.getWarmUp()), Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, serverListner, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, serverListner, Priority.Low, this);
        MyWarp.logger.info(name + " " + version + " enabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // workaround until I get the complete line or a parsed one
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            line.append(arg);
            if (i < args.length - 1) {
                line.append(' ');
            }
        }

        return this.commands.executeCommand(sender, MinecraftUtil.parseLine(line.toString()));
    }

    private void updateFiles() {
        File file = new File("MyWarp", "warps.db");
        File folder = new File("MyWarp");
        file.renameTo(new File(this.getDataFolder(), "warps.db"));
        folder.delete();
    }
}
