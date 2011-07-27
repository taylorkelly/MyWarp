package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;

public class RenameCommand<W extends WarpObject<?>, M extends Manager<W>> extends ManagerCommand<W, M> {

    public RenameCommand(M list, Server server, String label) {
        super(list, server, label, "new name", "rename", "mv");
    }

    @Override
    protected boolean executeEdit(W warpObject, CommandSender sender, String[] parameters) {
        this.manager.setName(warpObject, sender, parameters[0]);
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Changes the name of the warp." };
    }

    @Override
    public String getSmallHelpText() {
        return "Renames the warp";
    }
}
