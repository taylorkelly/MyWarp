package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Server;
import org.bukkit.World;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.Warp.Visibility;
import de.xzise.xwarp.editors.Editor;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.WarpPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Table;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.editors.WarpProtectionAreaPermissions;
import de.xzise.xwarp.DefaultWarpObject.EditorPermissionEntry;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.WorldWrapper;

import me.taylorkelly.mywarp.MyWarp;

public class SQLiteConnection implements WarpProtectionConnection {

    public final static String DATABASE = "jdbc:sqlite:homes-warps.db";
    private final static String WARP_TABLE = "CREATE TABLE `warps` (" + "`id` INTEGER PRIMARY KEY," + "`name` varchar(32) NOT NULL," + "`creator` varchar(32) NOT NULL," + "`world` varchar(32) NOT NULL," + "`x` DOUBLE NOT NULL DEFAULT '0'," + "`y` DOUBLE NOT NULL DEFAULT '0'," + "`z` DOUBLE NOT NULL DEFAULT '0'," + "`yaw` smallint NOT NULL DEFAULT '0'," + "`pitch` smallint NOT NULL DEFAULT '0'," + "`publicLevel` smallint NOT NULL DEFAULT '1'," + "`welcomeMessage` varchar(100) DEFAULT NULL," + "`owner` varchar(32) NOT NULL DEFAULT '', " + "`price` DOUBLE NOT NULL DEFAULT '0', " + "`cooldown` INTEGER NOT NULL DEFAULT -1, " + "`warmup` INTEGER NOT NULL DEFAULT -1" + ");";
    private final static String PERMISSIONS_TABLE = "CREATE TABLE `permissions` (" + "`id` INTEGER NOT NULL," + "`editor` varchar(32) NOT NULL," + "`value` INTEGER NOT NULL," + "`type` INTEGER NOT NULL," + "`table` INTEGER NOT NULL" + ");";
    private final static String PROTECTION_AREA_TABLE = "CREATE TABLE `protectionAreas` (" + "`id` INTEGER PRIMARY KEY," + "`name` varchar(32) NOT NULL," + "`creator` varchar(32) NOT NULL," + "`world` varchar(32) NOT NULL," + "`x1` DOUBLE NOT NULL DEFAULT '0'," + "`y1` DOUBLE NOT NULL DEFAULT '0'," + "`z1` DOUBLE NOT NULL DEFAULT '0'," + "`x2` DOUBLE NOT NULL DEFAULT '0'," + "`y2` DOUBLE NOT NULL DEFAULT '0'," + "`z2` DOUBLE NOT NULL DEFAULT '0'," + "`owner` varchar(32) NOT NULL DEFAULT '', " + ");";
    
    private final static String VERSION_TABLE = "CREATE TABLE `meta` (`name` varchar(32) NOT NULL, `value` int NOT NULL);";

    private final static int TARGET_VERSION = 5;

    private Server server;
    private Connection connection;

    public SQLiteConnection(Server server) {
        // Nothing to do here
        this.server = server;
    }

    private boolean initFile(File file) {
        this.free();
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            this.connection.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            MyWarp.logger.severe("Class not found", e);
            return false;
        } catch (SQLException e) {
            MyWarp.logger.severe("Generic SQLException", e);
            return false;
        }
        return true;
    }

    public void free() {
        if (this.connection != null) {
            MyWarp.logger.info("Close connection!");
            try {
                this.connection.close();
                this.connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            this.connection = null;
        }
    }

    protected void finalize() throws Throwable {
        this.free();
        super.finalize();
    }

    private class WarpPermission {
        public final int id;
        public final String editor;

        public WarpPermission(int id, String editor) {
            this.id = id;
            this.editor = editor;
        }
    }

    private void update() {
        int version = getVersion();

        if (version < TARGET_VERSION) {
            MyWarp.logger.info("Database layout is outdated (" + version + ")! Updating to " + TARGET_VERSION + ".");
            Statement statement = null;
            PreparedStatement convertedWarp = null;
            PreparedStatement convertedPermissions = null;
            PreparedStatement convertedProtectionArea = null;
            PreparedStatement permissionsInsert = null;
            ResultSet set = null;
            try {
                statement = this.connection.createStatement();

                // Backup old permissions table (if exists)
                if (tableExists("permissions")) {
                    statement.execute("ALTER TABLE permissions RENAME TO permissions_backup");
                    MyWarp.logger.info("Backuping old permissions table.");
                }
                
                MyWarp.logger.info("Creating permission table.");
                statement.execute(PERMISSIONS_TABLE);
                
                if (tableExists("permissions_backup")) {
                    set = statement.executeQuery("SELECT * FROM permissions_backup");
                    convertedPermissions = this.connection.prepareStatement("INSERT INTO permissions (id, editor, value, type, table) VALUES (?,?,?,?,?)");
                    while (set.next()) {
                        convertedPermissions.setInt(1, set.getInt("id"));
                        convertedPermissions.setString(2, set.getString("editor"));
                        convertedPermissions.setInt(3, set.getInt("value"));
                        if (version < 5) {
                            convertedPermissions.setInt(4, EditorPermissions.Type.PLAYER.id);
                            convertedPermissions.setInt(5, EditorPermissions.Table.WARP.id);
                        } else {
                            convertedPermissions.setInt(4, set.getInt("type"));
                            convertedPermissions.setInt(5, set.getInt("table"));
                        }
                        convertedPermissions.executeUpdate();
                    }
                    
                    statement.executeUpdate("DROP TABLE permissions_backup");
                    MyWarp.logger.info("Permissions backup recovered.");
                }
                
                // Backup old protection areas table (if exists)
                if (tableExists("protectionAreas")) {
                    statement.execute("ALTER TABLE protectionAreas RENAME TO protectionAreas_backup");
                    MyWarp.logger.info("Backuping old protection area table.");
                }
                
                MyWarp.logger.info("Creating protection area table.");
                statement.execute(PROTECTION_AREA_TABLE);
                
                if (tableExists("protectionAreas_backup")) {
                    set = statement.executeQuery("SELECT * FROM protectionAreas_backup");
                    convertedProtectionArea = this.connection.prepareStatement("INSERT INTO protectionAreas (id, name, creator, x1, y1, z1, x2, y2, z2, owner) VALUES (?,?,?,?,?,?,?,?,?,?)");
                    while (set.next()) {
                        convertedProtectionArea.setInt(1, set.getInt("id"));
                        convertedProtectionArea.setString(2, set.getString("name"));
                        convertedProtectionArea.setString(3, set.getString("creator"));
                        convertedProtectionArea.setDouble(4, set.getDouble("x1"));
                        convertedProtectionArea.setDouble(5, set.getDouble("y1"));
                        convertedProtectionArea.setDouble(6, set.getDouble("z1"));
                        convertedProtectionArea.setDouble(7, set.getDouble("x2"));
                        convertedProtectionArea.setDouble(8, set.getDouble("y2"));
                        convertedProtectionArea.setDouble(9, set.getDouble("z2"));
                        convertedProtectionArea.setString(10, set.getString("owner"));
                        convertedProtectionArea.executeUpdate();
                    }
                    
                    statement.executeUpdate("DROP TABLE permissions_backup");
                    MyWarp.logger.info("Permissions backup recovered.");
                }

                // Backup old warp table (if exists)
                if (tableExists("warps")) {
                    // Backup it
                    statement.execute("ALTER TABLE warps RENAME TO warps_backup");
                    MyWarp.logger.info("Backuping old warp table.");
                } else if (tableExists("warpTable")) {
                    // Backup it
                    statement.execute("ALTER TABLE warpTable RENAME TO warps_backup");
                    MyWarp.logger.info("Backuping old warp table.");
                }

                // Create new database
                statement.executeUpdate(WARP_TABLE);
                if (tableExists("warps_backup")) {
                    // Select line by line
                    String world = server.getWorlds().get(0).getName();
                    set = statement.executeQuery("SELECT * FROM warps_backup");
                    List<WarpPermission> list = new ArrayList<WarpPermission>();
                    convertedWarp = this.connection.prepareStatement("INSERT INTO warps (id, name, creator, world, x, y, z, yaw, pitch, publicLevel, welcomeMessage, owner, price, cooldown, warmup) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    while (set.next()) {
                        int id = set.getInt("id");
                        convertedWarp.setInt(1, id);
                        convertedWarp.setString(2, set.getString("name"));
                        convertedWarp.setString(3, set.getString("creator"));
                        if (version < 1) {
                            String worldName = set.getString("world");
                            if (worldName.equals("0")) {
                                convertedWarp.setString(4, world);
                            } else {
                                if (this.server.getWorld(set.getString("world")) == null) {
                                    MyWarp.logger.info("Found warp with unknown world. (Name: " + set.getString("name") + ")");
                                }
                                convertedWarp.setString(4, set.getString("world"));
                            }
                        } else {
                            convertedWarp.setString(4, set.getString("world"));
                        }
                        convertedWarp.setDouble(5, set.getDouble("x"));
                        convertedWarp.setDouble(6, set.getDouble("y"));
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
                        if (version < 2) {
                            List<String> p = processList(set.getString("permissions"));
                            for (String string : p) {
                                list.add(new WarpPermission(id, string));
                            }
                        }
                        convertedWarp.setString(11, set.getString("welcomeMessage"));
                        if (version < 3) {
                            convertedWarp.setString(12, set.getString("creator"));
                        } else {
                            convertedWarp.setString(12, set.getString("owner"));
                        }
                        if (version < 4) {
                            convertedWarp.setDouble(13, 0);
                        } else {
                            convertedWarp.setDouble(13, set.getDouble("price"));
                        }
                        convertedWarp.executeUpdate();
                    }

                    if (version < 3) {

                    }

                    if (version < 2) {
                        MyWarp.logger.info("Adding permissions table");

                        if (list.size() > 0) {
                            permissionsInsert = this.connection.prepareStatement("INSERT OR IGNORE INTO permissions (id, editor, value, type, table) VALUES (?,?,?,?,?)");

                            for (WarpPermission warpPermission : list) {
                                permissionsInsert.setInt(1, warpPermission.id);
                                permissionsInsert.setString(2, warpPermission.editor);
                                permissionsInsert.setInt(3, WarpPermissions.WARP.id);
                                permissionsInsert.setInt(4, EditorPermissions.Type.PLAYER.id);
                                permissionsInsert.setInt(5, EditorPermissions.Table.WARP.id);
                                permissionsInsert.addBatch();
                            }
                            permissionsInsert.executeBatch();
                        }
                    }

                    statement.executeUpdate("DROP TABLE warps_backup");
                    MyWarp.logger.info("Recovering the backup.");
                }
                if (version < 0) {
                    statement.executeUpdate("INSERT INTO meta (name, value) VALUES (\"version\", " + TARGET_VERSION + ")");
                } else {
                    statement.executeUpdate("UPDATE meta SET value = " + TARGET_VERSION + " WHERE name = \"version\"");
                }
                this.connection.commit();
            } catch (SQLException ex) {
                // try {
                // statement.execute("ROLLBACK");
                // } catch (SQLException e) {
                // MyWarp.logger.severe("Unable to rollback changes!");
                // }
                MyWarp.logger.log(Level.SEVERE, "Warp Load Exception", ex);
            } finally {
                try {
                    if (permissionsInsert != null)
                        permissionsInsert.close();
                    if (convertedWarp != null)
                        convertedWarp.close();
                    if (convertedPermissions != null)
                        convertedPermissions.close();
                    if (convertedProtectionArea != null)
                        convertedProtectionArea.close();
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

    private boolean tableExists(String name) {
        ResultSet rs = null;
        try {
            DatabaseMetaData dbm = this.connection.getMetaData();
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

    public int getVersion() {
        Statement statement = null;
        int version = -1;
        ResultSet set = null;
        try {
            statement = this.connection.createStatement();
            if (tableExists("meta")) {
                set = statement.executeQuery("SELECT * FROM meta WHERE name = \"version\"");

                if (set.next()) {
                    version = set.getInt("value");
                }
            } else {
                MyWarp.logger.info("Meta table doesn't exists... Creating new");
                statement.executeUpdate(VERSION_TABLE);
                this.connection.commit();
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

    @Override
    public List<Warp> getWarps() {
        List<Warp> result = new ArrayList<Warp>();
        Statement statement = null;
        ResultSet set = null;
        try {
            statement = this.connection.createStatement();
            Map<Integer, Map<EditorPermissions.Type, Map<String, EditorPermissions<WarpPermissions>>>> allPermissions = this.getEditorPermissions(Table.WARP, WarpPermissions.class, WarpPermissions.ID_MAP);

            set = statement.executeQuery("SELECT * FROM warps");
            int size = 0;
            int invalidSize = 0;
            while (set.next()) {
                size++;
                int index = set.getInt("id");
                String name = set.getString("name");
                String creator = set.getString("creator");
                String worldName = set.getString("world");
                World world = server.getWorld(worldName);
                double x = set.getDouble("x");
                double y = set.getDouble("y");
                double z = set.getDouble("z");
                float yaw = set.getFloat("yaw");
                float pitch = set.getFloat("pitch");
                LocationWrapper loc = new LocationWrapper(new FixedLocation(world, x, y, z, yaw, pitch), worldName);
                int publicLevel = set.getInt("publicLevel");
                Visibility visibility = Visibility.parseLevel(publicLevel);
                boolean listed = Visibility.isListed(publicLevel);
                String welcomeMessage = set.getString("welcomeMessage");
                String owner = set.getString("owner");
                int cooldown = set.getInt("cooldown");
                int warmup = set.getInt("warmup");
                Warp warp = new Warp(index, name, creator, owner, loc, visibility, allPermissions.get(index), welcomeMessage);
                warp.setPrice(set.getInt("price"));
                warp.setListed(listed);
                warp.setCoolDown(cooldown);
                warp.setWarmUp(warmup);
                result.add(warp);
                if (!warp.getLocationWrapper().isValid()) {
                    invalidSize++;
                }
            }
            MyWarp.logger.info(size + " warps loaded");
            if (invalidSize > 0) {
                MyWarp.logger.warning(invalidSize + " invalid warps found.");
            }
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
        return result;
    }

    @Override
    public void addWarp(Warp... warps) {
        if (warps.length > 0) {
            PreparedStatement ps = null;
            PreparedStatement insertPermissions = null;
            try {
                ps = this.connection.prepareStatement("INSERT INTO warps (id, name, creator, world, x, y, z, yaw, pitch, publicLevel, welcomeMessage, owner, price) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
                insertPermissions = this.connection.prepareStatement("INSERT INTO permissions (id, editor, value, type, table) VALUES (?,?,?,?,?)");
                for (Warp warp : warps) {
                    ps.setInt(1, warp.index);
                    ps.setString(2, warp.getName());
                    ps.setString(3, warp.getCreator());
                    setLocation(warp.getLocationWrapper(), 4, ps);
                    ps.setInt(10, warp.getVisibility().getInt(warp.isListed()));
                    ps.setString(11, warp.getWelcomeMessage());
                    ps.setString(12, warp.getOwner());
                    ps.setDouble(13, warp.getPrice());
                    ps.addBatch();

                    for (EditorPermissionEntry<WarpPermissions> editorPermissionEntry : warp.getEditorPermissionsList()) {
                        for (WarpPermissions p : editorPermissionEntry.editorPermissions.getByValue(true)) {
                            insertPermissions.setInt(1, warp.index);
                            insertPermissions.setString(2, editorPermissionEntry.name);
                            insertPermissions.setInt(3, p.id);
                            insertPermissions.setInt(4, editorPermissionEntry.type.id);
                            insertPermissions.setInt(5, EditorPermissions.Table.WARP.id);
                            insertPermissions.addBatch();
                        }
                    }
                }
                ps.executeBatch();
                insertPermissions.executeBatch();

                this.connection.commit();
            } catch (SQLException ex) {
                MyWarp.logger.log(Level.SEVERE, "Warp Insert Exception", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (insertPermissions != null) {
                        insertPermissions.close();
                    }
                } catch (SQLException ex) {
                    MyWarp.logger.log(Level.SEVERE, "Warp Insert Exception (on close)", ex);
                }
            }
        }
    }
    
    private static void setLocation(LocationWrapper wrapper, int offset, PreparedStatement ps) throws SQLException {
        ps.setString(offset++, wrapper.getWorld());
        FixedLocation location = wrapper.getLocation();
        ps.setDouble(offset++, location.x);
        ps.setDouble(offset++, location.y);
        ps.setDouble(offset++, location.z);
        ps.setInt(offset++, (int) location.yaw);
        ps.setInt(offset++, (int) location.pitch);
    }

    @Override
    public void deleteWarp(Warp warp) {
        this.updateWarp(warp, "Delete", "DELETE FROM warps WHERE id = ?", new UpdateFiller<Warp>() {

            @Override
            public void fillStatement(Warp warp, PreparedStatement statement) throws SQLException {
                statement.setInt(1, warp.index);
            }
        });
    }

    private interface UpdateFiller<T extends WarpObject<?>> {
        void fillStatement(T warp, PreparedStatement statement) throws SQLException;
    }

    private <T extends WarpObject<?>> void updateWarpObject(T warpObject, String type, String name, String sql, UpdateFiller<T> filler) {
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            ps = this.connection.prepareStatement(sql);
            filler.fillStatement(warpObject, ps);
            ps.executeUpdate();
            this.connection.commit();
        } catch (SQLException ex) {
            MyWarp.logger.log(Level.SEVERE, type + " " + name + " exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
                MyWarp.logger.log(Level.SEVERE, type + " " + name + " exception (on close)", ex);
            }
        }
    }
    
    private void updateWarp(Warp warp, String name, String sql, UpdateFiller<Warp> filler) {
        this.updateWarpObject(warp, "Warp", name, sql, filler);
    }

    @Override
    public void updateOwner(Warp warp, IdentificationInterface<Warp> identification) {
        this.updateWarp(warp, "owner", "UPDATE warps SET owner = ? WHERE id = ?", new UpdateFiller<Warp>() {

            @Override
            public void fillStatement(Warp warp, PreparedStatement statement) throws SQLException {
                statement.setString(1, warp.getOwner());
                statement.setInt(2, warp.index);
            }
        });
    }

    @Override
    public void updateCreator(Warp warp) {
        this.updateWarp(warp, "creator", "UPDATE warps SET creator = ? WHERE id = ?", new UpdateFiller<Warp>() {

            @Override
            public void fillStatement(Warp warp, PreparedStatement statement) throws SQLException {
                statement.setString(1, warp.getCreator());
                statement.setInt(2, warp.index);
            }
        });
    }

    @Override
    public void updateMessage(Warp warp) {
        this.updateWarp(warp, "welcome message", "UPDATE warps SET welcomeMessage = ? WHERE id = ?", new UpdateFiller<Warp>() {

            @Override
            public void fillStatement(Warp warp, PreparedStatement statement) throws SQLException {
                statement.setString(1, warp.getRawWelcomeMessage());
                statement.setInt(2, warp.index);
            }
        });
    }

    @Override
    public void updateName(Warp warp, IdentificationInterface<Warp> identification) {
        this.updateWarp(warp, "name", "UPDATE warps SET name = ? WHERE id = ?", new UpdateFiller<Warp>() {

            @Override
            public void fillStatement(Warp warp, PreparedStatement statement) throws SQLException {
                statement.setString(1, warp.getName());
                statement.setInt(2, warp.index);
            }
        });
    }

    @Override
    public void updateVisibility(Warp warp) {
        this.updateWarp(warp, "visibility", "UPDATE warps SET publicLevel = ? WHERE id = ?", new UpdateFiller<Warp>() {

            @Override
            public void fillStatement(Warp warp, PreparedStatement statement) throws SQLException {
                statement.setInt(1, warp.getVisibility().getInt(warp.isListed()));
                statement.setInt(2, warp.index);
            }
        });
    }

    @Override
    public void updateLocation(Warp warp) {
        this.updateWarp(warp, "location", "UPDATE warps SET world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE id = ?", new UpdateFiller<Warp>() {

            @Override
            public void fillStatement(Warp warp, PreparedStatement statement) throws SQLException {
                SQLiteConnection.setLocation(warp.getLocationWrapper(), 1, statement);
                statement.setInt(7, warp.index);
            }
        });
    }

    @Override
    public void updatePrice(Warp warp) {
        this.updateWarp(warp, "price", "UPDATE warps SET price = ? WHERE id = ?", new UpdateFiller<Warp>() {

            @Override
            public void fillStatement(Warp warp, PreparedStatement statement) throws SQLException {
                statement.setDouble(1, warp.getPrice());
                statement.setInt(2, warp.index);
            }
        });
    }

    private void updateEditor(int id, String name, String typeMsg, EditorPermissions<? extends Editor> editorPerms, EditorPermissions.Type type, EditorPermissions.Table table) {
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            ps = this.connection.prepareStatement("DELETE FROM permissions WHERE id = ? AND editor = ? AND type = ? AND table = ?");
            ps.setInt(1, id);
            ps.setString(2, name.toLowerCase());
            ps.setInt(3, type.id);
            ps.setInt(4, table.id);
            ps.executeUpdate();

            if (editorPerms != null) {
                ps = this.connection.prepareStatement("INSERT OR IGNORE INTO permissions (id, editor, value, type, table) VALUES (?,?,?,?,?)");

                boolean permissionAdded = false;
                
                for (Editor perm : editorPerms.getByValue(true)) {
                    ps.setInt(1, id);
                    ps.setString(2, name.toLowerCase());
                    ps.setInt(3, perm.getId());
                    ps.setInt(4, type.id);
                    ps.setInt(5, table.id);
                    ps.addBatch();
                    permissionAdded = true;
                }

                if (permissionAdded) {
                    ps.executeBatch();
                } else {
                    ps.clearBatch();
                }
            }

            this.connection.commit();
        } catch (SQLException ex) {
            MyWarp.logger.log(Level.SEVERE, typeMsg + " editor exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
                MyWarp.logger.log(Level.SEVERE, typeMsg + " editor exception (on close)", ex);
            }
        }
    }
    
    @Override
    public void updateEditor(Warp warp, String name, EditorPermissions.Type type) {
        this.updateEditor(warp.index, name, "Warp", warp.getEditorPermissions(name, false, type), type, EditorPermissions.Table.WARP);
    }

    public static List<String> processList(String permissions) {
        String[] names = permissions.split(",");
        List<String> ret = new ArrayList<String>();
        for (String name : names) {
            if (name.equals(""))
                continue;
            ret.add(name.trim());
        }
        return ret;
    }

    public boolean load(File file) {
        if (file.exists()) {
            if (this.initFile(file)) {
                this.update();
                return true;
            } else {
                return false;
            }
        } else {
            return this.create(file);
        }
    }

    @Override
    public String getFilename() {
        return "warps.db";
    }

    @Override
    public void clear() {
        Statement statement = null;
        ResultSet set = null;
        try {
            statement = this.connection.createStatement();
            statement.execute("DELETE FROM warps");
            statement.execute("DELETE FROM permissions");
            statement.execute("DELETE FROM protectionAreas");
            this.connection.commit();
        } catch (SQLException ex) {
            MyWarp.logger.log(Level.SEVERE, "Table Clear Exception", ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (set != null)
                    set.close();
            } catch (SQLException ex) {
                MyWarp.logger.severe("Table Clear Exception (on closing)");
            }
        }
    }

    @Override
    public boolean create(File file) {
        this.initFile(file);
        int version = this.getVersion();
        Statement statement = null;
        ResultSet set = null;
        try {
            statement = this.connection.createStatement();
            // Drop warps → create new one
            statement.execute("DROP TABLE IF EXISTS warps");
            statement.execute(WARP_TABLE);
            // Drop permissions → create new one
            statement.execute("DROP TABLE IF EXISTS permissions");
            statement.execute(PERMISSIONS_TABLE);
            if (version < 0) {
                statement.executeUpdate("INSERT INTO meta (name, value) VALUES (\"version\", " + TARGET_VERSION + ")");
            } else {
                statement.executeUpdate("UPDATE meta SET value = " + TARGET_VERSION + " WHERE name = \"version\"");
            }
            this.connection.commit();
        } catch (SQLException ex) {
            MyWarp.logger.log(Level.SEVERE, "Table Drop/Create Exception", ex);
            return false;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (set != null)
                    set.close();
            } catch (SQLException ex) {
                MyWarp.logger.severe("Table Drop/Create Exception (on closing)");
                return false;
            }
        }
        return true;
    }

    private static final class IdIdentification<T extends WarpObject<?>> implements IdentificationInterface<T> {

        private final int id;

        public IdIdentification(int id) {
            this.id = id;
        }

        public static IdIdentification<Warp> create(Warp warp) {
            return new IdIdentification<Warp>(warp.index);
        }

        public static IdIdentification<WarpProtectionArea> create(WarpProtectionArea wpa) {
            return new IdIdentification<WarpProtectionArea>(wpa.index);
        }

        @Override
        public boolean isIdentificated(T warpObject) {
            Integer parameterId = getWarpObjectIndex(warpObject);
            return parameterId != null && parameterId == this.id;
        }

    }

    public static Integer getWarpObjectIndex(WarpObject<?> o) {
        if (o instanceof Warp) {
            return ((Warp) o).index;
        } else if (o instanceof WarpProtectionArea) {
            return ((WarpProtectionArea) o).index;
        } else {
            return null;
        }
    }

    @Override
    public IdentificationInterface<Warp> createWarpIdentification(Warp warp) {
        return IdIdentification.create(warp);
    }
    
    private <T extends Enum<T> & Editor> Map<Integer, Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>>> getEditorPermissions(EditorPermissions.Table table, Class<T> clazz, Map<Integer, T> idMap) {
        Map<Integer, Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>>> result = new HashMap<Integer, Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>>>();
        Statement statement = null;
        ResultSet set = null;
        try {
            statement = this.connection.createStatement();
            set = statement.executeQuery("SELECT * FROM permissions WHERE table = " + table.id);
            Map<Integer, Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>>> allPermissions = new HashMap<Integer, Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>>>();
            while (set.next()) {
                int index = set.getInt("id");
                Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>> warpPermissions = allPermissions.get(index);
                if (warpPermissions == null) {
                    warpPermissions = new EnumMap<EditorPermissions.Type, Map<String, EditorPermissions<T>>>(EditorPermissions.Type.class);
                    allPermissions.put(index, warpPermissions);
                }
                
                EditorPermissions.Type type = EditorPermissions.Type.parseInt(set.getInt("type"));
                Map<String, EditorPermissions<T>> typePermissions = warpPermissions.get(type);
                if (typePermissions == null) {
                    typePermissions = new HashMap<String, EditorPermissions<T>>();
                    warpPermissions.put(type, typePermissions);
                }
                
                String editor = set.getString("editor");
                EditorPermissions<T> editorPermissions = typePermissions.get(editor.toLowerCase());
                if (editorPermissions == null) {
                    editorPermissions = new EditorPermissions<T>(clazz);
                    typePermissions.put(editor.toLowerCase(), editorPermissions);
                }
                int value = set.getInt("value");
                editorPermissions.put(idMap.get(value), true);
            }
        } catch (SQLException ex) {
            MyWarp.logger.severe("Permission database load exception", ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (set != null)
                    set.close();
            } catch (SQLException ex) {
                MyWarp.logger.severe("Permission database load exception (on close)");
            }
        }
        return result;
    }

    @Override
    public List<WarpProtectionArea> getProtectionAreas() {
        List<WarpProtectionArea> result = new ArrayList<WarpProtectionArea>();
        Statement statement = null;
        ResultSet set = null;
        try {
            Map<Integer, Map<EditorPermissions.Type, Map<String, EditorPermissions<WarpProtectionAreaPermissions>>>> allPermissions = this.getEditorPermissions(Table.PROTECTION_AREA, WarpProtectionAreaPermissions.class, WarpProtectionAreaPermissions.ID_MAP);

            statement = this.connection.createStatement();
            set = statement.executeQuery("SELECT * FROM protectionAreas");
            int size = 0;
            int invalidSize = 0;
            while (set.next()) {
                size++;
                int index = set.getInt("id");
                String name = set.getString("name");
                String creator = set.getString("creator");
                String owner = set.getString("owner");
                String worldName = set.getString("world");
                double x1 = set.getDouble("x1");
                double y1 = set.getDouble("y1");
                double z1 = set.getDouble("z1");
                double x2 = set.getDouble("x2");
                double y2 = set.getDouble("y2");
                double z2 = set.getDouble("z2");
                FixedLocation loc1 = new FixedLocation(x1, y1, z1);
                FixedLocation loc2 = new FixedLocation(x2, y2, z2);
                WorldWrapper worldWrapper = new WorldWrapper(worldName);
                WarpProtectionArea wpa = new WarpProtectionArea(index, worldWrapper, loc1, loc2, name, owner, creator);
                
                Map<EditorPermissions.Type, Map<String, EditorPermissions<WarpProtectionAreaPermissions>>> wpaPermissions = allPermissions.get(index);
                if (wpaPermissions != null) {
                    for (Entry<EditorPermissions.Type, Map<String, EditorPermissions<WarpProtectionAreaPermissions>>> typeEntry : wpaPermissions.entrySet()) {
                        for (Entry<String, EditorPermissions<WarpProtectionAreaPermissions>> editorEntry : typeEntry.getValue().entrySet()) {
                            wpa.getEditorPermissions(editorEntry.getKey(), true, typeEntry.getKey()).putAll(editorEntry.getValue());
                        }
                    }
                }
                
                result.add(wpa);
                if (!wpa.isValid()) {
                    invalidSize++;
                }
            }
            MyWarp.logger.info(size + " warps loaded");
            if (invalidSize > 0) {
                MyWarp.logger.warning(invalidSize + " invalid warps found.");
            }
        } catch (SQLException ex) {
            MyWarp.logger.severe("Warp protection area load exception", ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (set != null)
                    set.close();
            } catch (SQLException ex) {
                MyWarp.logger.severe("Warp protection area load exception (on close)");
            }
        }
        return result;
    }

    @Override
    public void addProtectionArea(WarpProtectionArea... areas) {
        if (areas.length > 0) {
            PreparedStatement ps = null;
            PreparedStatement insertPermissions = null;
            try {
                ps = this.connection.prepareStatement("INSERT INTO protectionAreas (id, name, owner, creator, world, x1, y1, z1, x2, y2, z2) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
                insertPermissions = this.connection.prepareStatement("INSERT INTO permissions (id, editor, value, type, table) VALUES (?,?,?,?,?)");
                for (WarpProtectionArea area : areas) {
                    ps.setInt(1, area.index);
                    ps.setString(2, area.getName());
                    ps.setString(3, area.getOwner());
                    ps.setString(4, area.getCreator());
                    ps.setString(5, area.getWorld());
                    FixedLocation loc = area.getCorner(0);
                    ps.setDouble(6, loc.x);
                    ps.setDouble(7, loc.y);
                    ps.setDouble(8, loc.z);
                    loc = area.getCorner(1);
                    ps.setDouble(9, loc.x);
                    ps.setDouble(10, loc.y);
                    ps.setDouble(11, loc.z);
                    ps.addBatch();

                    for (EditorPermissionEntry<WarpProtectionAreaPermissions> editorPermissionEntry : area.getEditorPermissionsList()) {
                        for (WarpProtectionAreaPermissions p : editorPermissionEntry.editorPermissions.getByValue(true)) {
                            insertPermissions.setInt(1, area.index);
                            insertPermissions.setString(2, editorPermissionEntry.name);
                            insertPermissions.setInt(3, p.id);
                            insertPermissions.setInt(4, editorPermissionEntry.type.id);
                            insertPermissions.setInt(5, EditorPermissions.Table.PROTECTION_AREA.id);
                            insertPermissions.addBatch();
                        }
                    }
                }
                ps.executeBatch();
                insertPermissions.executeBatch();

                this.connection.commit();
            } catch (SQLException ex) {
                MyWarp.logger.log(Level.SEVERE, "Warp protection area insert exception", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (insertPermissions != null) {
                        insertPermissions.close();
                    }
                } catch (SQLException ex) {
                    MyWarp.logger.log(Level.SEVERE, "Warp protection area insert exception (on close)", ex);
                }
            }
        }
    }

    @Override
    public void deleteProtectionArea(WarpProtectionArea area) {
        this.updateWarpObject(area, "Warp protection area", "delete", "DELETE FROM protectionAreas WHERE id = ?", new UpdateFiller<WarpProtectionArea>() {

            @Override
            public void fillStatement(WarpProtectionArea warp, PreparedStatement statement) throws SQLException {
                statement.setInt(1, warp.index);
            }
        });
    }

    @Override
    public void updateEditor(WarpProtectionArea area, String name, Type type) {
        this.updateEditor(area.index, name, "Warp protection area", area.getEditorPermissions(name, false, type), type, EditorPermissions.Table.PROTECTION_AREA);
    }

    @Override
    public void updateCreator(WarpProtectionArea area) {
        this.updateWarpObject(area, "Warp protection area", "creator", "UPDATE protectionAreas SET creator = ? WHERE id = ?", new UpdateFiller<WarpProtectionArea>() {

            @Override
            public void fillStatement(WarpProtectionArea warp, PreparedStatement statement) throws SQLException {
                statement.setString(1, warp.getCreator());
                statement.setInt(2, warp.index);
            }
        });        
    }

    @Override
    public void updateOwner(WarpProtectionArea area, IdentificationInterface<WarpProtectionArea> identification) {
        this.updateWarpObject(area, "Warp protection area", "owner", "UPDATE protectionAreas SET owner = ? WHERE id = ?", new UpdateFiller<WarpProtectionArea>() {

            @Override
            public void fillStatement(WarpProtectionArea warp, PreparedStatement statement) throws SQLException {
                statement.setString(1, warp.getOwner());
                statement.setInt(2, warp.index);
            }
        });
    }

    @Override
    public void updateName(WarpProtectionArea area, IdentificationInterface<WarpProtectionArea> identification) {
        this.updateWarpObject(area, "Warp protection area", "name", "UPDATE protectionAreas SET name = ? WHERE id = ?", new UpdateFiller<WarpProtectionArea>() {

            @Override
            public void fillStatement(WarpProtectionArea warp, PreparedStatement statement) throws SQLException {
                statement.setString(1, warp.getName());
                statement.setInt(2, warp.index);
            }
        });
    }

    @Override
    public IdentificationInterface<WarpProtectionArea> createWarpProtectionAreaIdentification(WarpProtectionArea area) {
        return IdIdentification.create(area);
    }
}
