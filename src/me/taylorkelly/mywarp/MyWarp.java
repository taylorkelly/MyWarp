package me.taylorkelly.mywarp;

import java.io.File;
import java.sql.Connection;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;


import de.xzise.DatabaseConnection;
import de.xzise.XLogger;
import de.xzise.xwarp.CommandMap;
import de.xzise.xwarp.PermissionWrapper;

public class MyWarp extends JavaPlugin implements DatabaseConnection {
	
	public static PermissionWrapper permissions = new PermissionWrapper();
	public static XLogger logger;
	
	private WMPlayerListener playerListener;
	public final String name = this.getDescription().getName();
	public final String version = this.getDescription().getVersion();
	
	public MyWarp(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File directory, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, directory, plugin, cLoader);
		logger = new XLogger(this.name);
	}

	@Override
	public void onDisable() {
	    ConnectionManager.freeConnection();
	}

	@Override
	public void onEnable() {

		if(new File("MyWarp").exists() && new File("MyWarp", "warps.db").exists()) {
			updateFiles();
		}

		// Init connection here
		if (ConnectionManager.initializeConnection(this.getServer()) == null) {
			MyWarp.logger.severe("Could not establish SQL connection. Disabling " + name + "!");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		permissions.init(this.getServer());
		
		WarpList warpList = new WarpList(getServer());

		// Create commands
		CommandMap commands = null;
		try {
			commands = new CommandMap(warpList, this.getServer());
		} catch (IllegalArgumentException iae) {
			MyWarp.logger.severe("Couldn't initalize commands.", iae);
			this.getServer().getPluginManager().disablePlugin(this);
		}
		
		this.playerListener = new WMPlayerListener(commands);
		MWBlockListener blockListener = new MWBlockListener(warpList);
		this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		this.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Normal, this);
		this.getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Low, this);
//		this.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);
//		this.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Low, this);
		MyWarp.logger.info(name + " " + version + " enabled");
	}

	private void updateFiles() {
		File file = new File("MyWarp", "warps.db");
		File folder = new File("MyWarp");
		file.renameTo(new File("homes-warps.db"));
		folder.delete();
	}

	@Override
	public Connection getConnection() {
		return ConnectionManager.getConnection();
	}

}
