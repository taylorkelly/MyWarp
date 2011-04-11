package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.Warp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.WarpManager;

public class MessageCommand extends WarpCommand {

    public MessageCommand(WarpManager list, Server server) {
        super(list, server, "message", "message", "msg");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String owner, String[] parameters) {
        if (parameters.length == 0) {
            Warp warp = this.list.getWarp(warpName, owner, MinecraftUtil.getPlayerName(sender));
            if (warp != null) {
                sender.sendMessage("Welcome message of '" + warp.name + "' is:");
                sender.sendMessage(warp.welcomeMessage);
            } else {
                WarpManager.sendMissingWarp(warpName, owner, sender);
            }
            return true;
        } else if (parameters.length == 1) {
            this.list.setMessage(warpName, owner, sender, parameters[0]);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] {"Sets the welcome message which appears if you teleport to this warp."};
    }

    @Override
    protected String getSmallHelpText() {
        return "Sets message";
    }

}
