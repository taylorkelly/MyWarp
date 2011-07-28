package de.xzise.xwarp.commands.warp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;

public class PrivatizeCommand extends WarpCommand {

    public PrivatizeCommand(WarpManager list, Server server) {
        super(list, server, "", "private");
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        this.manager.privatize(warp, sender);
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Sets a warp to private.", "Only invited and owners could visit private warps." };
    }

    @Override
    public String getSmallHelpText() {
        return "Privatizes the warp";
    }
}
