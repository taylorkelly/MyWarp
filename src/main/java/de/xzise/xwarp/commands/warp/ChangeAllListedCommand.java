package de.xzise.xwarp.commands.warp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.commands.DefaultSubCommand;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

/*
 * Temporary class to change the listed status of all warps!
 */
public class ChangeAllListedCommand extends DefaultSubCommand<WarpManager> {

    public ChangeAllListedCommand(WarpManager manager, Server server) {
        super(manager, server, "alllisted");
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Changes the listed status of all warps to the specified state.", "The states could be: Yes/True/Listed for listed and No/False/Unlisted for unlisted." };
    }

    @Override
    public String getSmallHelpText() {
        return "Changes listed of all warps.";
    }

    @Override
    public String getCommand() {
        return "warp alllisted <state>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 2) {
            if (XWarp.permissions.permission(sender, PermissionTypes.ADMIN_LIST_CHANGE)) {
                final Boolean listed;
                if (parameters[1].equalsIgnoreCase("yes") || parameters[1].equalsIgnoreCase("true") || parameters[1].equalsIgnoreCase("listed")) {
                    listed = true;
                } else if (parameters[1].equalsIgnoreCase("no") || parameters[1].equalsIgnoreCase("false") || parameters[1].equalsIgnoreCase("unlisted")) {
                    listed = false;
                } else {
                    listed = null;
                }
                if (listed == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid listed state!");
                } else {
                    for (Warp warp : this.manager.getWarpObjects()) {
                        warp.setListed(listed);
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to change the listed state of somebody elses warps.");
            }
            return true;
        } else {
            return false;
        }
    }

}
