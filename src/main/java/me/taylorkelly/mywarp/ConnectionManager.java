package me.taylorkelly.mywarp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.taylorkelly.myhome.MyHome;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public class ConnectionManager {

    private static Connection connection;

    public static synchronized Connection initializeConnection(Server server) {
        if (connection == null) {
            Plugin test = server.getPluginManager().getPlugin("MyHome");
            if (test != null && test.isEnabled()) {
                Logger log = Logger.getLogger("Minecraft");
                connection = MyHome.getConnection();
                log.log(Level.INFO, "[MYWARP] Connection with MyHome established.");
            } else {
                connection = createConnection();
            }
        }
        return connection;
    }

    public static synchronized Connection getConnection() {
        return connection;
    }

    private static Connection createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection ret = DriverManager.getConnection(WarpDataSource.DATABASE);
            ret.setAutoCommit(false);
            return ret;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized void freeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
