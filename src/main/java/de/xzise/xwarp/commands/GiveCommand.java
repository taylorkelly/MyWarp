package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;

public class GiveCommand<W extends WarpObject<?>, M extends Manager<W>> extends ManagerCommand<W, M> {

    public GiveCommand(M manager, Server server, String label) {
        super(manager, server, label, "player", "give", "chown");
    }

    @Override
    protected boolean executeEdit(W warpObject, CommandSender sender, String[] parameters) {
        this.manager.setOwner(warpObject, sender, this.getPlayer(parameters[0]));
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Changes the owner of the warp." };
    }

    @Override
    public String getSmallHelpText() {
        return "Gives the warp away.";
    }
}
