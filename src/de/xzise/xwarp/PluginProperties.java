package de.xzise.xwarp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Server;

import me.taylorkelly.mywarp.MyWarp;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.HModConnection;
import de.xzise.xwarp.dataconnections.SQLiteConnection;

public class PluginProperties {

    private DataConnection dataConnection;
    private boolean cooldownNotify;
    private boolean warmupNotify;
    private boolean useForceTo;
    private boolean showFreePriceMessage;
    private boolean cancelWarmUpOnDamage;
    private boolean cancelWarmUpOnMovement;
    private boolean createUpdates;
    private String economyPlugin;
    private String economyBaseAccount;

    private File dataDirectory;
    private File configFile;
    private Server server;

    public PluginProperties(File dataDirectory, Server server) {
        this.dataDirectory = dataDirectory;
        this.configFile = new File(this.dataDirectory, "config.properties");
        this.server = server;
        this.read();
    }

    public DataConnection getDataConnection() {
        return this.dataConnection;
    }
    
    public String getEconomyPlugin() {
        return this.economyPlugin;
    }
    
    public String getEconomyBaseAccount() {
        return this.economyBaseAccount;
    }

    public boolean isCooldownNotify() {
        return this.cooldownNotify;
    }

    public boolean isWarmupNotify() {
        return this.warmupNotify;
    }

    public boolean isForceToUsed() {
        return this.useForceTo;
    }

    public boolean isCancelWarmUpOnDamage() {
        return this.cancelWarmUpOnDamage;
    }

    public boolean isCancelWarmUpOnMovement() {
        return this.cancelWarmUpOnMovement;
    }

    public boolean showFreePriceMessage() {
        return this.showFreePriceMessage;
    }
    
    public boolean isCreationUpdating() {
        return this.createUpdates;
    }
    
    public void read() {
        java.util.Properties properties = new java.util.Properties();
        if (this.configFile.exists()) {
            try {
                BufferedInputStream stream = new BufferedInputStream(new FileInputStream(this.configFile));
                properties.load(stream);
                stream.close();
            } catch (IOException e) {
                MyWarp.logger.warning("Unable to load properties file.", e);
            }
        } else {
            try {
                properties.setProperty("data-connection", "sqlite");
                properties.setProperty("cooldown-notify", "true");
                properties.setProperty("warmup-notify", "true");
                properties.setProperty("use-force-to", "true");
                properties.setProperty("show-free-price-message", "true");
                properties.setProperty("cancel-warm-up-on-damage", "true");
                properties.setProperty("cancel-warm-up-on-movement", "false");
                properties.setProperty("economy", "");
                properties.setProperty("economy-base-account", "");
                properties.setProperty("update-if-exists", "false");
                properties.store(new FileWriter(this.configFile), null);
            } catch (IOException e) {
                MyWarp.logger.warning("Unable to create properties file.", e);
            }
        }
        String dataConnectionProperty = properties.getProperty("data-connection", "sqlite");

        if (dataConnectionProperty.equalsIgnoreCase("hmod")) {
            this.dataConnection = new HModConnection(this.server);
        } else {
            if (!dataConnectionProperty.equalsIgnoreCase("sqlite")) {
                MyWarp.logger.warning("Unrecognized data-connection selected (" + dataConnectionProperty + ")");
            }
            // Per default sqlite
            this.dataConnection = new SQLiteConnection(server);
        }

        this.economyPlugin = properties.getProperty("economy", "");
        this.economyBaseAccount = properties.getProperty("economy-base-account", "");
        
        this.cooldownNotify = parseString(properties.getProperty("cooldown-notify"), true);
        this.warmupNotify = parseString(properties.getProperty("warmup-notify"), true);
        this.useForceTo = parseString(properties.getProperty("use-force-to"), true);
        this.showFreePriceMessage = parseString(properties.getProperty("show-free-price-message"), true);
        this.cancelWarmUpOnDamage = parseString(properties.getProperty("cancel-warm-up-on-damage"), true);
        this.cancelWarmUpOnMovement = parseString(properties.getProperty("cancel-warm-up-on-movement"), false);
        this.createUpdates = parseString(properties.getProperty("update-if-exists"), false);
    }

    public static boolean parseString(String string, boolean def) {
        if (MinecraftUtil.isSet(string)) {
            return string.equalsIgnoreCase("true");
        } else {
            return def;
        }
    }
}
