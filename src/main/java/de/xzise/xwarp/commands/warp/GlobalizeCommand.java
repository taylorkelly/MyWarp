package de.xzise.xwarp.commands.warp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;

public class GlobalizeCommand extends WarpCommand {

    public GlobalizeCommand(WarpManager manager, Server server) {
        super(manager, server, "", "global");
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        this.manager.globalize(warp, sender);
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Sets the status of a warp to global.", "This is only possible if there is no global warp with this name." };
    }

    @Override
    public String getSmallHelpText() {
        return "Globalizes the warp.";
    }

}
