package de.xzise.xwarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;

public class InviteCommand<W extends WarpObject<?>, M extends Manager<W>> extends ManagerCommand<W, M> {

    public InviteCommand(M list, Server server, String label) {
        super(list, server, label, "invited", "invite");
    }

    @Override
    protected boolean executeEdit(W warpObject, CommandSender sender, String[] parameters) {
        this.manager.invite(warpObject, sender, this.getPlayer(parameters[0]));
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Adds the invited person to the permissions list.", "These person could use the warp." };
    }

    @Override
    public String getSmallHelpText() {
        return "Invites " + ChatColor.GREEN + "<invited>";
    }

}
