package de.xzise.xwarp.lister;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import me.taylorkelly.mywarp.Warp;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.warpable.WarperFactory;

public class GenericLister {

    public static final ChatColor GLOBAL_OWN = ChatColor.DARK_BLUE;
    public static final ChatColor PUBLIC_OWN = ChatColor.BLUE;
    public static final ChatColor PRIVATE_OWN = ChatColor.AQUA;

    public static final ChatColor GLOBAL_OTHER = ChatColor.DARK_GREEN;
    public static final ChatColor PUBLIC_OTHER = ChatColor.GREEN;
    public static final ChatColor PRIVATE_OTHER = ChatColor.RED;

    public static final ChatColor PRIVATE_INVITED = ChatColor.YELLOW;

    private GenericLister() {
    }
    
    public enum Column {
        OWNER,
        WORLD,
        LOCATION;
    }
    
    public static void listPage(int page, int maxPages, CommandSender sender, ListSection... sections) {
        listPage(page, maxPages, sender, EnumSet.allOf(Column.class), sections);
    }
    
    public static void listPage(int page, int maxPages, CommandSender sender, Set<Column> columns, ListSection... sections) {

        int charsPerLine = 40;
        WidthCalculator widther = null;

        // Get the correct width calculator!
        if (sender instanceof ConsoleCommandSender) {
            charsPerLine = 80;
            widther = new ConsoleWidth();
        } else if (sender instanceof Player) {
            charsPerLine = 40;
            widther = new MinecraftWidth();
        }

        // Generate header with the same length every time
        String intro = GenericLister.charList(charsPerLine / 2 - GenericLister.getWidth(page, 10), '-') + " " + ChatColor.GREEN + "Page " + page + "/" + maxPages + ChatColor.WHITE + " " + GenericLister.charList(charsPerLine / 2 - GenericLister.getWidth(maxPages, 10), '-');

        sender.sendMessage(ChatColor.WHITE + intro);

        final int width = widther.getWidth(intro);

        for (ListSection listSection : sections) {
            if (listSection.title != null && !listSection.title.isEmpty()) {
                sender.sendMessage(listSection.title);
            }

            for (Warp warp : listSection) {
                String name = warp.name;

                String owner = warp.getOwner();
                ChatColor color;
                if (sender instanceof Player) {
                    if (owner.equalsIgnoreCase(((Player) sender).getName())) {
                        owner = "you";
                    }
                    color = GenericLister.getColor(warp, (Player) sender);
                } else {
                    color = GenericLister.getColor(warp, null);
                }

                String location = GenericLister.getLocationString(warp.getLocationWrapper(), columns.contains(Column.WORLD), columns.contains(Column.LOCATION));
                final String creatorString = columns.contains(Column.OWNER) ? " by " + owner : "";
                
                // Find remaining length left
                int left = width - widther.getWidth("''" + creatorString + location);

                int nameLength = widther.getWidth(name);
                if (left > nameLength) {
                    name = "'" + name + "'" + ChatColor.WHITE + creatorString + whitespace(left - nameLength, widther.getWidth(" "));
                } else if (left < nameLength) {
                    name = "'" + substring(name, left, widther) + ChatColor.WHITE + "..." + color + "'";
                    nameLength = widther.getWidth(name);
                    // Cut location if needed
                    location = substring(location, width - nameLength - widther.getWidth(creatorString), widther);
                    name += ChatColor.WHITE + creatorString;
                }

                sender.sendMessage(color + name + location);
            }
        }
    }

    /**
     * Lob shit off that string till it fits.
     */
    private static String substring(String name, int left, WidthCalculator widthCalculator) {
        while (widthCalculator.getWidth(name) > left && name.length() > 3) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    public static String whitespace(int length, int spaceWidth) {
        return charList(length / spaceWidth, ' ');
    }

    public static String charList(int count, char c) {
        StringBuilder ret = new StringBuilder();

        while (count-- > 0) {
            ret.append(c);
        }

        return ret.toString();
    }

    private static int getWidth(int number, int base) {
        int width = 1;
        while (number >= base) {
            number /= base;
            width++;
        }
        return width;
    }

    public static String[] getLegend() {
        List<String> result = new ArrayList<String>(8);
        result.add(ChatColor.RED + "-------------------- " + ChatColor.WHITE + "LIST LEGEND" + ChatColor.RED + " -------------------");
        result.add(GenericLister.GLOBAL_OWN + "Yours and it is global");
        result.add(GenericLister.PUBLIC_OWN + "Yours and it is public.");
        result.add(GenericLister.PRIVATE_OWN + "Yours and it is private.");
        result.add(GenericLister.GLOBAL_OTHER + "Not yours and it is global");
        result.add(GenericLister.PUBLIC_OTHER + "Not yours and it is public");
        result.add(GenericLister.PRIVATE_OTHER + "Not yours, private and not invited");
        result.add(GenericLister.PRIVATE_INVITED + "Not yours, private and you are invited");
        return result.toArray(new String[0]);
    }

    public static ChatColor getColor(Warp warp, Player player) {
        if (player != null && warp.isOwn(player.getName())) {
            switch (warp.getVisibility()) {
            case PRIVATE:
                return GenericLister.PRIVATE_OWN;
            case PUBLIC:
                return GenericLister.PUBLIC_OWN;
            case GLOBAL:
                return GenericLister.GLOBAL_OWN;
            }
        } else {
            switch (warp.getVisibility()) {
            case PRIVATE:
                if (player != null && warp.playerCanWarp(WarperFactory.getWarpable(player))) {
                    return GenericLister.PRIVATE_INVITED;
                } else {
                    return GenericLister.PRIVATE_OTHER;
                }
            case PUBLIC:
                return GenericLister.PUBLIC_OTHER;
            case GLOBAL:
                return GenericLister.GLOBAL_OTHER;
            }
        }
        return GenericLister.PRIVATE_OTHER;
    }

    public static String getLocationString(LocationWrapper wrapper, boolean world, boolean coordinates) {
        if (world || coordinates) {
            FixedLocation location = wrapper.getLocation();
            StringBuilder result = new StringBuilder("@(");
            if (world) {
                result.append(wrapper.getWorld());
                if (!wrapper.isValid()) {
                    result.append(" " + ChatColor.RED + "(invalid)" + ChatColor.WHITE);
                }
                if (coordinates) {
                    result.append(", ");
                }
            }
            if (coordinates) {
                result.append(location.getBlockX()).append(", ");
                result.append(location.getBlockY()).append(", ");
                result.append(location.getBlockZ());
            }
            return result.append(")").toString();
        } else {
            return "";
        }
    }

}

interface WidthCalculator {
    int getWidth(String text);
}

class MinecraftWidth implements WidthCalculator {

    @Override
    public int getWidth(String text) {
        return MinecraftFontWidthCalculator.getStringWidth(text);
    }

}

class ConsoleWidth implements WidthCalculator {

    @Override
    public int getWidth(String text) {
        // Assume that the font is non proportional!
        return ChatColor.stripColor(text).length();
    }

}