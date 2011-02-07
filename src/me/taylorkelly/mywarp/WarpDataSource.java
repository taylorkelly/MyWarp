package me.taylorkelly.mywarp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;

import me.taylorkelly.mywarp.Warp.Visibility;

import com.bukkit.xzise.XLogger;

public class WarpDataSource {
	public final static String DATABASE = "jdbc:sqlite:homes-warps.db";
	private final static String WARP_TABLE = "CREATE TABLE `warpTable` ("
			+ "`id` INTEGER PRIMARY KEY,"
			+ "`name` varchar(32) NOT NULL DEFAULT 'warp',"
			+ "`creator` varchar(32) NOT NULL DEFAULT 'Player',"
			+ "`world` tinyint NOT NULL DEFAULT '0',"
			+ "`x` DPUBLE NOT NULL DEFAULT '0',"
			+ "`y` tinyint NOT NULL DEFAULT '0',"
			+ "`z` DOUBLE NOT NULL DEFAULT '0',"
			+ "`yaw` smallint NOT NULL DEFAULT '0',"
			+ "`pitch` smallint NOT NULL DEFAULT '0',"
			+ "`publicLevel` smallint NOT NULL DEFAULT '1',"
			+ "`permissions` varchar(150) NOT NULL DEFAULT '',"
			+ "`welcomeMessage` varchar(100) NOT NULL DEFAULT ''" + ");";
	
	private final static String VERSION_TABLE = "CREATE TABLE `meta` (`name` varchar(32) NOT NULL, `value` int NOT NULL);";

	private final static int TARGET_VERSION = 0;
	
	public static void initialize() {
		int version = getVersion();
		
		if (version < TARGET_VERSION) {
			XLogger.info("Database layout is outdated (" + version + ")! Updating to " + TARGET_VERSION + ".");
			Statement statement = null;
			ResultSet set = null;
			try {
				Connection conn = ConnectionManager.getConnection();
				statement = conn.createStatement();
				
				// Copy old database
				if (tableExists("warpTable")) {
					// Backup it
					statement.execute("ALTER TABLE warpTable RENAME TO warpTable_backup");
					XLogger.info("Backuping old database.");
				}
				// Create new database
				statement.executeUpdate(WARP_TABLE);
				if (tableExists("warpTable_backup")) {
					// Copy back
					statement.executeUpdate("INSERT INTO warpTable SELECT * FROM warpTable_backup");
					statement.executeUpdate("DROP TABLE warpTable_backup");
					XLogger.info("Recovering the backup.");
				}
				statement.executeUpdate("INSERT INTO meta (name, value) VALUES (\"version\", " + TARGET_VERSION + ")");
				conn.commit();
			} catch (SQLException ex) {
//				try {
//					statement.execute("ROLLBACK");
//				} catch (SQLException e) {
//					XLogger.severe("Unable to rollback changes!");
//				}
				XLogger.log(Level.SEVERE, "Warp Load Exception", ex);
			} finally {
				try {
					if (statement != null)
						statement.close();
					if (set != null)
						set.close();
				} catch (SQLException ex) {
					XLogger.severe("Warp Load Exception (on close)");
				}
			}
		}
	}

	public static void getMap(Map<String, Warp> global, Map<String, Map<String, Warp>> personal) {
		Statement statement = null;
		ResultSet set = null;
		try {
			statement = ConnectionManager.getConnection().createStatement();
			set = statement.executeQuery("SELECT * FROM warpTable");
			int size = 0;
			int globalSize = 0;
			while (set.next()) {
				size++;
				int index = set.getInt("id");
				String name = set.getString("name");
				String creator = set.getString("creator");
				int world = set.getInt("world");
				double x = set.getDouble("x");
				int y = set.getInt("y");
				double z = set.getDouble("z");
				int yaw = set.getInt("yaw");
				int pitch = set.getInt("pitch");
				Visibility visibility = Visibility.parseLevel(set.getInt("publicLevel"));
				String permissions = set.getString("permissions");
				String welcomeMessage = set.getString("welcomeMessage");
				Warp warp = new Warp(index, name, creator, world, x, y, z, yaw,
						pitch, visibility, permissions, welcomeMessage);
				if (visibility == Visibility.GLOBAL || !global.containsKey(name.toLowerCase())) {
					global.put(name.toLowerCase(), warp);
					if (visibility == Visibility.GLOBAL) {
						globalSize++;
					}
				}
				WarpList.putIntoPersonal(personal, warp);
			}
			XLogger.info(size + " warps loaded (" + globalSize + " global)");
		} catch (SQLException ex) {
			XLogger.severe("Warp Load Exception", ex);
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (set != null)
					set.close();
			} catch (SQLException ex) {
				XLogger.severe("Warp Load Exception (on close)");
			}
		}
	}
	
	private static int getVersion() {
		Statement statement = null;
		int version = -1;
		ResultSet set = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			statement = conn.createStatement();
			if (tableExists("meta")) {
				set = statement.executeQuery("SELECT * FROM meta WHERE name = \"version\"");

				if (set.next()) {
					version = set.getInt("value");
				}
			} else {
				XLogger.info("Meta table doesn't exists... Creating new");
				statement.executeUpdate(VERSION_TABLE);
				conn.commit();
				version = -1;
			}
		} catch (SQLException ex) {
			XLogger.log(Level.SEVERE, "Table Check Exception", ex);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (set != null)
					set.close();
			} catch (SQLException ex) {
				XLogger.severe("Table Check SQL Exception (on closing)");
			}
		}
		return version;
	}

	private static boolean tableExists(String name) {
		ResultSet rs = null;
		try {
			DatabaseMetaData dbm = ConnectionManager.getConnection().getMetaData();
			rs = dbm.getTables(null, null, name, null);
			if (!rs.next())
				return false;
			return true;
		} catch (SQLException ex) {
			XLogger.log(Level.SEVERE, "Table Check Exception", ex);
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {
				XLogger.severe("Table Check SQL Exception (on closing)");
			}
		}
	}

//	private static void createTable() {
//		Statement st = null;
//		try {
//			st = ConnectionManager.getConnection().createStatement();
//			st.executeUpdate(WARP_TABLE);
//		} catch (SQLException e) {
//			XLogger.log(Level.SEVERE, "Create Table Exception", e);
//		} finally {
//			try {
//				if (st != null)
//					st.close();
//			} catch (SQLException e) {
//				XLogger.severe("Could not create the table (on close)");
//			}
//		}
//	}

	public static void addWarp(Warp warp) {
		PreparedStatement ps = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			
			ps = conn.prepareStatement("INSERT INTO warpTable (id, name, creator, world, x, y, z, yaw, pitch, publicLevel, permissions, welcomeMessage) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setInt(1, warp.index);
			ps.setString(2, warp.name);
			ps.setString(3, warp.creator);
			ps.setInt(4, warp.world);
			ps.setDouble(5, warp.x);
			ps.setInt(6, warp.y);
			ps.setDouble(7, warp.z);
			ps.setInt(8, warp.yaw);
			ps.setInt(9, warp.pitch);
			ps.setInt(10, warp.visibility.level);
			ps.setString(11, warp.permissionsString());
			ps.setString(12, warp.welcomeMessage);
			ps.executeUpdate();
			XLogger.info("ro:" + conn.isReadOnly());
			conn.commit();
		} catch (SQLException ex) {
			XLogger.log(Level.SEVERE, "Warp Insert Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex) {
				XLogger.log(Level.SEVERE, "Warp Insert Exception (on close)", ex);
			}
		}
	}
	
	public static void updateWarp(Warp warp) {
		PreparedStatement ps = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			
			ps = conn.prepareStatement("UPDATE warpTable SET x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE id = ?");
			ps.setDouble(1, warp.x);
			ps.setInt(2, warp.y);
			ps.setDouble(3, warp.z);
			ps.setInt(4, warp.yaw);
			ps.setInt(5, warp.pitch);
			ps.setInt(6, warp.index);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			XLogger.log(Level.SEVERE, "Warp Update Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex) {
				XLogger.log(Level.SEVERE, "Warp Insert Exception (on close)", ex);
			}
		}
	}

	public static void deleteWarp(Warp warp) {
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement("DELETE FROM warpTable WHERE id = ?");
			ps.setInt(1, warp.index);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			XLogger.log(Level.SEVERE, "Warp Delete Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				XLogger.log(Level.SEVERE, "Warp Delete Exception (on close)", ex);
			}
		}
	}
	
	public static void updateMessage(Warp warp) {
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement("UPDATE warpTable SET welcomeMessage = ? WHERE id = ?");
			ps.setString(1, warp.welcomeMessage);
			ps.setInt(2, warp.index);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			XLogger.log(Level.SEVERE, "Warp Welcome Message Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				XLogger.log(Level.SEVERE, "Warp Publicize Exception (on close)", ex);
			}
		}
	}
	
	public static void updateVisibility(Warp warp, Visibility visibility) {
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement("UPDATE warpTable SET publicLevel = ? WHERE id = ?");
			ps.setInt(1, visibility.level);
			ps.setInt(2, warp.index);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			XLogger.log(Level.SEVERE, "Warp Visibility Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				XLogger.log(Level.SEVERE, "Warp Publicize Exception (on close)", ex);
			}
		}
	}

	public static void updatePermissions(Warp warp) {
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement("UPDATE warpTable SET permissions = ? WHERE id = ?");
			ps.setString(1, warp.permissionsString());
			ps.setInt(2, warp.index);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			XLogger.log(Level.SEVERE, "Warp Permissions Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				XLogger.log(Level.SEVERE, "Warp Permissions Exception (on close)", ex);
			}
		}
	}

	public static void updateCreator(Warp warp) {
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement("UPDATE warpTable SET creator = ? WHERE id = ?");
			ps.setString(1, warp.creator);
			ps.setInt(2, warp.index);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			XLogger.log(Level.SEVERE, "Warp Creator Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				XLogger.log(Level.SEVERE,
						"Warp Creator Exception (on close)", ex);
			}
		}
	}

}
