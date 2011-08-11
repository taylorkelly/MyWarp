package de.xzise.xwarp.commands.warp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;

public class SafetyCheckCommand extends WarpCommand {

    public SafetyCheckCommand(WarpManager manager, Server server) {
        super(manager, server, "", "safety-check");
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Returns everything related to the safety check." };
    }

    @Override
    public String getSmallHelpText() {
        return "Manually safety check";
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        if (parameters.length == 0) {
            boolean safety = warp.isSave(sender);
            sender.sendMessage("Warp is " + (safety ? "" : "not ") + "save.");
            return true;
        } else {
            return false;
        }
    }

}
