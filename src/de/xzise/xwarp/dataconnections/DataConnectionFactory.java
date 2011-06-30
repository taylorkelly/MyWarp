package de.xzise.xwarp.dataconnections;

import org.bukkit.Server;

public final class DataConnectionFactory {

    private DataConnectionFactory() {}
    
    public static DataConnection getConnection(Server server, String type) {
        if (type.equalsIgnoreCase("sqlite")) {
            return new SQLiteConnection(server);
        } else if (type.equalsIgnoreCase("hmod")) {
            return new HModConnection(server);
        } else if (type.equalsIgnoreCase("yml")) {
            ;
        } else {
            return null;
        }
    }
    
}
