package de.xzise.xwarp.commands.warp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;

public class ListedCommand extends WarpCommand {

    public ListedCommand(WarpManager list, Server server) {
        super(list, server, "list warp", "listed");
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        String parameter = parameters[0];
        Boolean listed = null;
        if (parameter.equalsIgnoreCase("yes") || parameter.equalsIgnoreCase("true")) {
            listed = true;
        } else if (parameter.equalsIgnoreCase("no") || parameter.equalsIgnoreCase("false")) {
            listed = false;
        }
        if (listed != null) {
            this.manager.setListed(warp, sender, listed);
        } else {
            sender.sendMessage(ChatColor.RED + "The 'list warp' value has to be 'yes', 'true', 'no' or 'false'.");
        }
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Defines if a warp is listed.", "To list the warp the parameter has to be 'yes' or 'true.", "To unlist it has to be 'no' or 'false'." };
    }

    @Override
    public String getSmallHelpText() {
        return "(Un)lists a warp";
    }

}
