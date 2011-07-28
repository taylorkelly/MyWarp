package de.xzise.xwarp.commands.warp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.WarperFactory;

public class UpdateCommand extends WarpCommand {

    public UpdateCommand(WarpManager list, Server server) {
        super(list, server, "", "update", "*");
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        Positionable positionable = WarperFactory.getPositionable(sender);
        if (positionable != null) {
            this.manager.updateLocation(warp, positionable);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Updates the position of the warp." };
    }

    @Override
    public String getSmallHelpText() {
        return "Updates the warp's position.";
    }
}
