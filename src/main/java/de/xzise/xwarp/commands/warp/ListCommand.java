package de.xzise.xwarp.commands.warp;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.Callback;
import de.xzise.MinecraftUtil;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.Warp.Visibility;
import de.xzise.xwarp.commands.DefaultSubCommand;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.lister.GenericLister.Column;
import de.xzise.xwarp.lister.ListSection;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class ListCommand extends DefaultSubCommand<WarpManager> {

    public static class WhiteBlackList<V, S extends Set<V>> implements Callback<Boolean, V> {
        private final S white, black;

        public WhiteBlackList(S white, S black) {
            this.white = white;
            this.black = black;
        }

        public boolean add(V value, boolean white) {
            if (white) {
                return this.white.add(value);
            } else {
                return this.black.add(value);
            }
        }

        public S getWhitelist() {
            return this.white;
        }

        public S getBlacklist() {
            return this.black;
        }

        public boolean isEmpty() {
            return this.white.isEmpty() && this.black.isEmpty();
        }

        @Override
        public Boolean call(V value) {
            if (this.black.contains(value)) {
                return false;
            } else if (this.white.contains(value)) {
                return true;
            } else if (this.isEmpty()) {
                return null;
            } else {
                return this.white.isEmpty();
            }
        }
    }

    public static abstract class DefWhiteBlackList<V> extends WhiteBlackList<V, Set<V>> {
        public DefWhiteBlackList(Set<V> white, Set<V> black) {
            super(white, black);
        }

        public DefWhiteBlackList() {
            this(new HashSet<V>(), new HashSet<V>());
        }
    }

    public static abstract class WarpOptions<V> extends WhiteBlackList<V, HashSet<V>> {

        public WarpOptions() {
            super(new HashSet<V>(), new HashSet<V>());
        }

        public abstract V getValue(Warp warp);

        public Boolean call(Warp warp) {
            V value = getValue(warp);
            return super.call(value);
        }
    }

    public static abstract class StringOptions extends WarpOptions<String> {
        public boolean add(String string, boolean white) {
            return super.add(string.toLowerCase(), white);
        }

        public abstract String getString(Warp warp);

        @Override
        public String getValue(Warp warp) {
            return this.getString(warp).toLowerCase();
        }
    }

    public static class OwnerOptions extends StringOptions {

        @Override
        public String getString(Warp warp) {
            return warp.getOwner();
        }
    }

    public static class CreatorOptions extends StringOptions {

        @Override
        public String getString(Warp warp) {
            return warp.getCreator();
        }

    }

    public static class WorldOptions extends StringOptions {

        @Override
        public String getString(Warp warp) {
            return warp.getWorld();
        }

    }

    public static class VisibilityOptions extends WarpOptions<Visibility> {

        @Override
        public Visibility getValue(Warp warp) {
            return warp.getVisibility();
        }

    }

    public static class EnumWhiteBlackList<E extends Enum<E>> extends WhiteBlackList<E, EnumSet<E>> {

        protected final Class<E> enumClass;

        public EnumWhiteBlackList(Class<E> enumClass) {
            super(EnumSet.noneOf(enumClass), EnumSet.noneOf(enumClass));
            this.enumClass = enumClass;
        }

        /**
         * Returns all white listed and not defined elements.
         * 
         * @return all white listed and not defined elements.
         */
        public EnumSet<E> getSelected() {
            EnumSet<E> result = EnumSet.copyOf(this.getWhitelist());
            result.addAll(EnumSet.complementOf(this.getBlacklist()));
            return result;
        }

        public EnumSet<E> getByStatus(Boolean status) {
            EnumSet<E> result = EnumSet.noneOf(this.enumClass);
            for (E e : enumClass.getEnumConstants()) {
                if (this.call(e) == status) {
                    result.add(e);
                }
            }
            return result;
        }
    }

    private final PluginProperties properties;

    public ListCommand(WarpManager list, Server server, PluginProperties properties) {
        super(list, server, "list", "ls");
        this.properties = properties;
    }

    private static <T> void add(CommandSender sender, WhiteBlackList<T, ?> set, T t, boolean white) {
        if (!set.add(t, white)) {
            sender.sendMessage(ChatColor.RED + "This parameter was already added.");
        }
    }

    private static Integer processOptions(CommandSender sender, String value, OwnerOptions owners, CreatorOptions creators, WorldOptions worlds, VisibilityOptions visibilities, EnumWhiteBlackList<Column> columns) {
        if (!value.isEmpty()) {
            boolean white = true;
            char modifier = value.charAt(0);
            String rawValue;
            switch (modifier) {
            case '-':
                white = false;
            case '+':
                rawValue = value.substring(1);
                break;
            default:
                rawValue = value;
            }

            String[] segments = rawValue.split(":");
            if (segments.length == 2) {
                if (segments[0].equals("c")) {
                    add(sender, creators, MinecraftUtil.expandName(segments[1]), white);
                } else if (rawValue.startsWith("oc")) {
                    add(sender, creators, segments[1], white);
                } else if (rawValue.startsWith("o")) {
                    add(sender, owners, MinecraftUtil.expandName(segments[1]), white);
                } else if (rawValue.startsWith("oo")) {
                    add(sender, owners, segments[1], white);
                } else if (rawValue.startsWith("w")) {
                    add(sender, worlds, segments[1], white);
                } else if (rawValue.startsWith("v")) {
                    Visibility v = Visibility.parseString(segments[1]);
                    if (v != null) {
                        add(sender, visibilities, v, white);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid visibility value: " + segments[1]);
                    }
                } else if (segments[0].equals("col")) {
                    if (segments[1].equalsIgnoreCase("owner")) {
                        add(sender, columns, Column.OWNER, white);
                    } else if (segments[1].equalsIgnoreCase("world")) {
                        add(sender, columns, Column.WORLD, white);
                    } else if (segments[1].equalsIgnoreCase("loc")) {
                        add(sender, columns, Column.LOCATION, white);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid column value: " + segments[1]);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown parameter prefix: " + segments[0]);
                }
            } else {
                Integer buffer = MinecraftUtil.tryAndGetInteger(rawValue);
                if (buffer != null) {
                    return buffer;
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown parameter: " + rawValue);
                }
            }
        }
        return null;
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (!XWarp.permissions.permission(sender, PermissionTypes.CMD_LIST)) {
            sender.sendMessage(ChatColor.RED + "You have no permission to list warps.");
            return true;
        }

        // Special case
        if (parameters.length == 2 && parameters[1].equalsIgnoreCase("legend")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Maybe no colors here, so this command could be useless here!");
            }
            for (String line : GenericLister.getLegend()) {
                sender.sendMessage(line);
            }
        } else {
            // Parse values here
            /*
             * c:<creator> oc:<offline creator (won't be expanded)> w:<world>
             * o:<owner> oo:<offline owner (won't be expanded)> v:<visibility>
             * col:<column>
             */

            // MixedList
            OwnerOptions owners = new OwnerOptions();
            CreatorOptions creators = new CreatorOptions();
            WorldOptions worlds = new WorldOptions();
            VisibilityOptions visibilities = new VisibilityOptions();
            EnumWhiteBlackList<Column> columns = new EnumWhiteBlackList<Column>(Column.class);

            columns.getWhitelist().addAll(this.properties.getDefaultColumns());

            Integer page = null; // Default page = 1
            // 0 = list/ls
            for (int i = 1; i < parameters.length; i++) {
                Integer buffer = processOptions(sender, parameters[i], owners, creators, worlds, visibilities, columns);
                if (buffer != null) {
                    if (page == null) {
                        page = buffer;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Found more than one page definition. Selecting first: " + buffer);
                    }
                }
            }

            System.out.println(columns.getWhitelist().size() + " " + columns.getBlacklist().size());
            for (Column c : columns.getSelected()) {
                System.out.println(c);
            }

            if (page == null) {
                page = 1;
            }

            final List<Warp> warps = this.manager.getWarps(sender, creators, owners, worlds, visibilities);

            final int maxPages = getNumberOfPages(warps.size(), sender);
            final int numLines = MinecraftUtil.getMaximumLines(sender) - 1;

            final ListSection section = new ListSection("", numLines);

            if (maxPages < 1) {
                sender.sendMessage(ChatColor.RED + "There are no warps to list");
            } else if (page < 1) {
                sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
            } else if (page > maxPages) {
                sender.sendMessage(ChatColor.RED + "There are only " + maxPages + " pages of warps");
            } else {
                // Get only those warps one the page
                final int offset = (page - 1) * numLines;
                final int lines = Math.min(warps.size() - offset, numLines);
                List<Warp> pageWarps = warps.subList(offset, offset + lines);

                section.addWarps(pageWarps);

                GenericLister.listPage(page, maxPages, sender, columns.getByStatus(true), section);
            }
        }
        return true;
    }

    private static int getNumberOfPages(int elements, CommandSender sender) {
        return (int) Math.ceil(elements / (double) (MinecraftUtil.getMaximumLines(sender) - 1));
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Shows a list of warps. Following filters are available:", "oo:<owner>, o:<exp. owner>, oc:<creator>, c:<exp. creator>", "w:<world>, v:<visibility>, -col:{owner,world,location}", "Example: /warp list o:xZise -col:owner" };
    }

    @Override
    public String getSmallHelpText() {
        return "Shows the warp list";
    }

    @Override
    public String getCommand() {
        return "warp list [filters|#page]";
    }
}
