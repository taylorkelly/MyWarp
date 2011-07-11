package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.Warp;

import org.bukkit.ChatColor;
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
                String message = warp.getWelcomeMessage();
                if (message.isEmpty()) {
                    sender.sendMessage("Welcome message of '" + warp.name + "' is empty.");
                } else {
                    sender.sendMessage("Welcome message of '" + warp.name + "' is:");
                    boolean def = warp.getRawWelcomeMessage() == null;
                    sender.sendMessage(ChatColor.AQUA + message + (def ? ChatColor.WHITE + " (default)" : ""));
                }
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
        return new String[] {"Sets the welcome message which appears if you teleport to this warp." , "If set to 'default' it will show the default message."};
    }

    @Override
    protected String getSmallHelpText() {
        return "Sets message";
    }

}
