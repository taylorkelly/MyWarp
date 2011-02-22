package me.taylorkelly.mywarp;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionManager {
    private static Connection conn;

    public static Connection initialize(File dataFolder) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder.getAbsolutePath() + "/warps.db");
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException ex) {
            MyWarp.severe("SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            MyWarp.severe("You need the SQLite library.", ex);
        }
        return conn;
    }

    public static Connection getConnection() {
        return conn;
    }

    public static void closeConnection() {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                MyWarp.severe("Error on Connection close", ex);
            }
        }
    }


}
