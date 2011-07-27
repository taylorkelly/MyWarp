package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;

public class DeleteCommand<W extends WarpObject<?>, M extends Manager<W>> extends ManagerCommand<W, M> {

    public DeleteCommand(M manager, Server server, String label) {
        super(manager, server, label, "", "delete", "-");
    }

    @Override
    protected boolean executeEdit(W warpObject, CommandSender sender, String[] parameters) {
        this.manager.delete(warpObject, sender);
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Deletes the given warp." };
    }

    @Override
    public String getSmallHelpText() {
        return "Deletes the warp.";
    }

}
