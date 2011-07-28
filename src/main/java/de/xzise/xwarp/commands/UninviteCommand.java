package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;

public class UninviteCommand<W extends WarpObject<?>, M extends Manager<W>> extends ManagerCommand<W, M> {

    public UninviteCommand(M list, Server server, String label) {
        super(list, server, label, "player", "uninvite");
    }
    
    public static <W extends WarpObject<?>, M extends Manager<W>> UninviteCommand<W, M> create(M manager, Server server, String label) {
        return new UninviteCommand<W, M>(manager, server, label);
    }

    @Override
    protected boolean executeEdit(W warpObject, CommandSender sender, String[] parameters) {
        this.manager.uninvite(warpObject, sender, this.getPlayer(parameters[0]));
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Revokes the invitation of the invited user." };
    }

    @Override
    public String getSmallHelpText() {
        return "Uninvites the user.";
    }
}
