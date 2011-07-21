package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.WarperFactory;

public class UpdateCommand extends WarpCommand {

    public UpdateCommand(WarpManager list, Server server) {
        super(list, server, "", "update", "*");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
        Positionable positionable = WarperFactory.getPositionable(sender);
        if (positionable != null) {
            this.list.updateLocation(warpName, creator, positionable);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Updates the position of the warp." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Updates the warp's position.";
    }
}
