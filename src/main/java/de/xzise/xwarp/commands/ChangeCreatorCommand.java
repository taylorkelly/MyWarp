package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;

public class ChangeCreatorCommand<W extends WarpObject<?>, M extends Manager<W>> extends ManagerCommand<W, M> {

    public ChangeCreatorCommand(M manager, Server server, String label) {
        super(manager, server, label, "player", "change-creator", "chcre");
    }

    @Override
    public boolean executeEdit(W warp, CommandSender sender, String[] parameters) {
        this.manager.setCreator(warp, sender, this.getPlayer(parameters[0]));
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Changes the creator of the warp." };
    }

    @Override
    public String getSmallHelpText() {
        return "Change the creator";
    }

}
