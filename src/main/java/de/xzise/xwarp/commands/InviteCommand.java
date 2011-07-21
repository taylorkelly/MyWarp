package de.xzise.xwarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class InviteCommand extends WarpCommand {

    public InviteCommand(WarpManager list, Server server) {
        super(list, server, "invited", "invite");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
        this.list.invite(warpName, creator, sender, this.getPlayer(parameters[0]));
        return true;
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Adds the invited person to the permissions list.", "These person could use the warp." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Invites " + ChatColor.GREEN + "<invited>";
    }

}
