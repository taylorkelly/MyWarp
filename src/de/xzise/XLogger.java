package de.xzise;

import java.util.logging.Level;
import java.util.logging.Logger;

public class XLogger {

	private final Logger logger;
	private final String pluginName;
	
	public XLogger(String loggerName, String pluginName) {
		this.logger = Logger.getLogger(loggerName);
		this.pluginName = pluginName;
	}
	
	public XLogger(String pluginName) {
		this("Minecraft", pluginName);
	}
	
	private String formatMessage(String message) {
		return "[" + pluginName + "]: " + message;
	}
	
	public void info(String msg) {
		this.logger.info(this.formatMessage(msg));
	}
	
	public void warning(String msg) {
		this.logger.warning(this.formatMessage(msg));
	}
	
	public void severe(String msg) {
		this.logger.severe(this.formatMessage(msg));
	}
	
	public void severe(String msg, Throwable exception) {
		this.log(Level.SEVERE, msg, exception);
	}

	public void log(Level level, String msg, Throwable exception) {
		this.logger.log(level, this.formatMessage(msg), exception);
	}
	
}
