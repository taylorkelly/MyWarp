package de.xzise;

import java.util.logging.Level;
import java.util.logging.Logger;

public class XLogger {

	private static Logger logger;
	private static String name;
	
	public static void initialize(String name, String pluginName) {
		XLogger.logger = Logger.getLogger(name);
		XLogger.name = pluginName;
	}
	
	private static String formatMessage(String message) {
		return "[" + XLogger.name + "]: " + message;
	}
	
	public static void info(String msg) {
		XLogger.logger.info(XLogger.formatMessage(msg));
	}
	
	public static void warning(String msg) {
		XLogger.logger.warning(XLogger.formatMessage(msg));
	}
	
	public static void severe(String msg) {
		XLogger.logger.severe(XLogger.formatMessage(msg));
	}
	
	public static void severe(String msg, Throwable exception) {
		XLogger.log(Level.SEVERE, msg, exception);
	}

	public static void log(Level level, String msg, Throwable exception) {
		XLogger.logger.log(level, msg, exception);
	}
	
}
