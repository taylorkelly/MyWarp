package de.xzise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public final class MinecraftUtil {
	
	public static final int MAX_LINES_VISIBLE = 10;
	
	private MinecraftUtil() {}
	
	/*
	 * Minecraft specific
	 */
	
	/**
	 * Returns the name to a sender. If the sender has no name it returns null.
	 * @param sender The given sender.
	 * @return Returns the name of the sender and null if there is no name.
	 */
	public static String getPlayerName(CommandSender sender) {
		if (sender instanceof Player) {
			return ((Player) sender).getName();
		} else {
			return null;
		}
	}
	
	public static String getName(CommandSender sender) {
		if (sender instanceof Player) {
			return ((Player) sender).getName();
		} else if (sender instanceof ConsoleCommandSender) {
			return "[SERVER]";
		} else {
			return "Somebody";
		}
	}
	
	/*
	 * Java specific
	 */
	
	/**
	 * Tries to convert a string into an integer. If the string is invalid it returns <code>null</code>.
	 * @param string The string to be parsed.
	 * @return The value if the string is valid, otherwise <code>null</code>.
	 */
	public static Integer tryAndGetInteger(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Tests where the first object is inside the array.
	 * @param o Searched object.
	 * @param a Searched array.
	 * @return the first position found.
	 */
	public static <T> int contains(T o, T[] a) {
		int idx = 0;
		for (T t : a) {
			if (t != null && t.equals(o)) {
				return idx;
			}
			idx++;
		}
		return -1;
	}
	
	public static void copyFile(File source, File destination) throws IOException {
	    FileReader in = new FileReader(source);
	    if (!destination.exists()) {
	    	destination.createNewFile();
	    }
	    FileWriter out = new FileWriter(destination);
	    int c;

	    while ((c = in.read()) != -1)
	      out.write(c);

	    in.close();
	    out.close();
	}
	
	public static void copy(File fromFile, File toFile) throws IOException {

//    if (!fromFile.exists())
//      throw new IOException("FileCopy: " + "no such source file: "
//          + fromFileName);
//    if (!fromFile.isFile())
//      throw new IOException("FileCopy: " + "can't copy directory: "
//          + fromFileName);
//    if (!fromFile.canRead())
//      throw new IOException("FileCopy: " + "source file is unreadable: "
//          + fromFileName);
//
//    if (toFile.isDirectory())
//      toFile = new File(toFile, fromFile.getName());
//
		if (toFile.exists()) {
			if (!toFile.canWrite())
				throw new IOException("FileCopy: " + "destination file is unwriteable: " + toFile.getName());
		} else {
			File parent = toFile.getParentFile();
			if (parent == null)
				parent = new File(System.getProperty("user.dir"));
			if (!parent.exists())
				throw new IOException("FileCopy: " + "destination directory doesn't exist: " + parent);
			if (parent.isFile())
				throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
			if (!parent.canWrite())
				throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
		}

    FileInputStream from = null;
    FileOutputStream to = null;
    try {
      from = new FileInputStream(fromFile);
      to = new FileOutputStream(toFile);
      byte[] buffer = new byte[4096];
      int bytesRead;

      while ((bytesRead = from.read(buffer)) != -1)
        to.write(buffer, 0, bytesRead); // write
    } finally {
      if (from != null)
        try {
          from.close();
        } catch (IOException e) {
          ;
        }
      if (to != null)
        try {
          to.close();
        } catch (IOException e) {
          ;
        }
    }
  }

	public static boolean isInteger(String string) {
		return MinecraftUtil.tryAndGetInteger(string) != null;
	}
}
