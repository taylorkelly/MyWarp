package me.taylorkelly.mywarp;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class MyWarp extends JavaPlugin{
	private WMPlayerListener playerListener;
	public final static String name = "MyWarp";
	public final static String version = "1.0";
	public MyWarp(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, plugin, cLoader);
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {
		Logger log = Logger.getLogger("Minecraft");

		if (!new File(name).exists()) {
			try {
				(new File(name)).mkdir();
			} catch (Exception e) {
				log.log(Level.SEVERE, "[MYWARP]: Unable to create MyWarp/ directory");
			}
		}
		
		WarpList warpList = new WarpList(getServer());
		playerListener = new WMPlayerListener(this, warpList);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);	
		log.info(name + " " + version + " enabled");
	}

}
