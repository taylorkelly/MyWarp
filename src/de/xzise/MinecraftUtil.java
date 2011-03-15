package de.xzise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.warpable.WarpablePlayer;

public final class MinecraftUtil {

    public static final int MAX_LINES_VISIBLE = 10;

    private MinecraftUtil() {
    }

    /*
     * Minecraft specific
     */

    /**
     * Returns the name to a sender. If the sender has no name it returns null.
     * 
     * @param sender
     *            The given sender.
     * @return Returns the name of the sender and null if there is no name.
     */
    public static String getPlayerName(CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getName();
        } else if (sender instanceof WarpablePlayer) {
            return ((WarpablePlayer) sender).getName();
        } else {
            return null;
        }
    }

    public static String getName(CommandSender sender) {
        String name = MinecraftUtil.getPlayerName(sender);
        if (name == null) {
            if (sender instanceof ConsoleCommandSender) {
                return "[SERVER]";
            } else {
                return "Somebody";
            }
        } else {
            return name;
        }
    }

    /**
     * Parses a string line using quoting and escaping. It will split the line
     * where a space is, but ignores quoted or escaped spaces.
     * 
     * Examples: <blockquote>
     * <table>
     * <tr>
     * <th>Input</th>
     * <th>Output</th>
     * </tr>
     * <tr>
     * <td>Hello World</td>
     * <td><tt>{ Hello, World }</tt></td>
     * </tr>
     * <tr>
     * <td>"Hello World"</td>
     * <td><tt>{ Hello World }</tt></td>
     * </tr>
     * <tr>
     * <td>Hello\ World</td>
     * <td><tt>{ Hello World }</tt></td>
     * </tr>
     * <tr>
     * <td>"Hello World \"Bukkit\""</td>
     * <td><tt>{ Hello World "Bukkit" }</tt></td>
     * </tr>
     * </table>
     * </blockquote>
     * 
     * <b>Notice</b>: This method ignores illegal escapes.
     * 
     * @param line
     *            The command line.
     * @return The parsed segments.
     */
    public static String[] parseLine(String line) {
        boolean quoted = false;
        boolean escaped = false;
        int lastStart = 0;
        char[] word = new char[line.length()];
        int wordIndex = 0;
        List<String> values = new ArrayList<String>(2);
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escaped) {
                word[wordIndex] = c;
                wordIndex++;
                escaped = false;
            } else {
                switch (c) {
                case '"':
                    quoted = !quoted;
                    break;
                case '\\':
                    escaped = true;
                    break;
                case ' ':
                    if (!quoted) {
                        if (lastStart < i) {
                            values.add(String.copyValueOf(word, 0, wordIndex));
                            word = new char[line.length() - i];
                            wordIndex = 0;
                        }
                        lastStart = i + 1;
                        break;
                    }
                default:
                    word[wordIndex] = c;
                    wordIndex++;
                    break;
                }
            }
        }
        if (wordIndex > 0) {
            values.add(String.copyValueOf(word, 0, wordIndex));
        }
        return values.toArray(new String[0]);
    }

    /*
     * Java specific
     */

    /**
     * Tries to convert a string into an integer. If the string is invalid it
     * returns <code>null</code>.
     * 
     * @param string
     *            The string to be parsed.
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
     * 
     * @param o
     *            Searched object.
     * @param a
     *            Searched array.
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

        // if (!fromFile.exists())
        // throw new IOException("FileCopy: " + "no such source file: "
        // + fromFileName);
        // if (!fromFile.isFile())
        // throw new IOException("FileCopy: " + "can't copy directory: "
        // + fromFileName);
        // if (!fromFile.canRead())
        // throw new IOException("FileCopy: " + "source file is unreadable: "
        // + fromFileName);
        //
        // if (toFile.isDirectory())
        // toFile = new File(toFile, fromFile.getName());
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
