package me.taylorkelly.mywarp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import de.xzise.DatabaseConnection;

public class ConnectionManager {

    private static Connection connection;
    private static boolean own;
    private static final String[] plugins = new String[] {"MyHome", "MyWarp"};

    public static synchronized Connection initializeConnection(Server server) {
        if (connection == null) {
        	for (String name : plugins) {
				Plugin test = server.getPluginManager().getPlugin(name);
				if (test != null && test.isEnabled() && test instanceof DatabaseConnection) {
					connection = ((DatabaseConnection) test).getConnection();
					if (connection != null) {
						MyWarp.logger.info("Connection with " + name + " established.");
						return connection;
					}
				}
			}
        	own = true;
        	connection = createConnection();
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
        if (connection != null && own) {
        	MyWarp.logger.info("Close connection!");
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
        	connection = null;
        }
    }
}
