package de.xzise.xwarp.dataconnections;

import org.bukkit.Server;

import de.xzise.xwarp.Warp.Visibility;

public final class DataConnections {

    private DataConnections() {}

    public static byte getPublicLevel(boolean listed, Visibility visibility) {
        return (byte) ((visibility.level & 0x7F) | (listed ? 0 : 1) << 7);
    }

    public static Visibility parseVisibility(int value) {
        return Visibility.getByLevel((byte) (value & 0x7F));
    }

    public static boolean isListed(int value) {
        return (value & 0x80) == 0;
    }
    public static DataConnection getConnection(Server server, String type) {
        if (type.equalsIgnoreCase("sqlite")) {
            return new SQLiteConnection(server);
        } else if (type.equalsIgnoreCase("hmod")) {
            return new HModConnection(server);
        } else if (type.equalsIgnoreCase("yml")) {
            return new YmlConnection();
        } else {
            return null;
        }
    }

}
