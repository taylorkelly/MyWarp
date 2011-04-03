package de.xzise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.xzise.metainterfaces.ConsoleCommandWrapper;
import de.xzise.metainterfaces.LinesCountable;
import de.xzise.metainterfaces.Nameable;

public final class MinecraftUtil {

    public static final int PLAYER_LINES_COUNT = 10;
    /**
     * @deprecated Use {@link #PLAYER_LINES_COUNT} instead.
     */
    public static final int MAX_LINES_VISIBLE = PLAYER_LINES_COUNT;
    public static final int CONSOLE_LINES_COUNT = 30;

    private MinecraftUtil() {
    }

    /*
     * Minecraft specific
     */
    
    public static int getMaximumLines(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return CONSOLE_LINES_COUNT;
        } else if (sender instanceof LinesCountable) {
            return ((LinesCountable) sender).getMaxLinesVisible();
        } else {
            return PLAYER_LINES_COUNT;
        }
    }

    /**
     * Returns the name to a sender. If the sender has no player it returns null.
     * 
     * @param sender
     *            The given sender.
     * @return Returns the name of the sender and null if the sender is no player.
     */
    public static String getPlayerName(CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getName();
        } else {
            return null;
        }
    }

    /**
     * Returns a name in each case. If the sender is a player it return the player name. If the sender is the console it will return <code>[SERVER]</code>. In all other cases it return <code>Somebody</code>.
     * @param sender The name of this sender will be determined.
     * @return The name of the sender.
     */
    public static String getName(CommandSender sender) {
        String name = MinecraftUtil.getPlayerName(sender);
        if (name == null) {
            if (sender instanceof ConsoleCommandSender) {
                return ConsoleCommandWrapper.NAME;
            } else if (sender instanceof Nameable) {
                return ((Nameable) sender).getName();
            } else {
                return "Somebody";
            }
        } else {
            return name;
        }
    }
    
    /**
     * Expands the name, so it match a player (if possible).
     * @param name The primitive name.
     * @param server The server where the player is searched.
     * @return The name of a player on the server, if the name matches anything. Otherwise the inputed name.
     * @see Uses {@link Server#getPlayer(String)} to determine the player object to the name.
     */
    public static String expandName(String name, Server server) {
        Player player = server.getPlayer(name);
        return player == null ? name : player.getName();
    }

    public static String[] parseLine(String line) {
        return MinecraftUtil.parseLine(line, ' ');
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
    public static String[] parseLine(String line, char delimiter) {
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
                default:
                    if (delimiter == c && !quoted) {
                        if (lastStart < i) {
                            values.add(String.copyValueOf(word, 0, wordIndex));
                            word = new char[line.length() - i];
                            wordIndex = 0;
                        }
                        lastStart = i + 1;
                    } else {
                        word[wordIndex] = c;
                        wordIndex++;
                    }
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
     * Checks if an object is set. Set mean at least “not null”. Following objects
     * will be checked separate:
     * 
     * <blockquote>
     * <table>
     * <tr>
     * <th>Type</th>
     * <th>Tests</th>
     * </tr>
     * <tr>
     * <td>String</td>
     * <td>not <tt>{@link String#isEmpty()}</tt></td>
     * </tr>
     * <tr>
     * <td>List&lt;?&gt;</td>
     * <td><tt>{@link List#size()} > 0</tt></td>
     * </tr>
     * <tr>
     * <td>Any array</td>
     * <td>Arraylength is positive</td>
     * </tr>
     * </table>
     * </blockquote>
     * 
     * @param o The tested object.
     * @return If the object is not empty.
     */
    public static boolean isSet(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof String) {
            return !((String) o).isEmpty();
        } else if (o instanceof List<?>) {
            return !((List<?>) o).isEmpty();
        } else if (o.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(o) > 0;
        } else {
            return true;
        }
    }
    
    public static String getOrdinal(int value) {
        String ordinal = "";
        if ((value % 100) / 10 == 1) {
            ordinal = "th";
        } else {
            switch (value % 10) {
            case (1):
                ordinal = "st";
                break;
            case (2):
                ordinal = "nd";
                break;
            case (3):
                ordinal = "rd";
                break;
            default:
                ordinal = "th";
            }
        }
        return value + ordinal;
    }
    
    public static String scramble(String word) {
        Random rand = new Random();
        char[] input = word.toCharArray();
        char[] result = new char[word.length()];
        boolean[] used = new boolean[word.length()];
        int i = 0;
        int length = word.length();
        while (i < length) {
            int newIdx = rand.nextInt(length);
            if (!used[newIdx]) {
                used[newIdx] = true;
                result[newIdx] = input[i];
                i++;
            }
        }        
        return new String(result);
    }
    
    public static int getWidth(int number, int base) {
        int width = 1;
        while (number >= base) {
                number /= base;
                width++;
        }
        return width;
    }
    
    public static <T> boolean toogleEntry(T entry, List<T> list) {
        if (list.remove(entry)) {
            return false;
        } else {
            list.add(entry);
            return true;
        }
    }
    
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
    public static <T> int indexOf(T o, T[] a) {
        int idx = 0;
        for (T t : a) {
            if (t != null && t.equals(o)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }
    
    public static <T> boolean contains(T o, T[] a) {
	return MinecraftUtil.indexOf(o, a) >= 0;
    }
    
    public static <T> T getRandom(List<T> list) {
	return list.get(new Random().nextInt(list.size()));
    }
    
    public static <T> T getRandom(T[] array) {
	return array[new Random().nextInt(array.length)];
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
