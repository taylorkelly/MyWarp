package me.taylorkelly.mywarp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.util.config.Configuration;

public class WarpSettings {
    
    private static final String settingsFile = "MyWarp.yml";
    
    public static boolean adminPrivateWarps;

    public static void initialize(File dataFolder) {
        if(!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile  = new File(dataFolder, settingsFile);
        if(!configFile.exists()) {
            createSettingsFile(configFile);
        }
        Configuration config = new Configuration(configFile);
        config.load();
        adminPrivateWarps = config.getBoolean("adminPrivateWarps", true);
    }

    private static void createSettingsFile(File configFile) {
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            configFile.createNewFile();
            fwriter = new FileWriter(configFile, true);
            bwriter = new BufferedWriter(fwriter);
            bwriter.write("adminPrivateWarps: true");
            bwriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
