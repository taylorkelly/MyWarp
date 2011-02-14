package me.taylorkelly.mywarp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import me.taylorkelly.mywarp.Warp.Visibility;

public class WarpDataSource {
	public final static String DATABASE = "jdbc:sqlite:homes-warps.db";
	private final static String WARP_TABLE = "CREATE TABLE `warpTable` ("
			+ "`id` INTEGER PRIMARY KEY,"
			+ "`name` varchar(32) NOT NULL DEFAULT 'warp',"
			+ "`creator` varchar(32) NOT NULL DEFAULT 'Player',"
			+ "`world` varchar(32) NOT NULL,"
			+ "`x` DPUBLE NOT NULL DEFAULT '0',"
			+ "`y` tinyint NOT NULL DEFAULT '0',"
			+ "`z` DOUBLE NOT NULL DEFAULT '0',"
			+ "`yaw` smallint NOT NULL DEFAULT '0',"
			+ "`pitch` smallint NOT NULL DEFAULT '0',"
			+ "`publicLevel` smallint NOT NULL DEFAULT '1',"
			+ "`permissions` varchar(150) NOT NULL DEFAULT '',"
			+ "`welcomeMessage` varchar(100) NOT NULL DEFAULT ''" + ");";
	
	private final static String VERSION_TABLE = "CREATE TABLE `meta` (`name` varchar(32) NOT NULL, `value` int NOT NULL);";

	private final static int TARGET_VERSION = 1;
	
	public static void initialize(Server server) {
		int version = getVersion();
		
		if (version < TARGET_VERSION) {
			MyWarp.logger.info("Database layout is outdated (" + version + ")! Updating to " + TARGET_VERSION + ".");
			Statement statement = null;
			PreparedStatement convertedWarp = null;
			ResultSet set = null;
			try {
				Connection conn = ConnectionManager.getConnection();
				statement = conn.createStatement();
				
				// Copy old database
				if (tableExists("warpTable")) {
					// Backup it
					statement.execute("ALTER TABLE warpTable RENAME TO warpTable_backup");
					MyWarp.logger.info("Backuping old database.");
				}
				// Create new database
				statement.executeUpdate(WARP_TABLE);
				if (tableExists("warpTable_backup")) {
					// Select line by line
					String world = server.getWorlds().get(0).getName();
					set = statement.executeQuery("SELECT * FROM warpTable_backup");
					convertedWarp = conn.prepareStatement("INSERT INTO warpTable (id, name, creator, world, x, y, z, yaw, pitch, publicLevel, permissions, welcomeMessage) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
					while (set.next()) {
						convertedWarp.setInt(1, set.getInt("id"));
						convertedWarp.setString(2, set.getString("name"));
						convertedWarp.setString(3, set.getString("creator"));
						convertedWarp.setString(4, world);						
						convertedWarp.setDouble(5, set.getDouble("x"));
						convertedWarp.setInt(6, set.getInt("y"));
						convertedWarp.setDouble(7, set.getDouble("z"));
						convertedWarp.setInt(8, set.getInt("yaw"));
						convertedWarp.setInt(9, set.getInt("pitch"));
						if (version < 0) {
							if (set.getBoolean("publicAll")) {
								convertedWarp.setInt(10, 1);
							} else {
								convertedWarp.setInt(10, 0);
							}
						} else {
							convertedWarp.setInt(10, set.getInt("publicLevel"));
						}
						convertedWarp.setString(11, set.getString("permissions"));
						convertedWarp.setString(12, set.getString("welcomeMessage"));
						convertedWarp.executeUpdate();						
					}
					statement.executeUpdate("DROP TABLE warpTable_backup");
					MyWarp.logger.info("Recovering the backup.");
				}
				if (version < 0) {
				statement.executeUpdate("INSERT INTO meta (name, value) VALUES (\"version\", " + TARGET_VERSION + ")");
				} else {
					statement.executeUpdate("UPDATE meta SET value = " + TARGET_VERSION + " WHERE name = \"version\"");
				}
				conn.commit();
			} catch (SQLException ex) {
//				try {
//					statement.execute("ROLLBACK");
//				} catch (SQLException e) {
//					MyWarp.logger.severe("Unable to rollback changes!");
//				}
				MyWarp.logger.log(Level.SEVERE, "Warp Load Exception", ex);
			} finally {
				try {
					if (statement != null)
						statement.close();
					if (set != null)
						set.close();
				} catch (SQLException ex) {
					MyWarp.logger.severe("Warp Load Exception (on close)");
				}
			}
		}
	}

	public static void getMap(Map<String, Warp> global, Map<String, Map<String, Warp>> personal, Server server) {
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
				World world = server.getWorld(set.getString("world"));
				double x = set.getDouble("x");
				int y = set.getInt("y");
				double z = set.getDouble("z");
				int yaw = set.getInt("yaw");
				int pitch = set.getInt("pitch");
				Location loc = new Location(world, x, y, z, yaw, pitch);
				Visibility visibility = Visibility.parseLevel(set.getInt("publicLevel"));
				String permissions = set.getString("permissions");
				String welcomeMessage = set.getString("welcomeMessage");
				Warp warp = new Warp(index, name, creator, loc, visibility, permissions, welcomeMessage);
				if (visibility == Visibility.GLOBAL || !global.containsKey(name.toLowerCase())) {
					global.put(name.toLowerCase(), warp);
					if (visibility == Visibility.GLOBAL) {
						globalSize++;
					}
				}
				WarpList.putIntoPersonal(personal, warp);
			}
			MyWarp.logger.info(size + " warps loaded (" + globalSize + " global)");
		} catch (SQLException ex) {
			MyWarp.logger.severe("Warp Load Exception", ex);
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (set != null)
					set.close();
			} catch (SQLException ex) {
				MyWarp.logger.severe("Warp Load Exception (on close)");
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
				MyWarp.logger.info("Meta table doesn't exists... Creating new");
				statement.executeUpdate(VERSION_TABLE);
				conn.commit();
				version = -1;
			}
		} catch (SQLException ex) {
			MyWarp.logger.log(Level.SEVERE, "Table Check Exception", ex);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (set != null)
					set.close();
			} catch (SQLException ex) {
				MyWarp.logger.severe("Table Check SQL Exception (on closing)");
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
			MyWarp.logger.log(Level.SEVERE, "Table Check Exception", ex);
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {
				MyWarp.logger.severe("Table Check SQL Exception (on closing)");
			}
		}
	}

//	private static void createTable() {
//		Statement st = null;
//		try {
//			st = ConnectionManager.getConnection().createStatement();
//			st.executeUpdate(WARP_TABLE);
//		} catch (SQLException e) {
//			MyWarp.logger.log(Level.SEVERE, "Create Table Exception", e);
//		} finally {
//			try {
//				if (st != null)
//					st.close();
//			} catch (SQLException e) {
//				MyWarp.logger.severe("Could not create the table (on close)");
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
			setLocation(warp.getLocation(), 4, ps);
			ps.setInt(10, warp.visibility.level);
			ps.setString(11, warp.permissionsString());
			ps.setString(12, warp.welcomeMessage);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			MyWarp.logger.log(Level.SEVERE, "Warp Insert Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex) {
				MyWarp.logger.log(Level.SEVERE, "Warp Insert Exception (on close)", ex);
			}
		}
	}
	
	public static void setLocation(Location location, int offset, PreparedStatement ps) throws SQLException {
		ps.setString(offset++, location.getWorld().getName());
		ps.setDouble(offset++, location.getX());
		ps.setInt(offset++, (int) location.getY());
		ps.setDouble(offset++, location.getZ());
		ps.setInt(offset++, (int) location.getYaw());
		ps.setInt(offset++, (int) location.getPitch());
	}
	
	public static void updateWarp(Warp warp) {
		PreparedStatement ps = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			
			ps = conn.prepareStatement("UPDATE warpTable SET world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE id = ?");
			Location loc = warp.getLocation();
			setLocation(loc, 1, ps);
			ps.setInt(7, warp.index);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			MyWarp.logger.log(Level.SEVERE, "Warp Update Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex) {
				MyWarp.logger.log(Level.SEVERE, "Warp Insert Exception (on close)", ex);
			}
		}
	}
	
	public static void updateName(Warp warp) {
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement("UPDATE warpTable SET name = ? WHERE id = ?");
			ps.setString(1, warp.name);
			ps.setInt(2, warp.index);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			MyWarp.logger.log(Level.SEVERE, "Warp Name Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				MyWarp.logger.log(Level.SEVERE, "Warp Name Exception (on close)", ex);
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
			MyWarp.logger.log(Level.SEVERE, "Warp Welcome Message Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				MyWarp.logger.log(Level.SEVERE, "Warp Welcome Message Exception (on close)", ex);
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
			MyWarp.logger.log(Level.SEVERE, "Warp Visibility Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				MyWarp.logger.log(Level.SEVERE, "Warp Visibility (on close)", ex);
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
			MyWarp.logger.log(Level.SEVERE, "Warp Permissions Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				MyWarp.logger.log(Level.SEVERE, "Warp Permissions Exception (on close)", ex);
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
			MyWarp.logger.log(Level.SEVERE, "Warp Creator Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				MyWarp.logger.log(Level.SEVERE,	"Warp Creator Exception (on close)", ex);
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
			MyWarp.logger.log(Level.SEVERE, "Warp Delete Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
			} catch (SQLException ex) {
				MyWarp.logger.log(Level.SEVERE, "Warp Delete Exception (on close)", ex);
			}
		}
	}
}
