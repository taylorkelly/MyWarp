package de.xzise.xwarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class ListedCommand extends WarpCommand {

    protected ListedCommand(WarpManager list, Server server) {
        super(list, server, "list warp", "listed");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String owner, String[] parameters) {
        String parameter = parameters[0];
        Boolean listed = null;
        if (parameter.equalsIgnoreCase("yes") || parameter.equalsIgnoreCase("true")) {
            listed = true;
        } else if (parameter.equalsIgnoreCase("no") || parameter.equalsIgnoreCase("false")) {
            listed = false;
        }
        if (listed != null) {
            this.list.setListed(warpName, owner, sender, listed);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "The 'list warp' value has to be 'yes', 'true', 'no' or 'false'.");
        }
        return true;
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Sets a warp to listed or not listed.", "To list the warp the parameter has to be 'yes' or 'true.", "To unlist it has to be 'no' or 'false." };
    }

    @Override
    protected String getSmallHelpText() {
        // TODO Auto-generated method stub
        return null;
    }

}
