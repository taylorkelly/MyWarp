package me.taylorkelly.mywarp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.util.config.Configuration;

public class WarpSettings {
    
    private static final String settingsFile = "MyWarp.settings";

    public static int maxPublic;
    public static int maxPrivate;
    public static boolean adminsObeyLimits;
    public static boolean adminPrivateWarps;


    public static void initialize(File dataFolder) {
        if(!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        if(!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile  = new File(dataFolder, settingsFile);
        PropertiesFile file = new PropertiesFile(configFile);
        maxPublic = file.getInt("maxPublic", 5, "Maximum number of public warps any player can make");
        maxPrivate = file.getInt("maxPrivate", 10, "Maximum number of private warps any player can make");
        adminsObeyLimits = file.getBoolean("adminsObeyLimits", false, "Whether or not admins can disobey warp limits");
        adminPrivateWarps = file.getBoolean("adminPrivateWarps", true, "Whether or not admins can see private warps in their list");
        file.save();
    }
}
