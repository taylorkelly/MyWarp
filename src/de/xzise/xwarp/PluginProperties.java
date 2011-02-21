package de.xzise.xwarp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Server;

import me.taylorkelly.mywarp.MyWarp;

import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.NullConnection;
import de.xzise.xwarp.dataconnections.SQLiteConnection;

public class PluginProperties {
	
	private DataConnection dataConnection;
	private File dataDirectory;
	private File configFile;
	private Server server;
	
	public PluginProperties(File dataDirectory, Server server) {
		this.dataDirectory = dataDirectory;
		this.configFile = new File(this.dataDirectory, "config.properties");
		this.server = server;
		this.read();
	}
	
	public DataConnection getDataConnection() {
		return this.dataConnection;
	}
	
	public void read() {
		java.util.Properties properties = new java.util.Properties();
		if (this.configFile.exists()) {
			try {
				BufferedInputStream stream = new BufferedInputStream(new FileInputStream(this.configFile));
				properties.load(stream);
				stream.close();
			} catch (IOException e) {
				MyWarp.logger.warning("Unable to load properties file.", e);
			}
		} else {
			try {
				properties.setProperty("data-connection", "sqlite");
				properties.store(new FileWriter(this.configFile), null);
			} catch (IOException e) {
				MyWarp.logger.warning("Unable to create properties file.", e);
			}
		}
		String dataConnectionProperty = properties.getProperty("data-connection", "sqlite");
		
		if (dataConnectionProperty.equalsIgnoreCase("none")) {
			// Not implemented (yet?)
			this.dataConnection = new NullConnection();
		} else {
			if (!dataConnectionProperty.equalsIgnoreCase("sqlite")) {
				MyWarp.logger.warning("Unrecognized data-connection selected (" + dataConnectionProperty + ")");
			}
			// Per default sqlite
			this.dataConnection = new SQLiteConnection(server);
		}
	}

}
