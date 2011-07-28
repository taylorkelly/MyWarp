package de.xzise.xwarp.commands;


import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.warpable.WarpablePlayer;
import de.xzise.xwarp.warpable.WarperFactory;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class WarpToCommand extends DefaultSubCommand<WarpManager> {

    public WarpToCommand(WarpManager manager, Server server) {
        super(manager, server, "to");
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        Warpable warped;
        String otherName = null;
        if (parameters.length == 4 && parameters[0].equalsIgnoreCase("to")) {
            otherName = parameters[3];
        } else if (parameters.length == 3 && !parameters[0].equalsIgnoreCase("to")) {
            otherName = parameters[2];
        } else if (parameters.length == 0 || parameters.length > 3) {
            return false;
        }
        if (otherName != null) {
            Player player = this.server.getPlayer(otherName);
            if (player != null) {
                warped = new WarpablePlayer(player);
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown warped player given.");
                return true;
            }
        } else {
            warped = WarperFactory.getWarpable(sender);
            if (warped == null) {
                sender.sendMessage(ChatColor.RED + "You are not able to warp anywhere.");
                return true;
            }
        }

        int start = 0;
        if (parameters[0].equalsIgnoreCase("to") && (parameters.length == 2 || parameters.length == 3)) {
            start++;
        }
        String creator = "";
        if (parameters.length > start + 1) {
            creator = this.getPlayer(parameters[start + 1]);
        }
        this.manager.warpTo(parameters[start], creator, sender, warped, false);
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Warps the player to the given warp.", "This command is only ingame available." };
    }

    @Override
    public String getSmallHelpText() {
        return "Warps the player";
    }

    @Override
    public String getCommand() {
        return "warp [to] <name> [creator] [warped]";
    }

    @Override
    public boolean listHelp(CommandSender sender) {
        return XWarp.permissions.permissionOr(sender, PermissionTypes.WARP_TO_PERMISSIONS);
    }
}
