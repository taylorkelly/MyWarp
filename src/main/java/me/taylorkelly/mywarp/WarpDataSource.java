package me.taylorkelly.mywarp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WarpDataSource {
    public final static String DATABASE = "jdbc:sqlite:homes-warps.db";
    
    private final static String WARP_TABLE = "CREATE TABLE `warpTable` (" + "`id` INTEGER PRIMARY KEY," + "`name` varchar(32) NOT NULL DEFAULT 'warp',"
            + "`creator` varchar(32) NOT NULL DEFAULT 'Player'," + "`world` tinyint NOT NULL DEFAULT '0'," + "`x` DOUBLE NOT NULL DEFAULT '0',"
            + "`y` tinyint NOT NULL DEFAULT '0'," + "`z` DOUBLE NOT NULL DEFAULT '0'," + "`yaw` smallint NOT NULL DEFAULT '0',"
            + "`pitch` smallint NOT NULL DEFAULT '0'," + "`publicAll` boolean NOT NULL DEFAULT '1'," + "`permissions` varchar(150) NOT NULL DEFAULT '',"
            + "`welcomeMessage` varchar(100) NOT NULL DEFAULT ''" + ");";

    public static void initialize() {
        if (!tableExists()) {
            createTable();
        }
    }

    public static HashMap<String, Warp> getMap() {
        HashMap<String, Warp> ret = new HashMap<String, Warp>();
        Statement statement = null;
        ResultSet set = null;
        Logger log = Logger.getLogger("Minecraft");
        try {
            Connection conn = ConnectionManager.getConnection();

            statement = conn.createStatement();
            set = statement.executeQuery("SELECT * FROM warpTable");
            int size = 0;
            while (set.next()) {
                size++;
                int index = set.getInt("id");
                String name = set.getString("name");
                String creator = set.getString("creator");
                String world = set.getString("world");
                double x = set.getDouble("x");
                int y = set.getInt("y");
                double z = set.getDouble("z");
                int yaw = set.getInt("yaw");
                int pitch = set.getInt("pitch");
                boolean publicAll = set.getBoolean("publicAll");
                String permissions = set.getString("permissions");
                String welcomeMessage = set.getString("welcomeMessage");
                Warp warp = new Warp(index, name, creator, world, x, y, z, yaw, pitch, publicAll, permissions, welcomeMessage);
                ret.put(name, warp);
            }
            log.info("[MYWARP]: " + size + " warps loaded");
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "[MYWARP]: Warp Load Exception");
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (set != null)
                    set.close();
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "[MYWARP]: Warp Load Exception (on close)");
            }
        }
        return ret;
    }

    private static boolean tableExists() {
        ResultSet rs = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, "warpTable", null);
            if (!rs.next())
                return false;
            return true;
        } catch (SQLException ex) {
            Logger log = Logger.getLogger("Minecraft");
            log.log(Level.SEVERE, "[MYWARP]: Table Check Exception", ex);
            return false;
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException ex) {
                Logger log = Logger.getLogger("Minecraft");
                log.log(Level.SEVERE, "[MYWARP]: Table Check SQL Exception (on closing)");
            }
        }
    }

    private static void createTable() {
        Statement st = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            st = conn.createStatement();
            st.executeUpdate(WARP_TABLE);
            conn.commit();
        } catch (SQLException e) {
            Logger log = Logger.getLogger("Minecraft");
            log.log(Level.SEVERE, "[MYWARP]: Create Table Exception", e);
        } finally {
            try {
                if (st != null)
                    st.close();
            } catch (SQLException e) {
                Logger log = Logger.getLogger("Minecraft");
                log.log(Level.SEVERE, "[MYWARP]: Could not create the table (on close)");
            }
        }
    }

    public static void addWarp(Warp warp) {
        PreparedStatement ps = null;
        Logger log = Logger.getLogger("Minecraft");
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn
                    .prepareStatement("INSERT INTO warpTable (id, name, creator, world, x, y, z, yaw, pitch, publicAll, permissions, welcomeMessage) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, warp.index);
            ps.setString(2, warp.name);
            ps.setString(3, warp.creator);
            ps.setString(4, warp.world);
            ps.setDouble(5, warp.x);
            ps.setInt(6, warp.y);
            ps.setDouble(7, warp.z);
            ps.setInt(8, warp.yaw);
            ps.setInt(9, warp.pitch);
            ps.setBoolean(10, warp.publicAll);
            ps.setString(11, warp.permissionsString());
            ps.setString(12, warp.welcomeMessage);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "[MYWARP]: Warp Insert Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "[MYWARP]: Warp Insert Exception (on close)", ex);
            }
        }
    }

    public static void deleteWarp(Warp warp) {
        PreparedStatement ps = null;
        ResultSet set = null;
        Logger log = Logger.getLogger("Minecraft");
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("DELETE FROM warpTable WHERE id = ?");
            ps.setInt(1, warp.index);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "[MYWARP]: Warp Delete Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "[MYWARP]: Warp Delete Exception (on close)", ex);
            }
        }
    }

    public static void publicizeWarp(Warp warp, boolean publicAll) {
        PreparedStatement ps = null;
        ResultSet set = null;
        Logger log = Logger.getLogger("Minecraft");
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("UPDATE warpTable SET publicAll = ? WHERE id = ?");
            ps.setBoolean(1, publicAll);
            ps.setInt(2, warp.index);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "[MYWARP]: Warp Publicize Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "[MYWARP]: Warp Publicize Exception (on close)", ex);
            }
        }
    }

    public static void updatePermissions(Warp warp) {
        PreparedStatement ps = null;
        ResultSet set = null;
        Logger log = Logger.getLogger("Minecraft");
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("UPDATE warpTable SET permissions = ? WHERE id = ?");
            ps.setString(1, warp.permissionsString());
            ps.setInt(2, warp.index);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "[MYWARP]: Warp Permissions Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "[MYWARP]: Warp Permissions Exception (on close)", ex);
            }
        }
    }

    public static void updateCreator(Warp warp) {
        PreparedStatement ps = null;
        ResultSet set = null;
        Logger log = Logger.getLogger("Minecraft");
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("UPDATE warpTable SET creator = ? WHERE id = ?");
            ps.setString(1, warp.creator);
            ps.setInt(2, warp.index);
            ps.executeUpdate();
            conn.commit();

        } catch (SQLException ex) {
            log.log(Level.SEVERE, "[MYWARP]: Warp Creator Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "[MYWARP]: Warp Creator Exception (on close)", ex);
            }
        }
    }

    public static void updateWelcomeMessage(Warp warp) {
        PreparedStatement ps = null;
        ResultSet set = null;
        Logger log = Logger.getLogger("Minecraft");
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("UPDATE warpTable SET welcomeMessage = ? WHERE id = ?");
            ps.setString(1, warp.welcomeMessage);
            ps.setInt(2, warp.index);
            ps.executeUpdate();
            conn.commit();

        } catch (SQLException ex) {
            log.log(Level.SEVERE, "[MYWARP]: Warp Creator Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "[MYWARP]: Warp Creator Exception (on close)", ex);
            }
        }
    }

}
