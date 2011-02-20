package de.xzise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class MinecraftUtil {
	
	public static final int MAX_LINES_VISIBLE = 10;
	
	private MinecraftUtil() {}
	
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
}
