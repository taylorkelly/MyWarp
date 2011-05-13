package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.lister.ListSection;

public class ListCommand extends DefaultSubCommand {

    public ListCommand(WarpManager list, Server server) {
        super(list, server, "list", "ls");
    }

    @Override
    protected boolean internalExecute(CommandSender sender, String[] parameters) {        
        if (parameters.length == 3 && !MinecraftUtil.isInteger(parameters[2])) {
            return false;
        } else if (!MyWarp.permissions.permission(sender, PermissionTypes.CMD_LIST)) {
            sender.sendMessage(ChatColor.RED + "You have no permission to list warps.");
            return true;
        }

        if (parameters.length == 2 && parameters[1].equalsIgnoreCase("legend")) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("No colors in console, so this command is useless here!");
            }
            for (String line : GenericLister.getLegend()) {
                sender.sendMessage(line);
            }
        } else {
            String creator = null;
            int page;
            int maxPages = -1;
            int numLines = MinecraftUtil.getMaximumLines(sender);

            ListSection section = new ListSection("", numLines);

            if (parameters.length == 3 || (parameters.length == 2 && !MinecraftUtil.isInteger(parameters[1]))) {
                creator = this.getPlayer(parameters[1]);
                if (parameters.length == 3) {
                    page = Integer.parseInt(parameters[2]);
                } else {
                    page = 1;
                }
            } else if (parameters.length == 2) {
                page = Integer.parseInt(parameters[1]);
            } else {
                page = 1;
            }

            maxPages = getNumberOfPages(this.list.getSize(sender, creator), sender);

            if (maxPages < 1) {
                sender.sendMessage(ChatColor.RED + "There are no warps to list");
                return true;
            } else if (page < 1) {
                sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
                return true;
            } else if (page > maxPages) {
                sender.sendMessage(ChatColor.RED + "There are only " + maxPages + " pages of warps");
                return true;
            }

            section.addWarps(this.list.getSortedWarps(sender, creator, (page - 1) * (numLines - 1), numLines - 1));

            GenericLister.listPage(page, maxPages, sender, section);
        }
        return true;
    }

    private static int getNumberOfPages(int elements, CommandSender sender) {
        return (int) Math.ceil(elements / (double) (MinecraftUtil.getMaximumLines(sender) - 1));
    }

    @Override
    protected String[] getFullHelpText() {
        String[] legend = GenericLister.getLegend();
        String[] lines = new String[2 + legend.length];
        lines[0] = "Shows the given page of the warp list.";
        lines[1] = "If creator is set only the warps of the creator are listed.";
        for (int i = 0; i < legend.length; i++) {
            lines[i + 2] = legend[i];
        }
        return lines;
    }

    @Override
    protected String getSmallHelpText() {
        return "Shows the warp list";
    }

    @Override
    protected String getCommand() {
        return "warp list [creator] [#page]";
    }
}
