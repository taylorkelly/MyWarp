package de.xzise.xwarp.commands.warp;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.Warp.Visibility;
import de.xzise.xwarp.commands.DefaultSubCommand;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.lister.GenericLister.Column;
import de.xzise.xwarp.lister.ListSection;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class ListCommand extends DefaultSubCommand<WarpManager> {

    public ListCommand(WarpManager list, Server server) {
        super(list, server, "list", "ls");
    }

    private static <T> void add(CommandSender sender, Set<T> set, T t) {
        if (!set.add(t)) {
            sender.sendMessage(ChatColor.RED + "This parameter was already added.");
        }
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (!MyWarp.permissions.permission(sender, PermissionTypes.CMD_LIST)) {
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
             * c:<creator>
             * oc:<offline creator (won't be expanded)>
             * w:<world>
             * o:<owner>
             * oo:<offline owner (won't be expanded)>
             * v:<visibility>
             */
            
            // Whitelist
            Set<String> creators = new HashSet<String>();
            Set<String> owners = new HashSet<String>();
            Set<String> worlds = new HashSet<String>();
            Set<Visibility> visibilites = new HashSet<Visibility>();
            
            // Blacklist
            /* not implemented yet */
            
            // Column blacklist
            EnumSet<Column> blackColumns = EnumSet.noneOf(Column.class);
            
            Integer page = null; // Default page = 1
            // 0 = list/ls
            for (int i = 1; i < parameters.length; i++) {
                if (parameters[i].startsWith("c:")) {
                    add(sender, creators, MinecraftUtil.expandName(parameters[i].substring(2), this.server).toLowerCase());
                } else if (parameters[i].startsWith("oc:")) {
                    add(sender, creators, parameters[i].substring(2).toLowerCase());
                } else if (parameters[i].startsWith("w:")) {
                    add(sender, worlds, parameters[i].substring(2).toLowerCase());
                } else if (parameters[i].startsWith("o:")) {
                    add(sender, owners, MinecraftUtil.expandName(parameters[i].substring(2), this.server).toLowerCase());
                } else if (parameters[i].startsWith("oo:")) {
                    add(sender, owners, parameters[i].substring(2).toLowerCase());
                } else if (parameters[i].startsWith("v:")) {
                    Visibility v = Visibility.parseString(parameters[i].substring(2));
                    if (v != null) {
                        add(sender, visibilites, v);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Inputed an invalid visibility value: " + parameters[i].substring(2));
                    }
                } else if (parameters[i].startsWith("-col:")) {
                    if (parameters[i].equalsIgnoreCase("-col:owner")) {
                        add(sender, blackColumns, Column.OWNER);
                    } else if (parameters[i].equalsIgnoreCase("-col:world")) {
                        add(sender, blackColumns, Column.WORLD);
                    } else if (parameters[i].equalsIgnoreCase("-col:loc")) {
                        add(sender, blackColumns, Column.LOCATION);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Unknown column: " + parameters[i].substring(5));
                    }
                } else {
                    Integer buffer = MinecraftUtil.tryAndGetInteger(parameters[i]);
                    if (buffer != null) {
                        if (page == null) {
                            page = buffer;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Found more than one page definition. Selecting first: " + buffer);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Unknown parameter prefix: " + parameters[i]);
                    }
                }
            }
            
            if (page == null) {
                page = 1;
            }

            final List<Warp> warps = this.manager.getWarps(sender, creators, owners, worlds, visibilites);
            
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
    
                GenericLister.listPage(page, maxPages, sender, EnumSet.complementOf(blackColumns), section);
            }
        }
        return true;
    }

    private static int getNumberOfPages(int elements, CommandSender sender) {
        return (int) Math.ceil(elements / (double) (MinecraftUtil.getMaximumLines(sender) - 1));
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] {
        "Shows a list of warps. Following filters are available:",
        "oo:<owner>, o:<exp. owner>, oc:<creator>, c:<exp. creator>",
        "w:<world>, v:<visibility>, -col:{owner,world,location}",
        "Example: /warp list o:xZise -col:owner"
        };
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
