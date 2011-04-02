package de.xzise.xwarp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Server;

import me.taylorkelly.mywarp.MyWarp;

import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.HModConnection;
import de.xzise.xwarp.dataconnections.SQLiteConnection;

public class PluginProperties {

    private DataConnection dataConnection;
    private boolean cooldownNotify;
    private boolean warmupNotify;

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

    public boolean isCooldownNotify() {
        return cooldownNotify;
    }

    public boolean isWarmupNotify() {
        return warmupNotify;
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

        cooldownNotify = parseString(properties.getProperty("cooldown-notify", "true"));
        cooldownNotify = parseString(properties.getProperty("warmup-notify", "true"));
    }

    public static boolean parseString(String string) {
        if (string.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

}
