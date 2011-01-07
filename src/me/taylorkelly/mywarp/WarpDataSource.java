package me.taylorkelly.mywarp;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WarpDataSource {
	private final static String DATABASE = "jdbc:sqlite:" + MyWarp.name + File.separator + "warps.db";
	private final static String WARP_TABLE = "CREATE TABLE `warpTable` (" + "`id` INTEGER PRIMARY KEY," + "`name` varchar(32) NOT NULL DEFAULT 'warp',"
			+ "`creator` varchar(32) NOT NULL DEFAULT 'Player'," + "`world` tinyint NOT NULL DEFAULT '0'," + "`x` int NOT NULL DEFAULT '0',"
			+ "`y` tinyint NOT NULL DEFAULT '0'," + "`z` int NOT NULL DEFAULT '0'," + "`yaw` smallint NOT NULL DEFAULT '0'," + "`pitch` smallint NOT NULL DEFAULT '0'," + "`publicAll` boolean NOT NULL DEFAULT '1',"
			+ "`permissions` varchar(150) NOT NULL DEFAULT ''," + "`welcomeMessage` varchar(100) NOT NULL DEFAULT ''" +");";

	public static void initialize() {
		if (!tableExists()) {
			createTable();
		}
	}

	public static HashMap<String, Warp> getMap() {
		HashMap<String, Warp> ret = new HashMap<String, Warp>();
		Connection conn = null;
		Statement statement = null;
		ResultSet set = null;
		Logger log = Logger.getLogger("Minecraft");
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);

			statement = conn.createStatement();  
			set = statement  
                    .executeQuery("SELECT * FROM warpTable");  
			int size = 0;
            while (set.next()) { 
            	size++;
            	int index = set.getInt("id");
				String name = set.getString("name");
				String creator = set.getString("creator");
				int world = set.getInt("world");
				int x = set.getInt("x");
				int y = set.getInt("y");
				int z = set.getInt("z");
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
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "[MYWARP]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (set != null)
					set.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "[MYWARP]: Warp Load Exception (on close)");
			}
		}
		return ret;
	}

	private static boolean tableExists() {
		Connection conn = null;
		ResultSet rs = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, "warpTable", null);
			if (!rs.next())
				return false;
			return true;
		} catch (SQLException ex) {
			Logger log = Logger.getLogger("Minecraft");
			log.log(Level.SEVERE, "[MYWARP]: Table Check Exception", ex);
			return false;
		} catch (ClassNotFoundException e) {
			Logger log = Logger.getLogger("Minecraft");
			log.log(Level.SEVERE, "[MYWARP]: Error loading org.sqlite.JDBC");
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				Logger log = Logger.getLogger("Minecraft");
				log.log(Level.SEVERE, "[MYWARP]: Table Check SQL Exception (on closing)");
			}
		}
	}

	private static void createTable() {
		Connection conn = null;
		Statement st = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);
			st = conn.createStatement();
			st.executeUpdate(WARP_TABLE);
		} catch (SQLException e) {
			Logger log = Logger.getLogger("Minecraft");
			log.log(Level.SEVERE, "[MYWARP]: Create Table Exception", e);
		} catch (ClassNotFoundException e) {
			Logger log = Logger.getLogger("Minecraft");
			log.log(Level.SEVERE, "[MYWARP]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
				Logger log = Logger.getLogger("Minecraft");
				log.log(Level.SEVERE, "[MYWARP]: Could not create the table (on close)");
			}
		}
	}
	
	public static void addWarp(Warp warp) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		Logger log = Logger.getLogger("Minecraft");
		try {
			Class.forName("org.sqlite.JDBC");  
	        conn = DriverManager.getConnection(DATABASE);

			ps = conn.prepareStatement("INSERT INTO warpTable (id, name, creator, world, x, y, z, yaw, pitch, publicAll, permissions, welcomeMessage) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
        	ps.setInt(1, warp.index);
        	ps.setString(2, warp.name);
        	ps.setString(3, warp.creator);
        	ps.setInt(4, warp.world);
        	ps.setInt(5, warp.x);
        	ps.setInt(6, warp.y);
          	ps.setInt(7, warp.z);
        	ps.setInt(8, warp.yaw);
        	ps.setInt(9, warp.pitch);
        	ps.setBoolean(10, warp.publicAll);
        	ps.setString(11, warp.permissionsString());
        	ps.setString(12, warp.welcomeMessage);
			ps.executeUpdate();
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "[MYWARP]: Warp Insert Exception", ex);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "[MYWARP]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "[MYWARP]: Warp Insert Exception (on close)", ex);
			}
		}
	}

	public static void deleteWarp(Warp warp) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		Logger log = Logger.getLogger("Minecraft");
		try {
			Class.forName("org.sqlite.JDBC");  
	        conn = DriverManager.getConnection(DATABASE);
			ps = conn.prepareStatement("DELETE FROM warpTable WHERE id = ?");
        	ps.setInt(1, warp.index);
			ps.executeUpdate();
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "[MYWARP]: Warp Delete Exception", ex);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "[MYWARP]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "[MYWARP]: Warp Delete Exception (on close)", ex);
			}
		}		
	}

	public static void publicizeWarp(Warp warp, boolean publicAll) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		Logger log = Logger.getLogger("Minecraft");
		try {
			Class.forName("org.sqlite.JDBC");  
	        conn = DriverManager.getConnection(DATABASE);
			ps = conn.prepareStatement("UPDATE warpTable SET publicAll = ? WHERE id = ?");
        	ps.setBoolean(1, publicAll);
        	ps.setInt(2, warp.index);
			ps.executeUpdate();
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "[MYWARP]: Warp Publicize Exception", ex);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "[MYWARP]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "[MYWARP]: Warp Publicize Exception (on close)", ex);
			}
		}
	}

	public static void updatePermissions(Warp warp) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		Logger log = Logger.getLogger("Minecraft");
		try {
			Class.forName("org.sqlite.JDBC");  
	        conn = DriverManager.getConnection(DATABASE);
			ps = conn.prepareStatement("UPDATE warpTable SET permissions = ? WHERE id = ?");
        	ps.setString(1, warp.permissionsString());
        	ps.setInt(2, warp.index);
			ps.executeUpdate();
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "[MYWARP]: Warp Permissions Exception", ex);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "[MYWARP]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "[MYWARP]: Warp Permissions Exception (on close)", ex);
			}
		}
	}

}
