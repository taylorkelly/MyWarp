package me.taylorkelly.mywarp;

import java.io.File;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.Messaging;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class MyWarp extends JavaPlugin{
	
	public static PermissionHandler permissions = new WarpHandler();
	
	private WMPlayerListener playerListener;
	public final String name = this.getDescription().getName();
	public final String version = this.getDescription().getVersion();
	public MyWarp(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File directory, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, directory, plugin, cLoader);
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {
		Logger log = Logger.getLogger("Minecraft");

		if(new File("MyWarp").exists() && new File("MyWarp", "warps.db").exists()) {
			updateFiles();
		}
		
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

		if(test != null) {
			MyWarp.permissions = Permissions.Security;
	    } else {
	    	log.info(Messaging.bracketize(name) + " Permission system not found.");
	    }		
		
		WarpList warpList = new WarpList(getServer());
		playerListener = new WMPlayerListener(this, warpList);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);	
		log.info(name + " " + version + " enabled");
	}

	private void updateFiles() {
		File file = new File("MyWarp", "warps.db");
		File folder = new File("MyWarp");
		file.renameTo(new File("homes-warps.db"));
		folder.delete();
	}

}
