package de.xzise.xwarp.commands;


import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.commands.warp.WarpCommand;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.warpable.WarperFactory;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

/* 
 * Temporary extra command, to warp in another worlds. Maybe it work maybe not!
 */
public class WarpForceToCommand extends WarpCommand {

    public WarpForceToCommand(WarpManager list, Server server) {
        super(list, server, new String[0], "force-to", ">-t");
    }

    @Override
    public boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        Warpable warpable = WarperFactory.getWarpable(sender);
        if (warpable != null) {
            this.manager.warpTo(warp, sender, warpable, false, true);
        }
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
        return "warp force-to <name> [owner]";
    }

    @Override
    public boolean listHelp(CommandSender sender) {
        return XWarp.permissions.permissionOr(sender, PermissionTypes.WARP_TO_PERMISSIONS);
    }
}
