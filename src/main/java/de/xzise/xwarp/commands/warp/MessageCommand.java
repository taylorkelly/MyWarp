package de.xzise.xwarp.commands.warp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.commands.WarpCommand;

public class MessageCommand extends WarpCommand {

    public MessageCommand(WarpManager list, Server server) {
        super(list, server, "message", "message", "msg");
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        if (parameters.length == 0) {
            String message = warp.getWelcomeMessage();
            if (message.isEmpty()) {
                sender.sendMessage("Welcome message of '" + warp.getName() + "' is empty.");
            } else {
                sender.sendMessage("Welcome message of '" + warp.getName() + "' is:");
                boolean def = warp.getRawWelcomeMessage() == null;
                sender.sendMessage(ChatColor.AQUA + message + (def ? ChatColor.WHITE + " (default)" : ""));
            }
            return true;
        } else if (parameters.length == 1) {
            String message = parameters[0];
            if (message.equalsIgnoreCase("none")) {
                message = "";
            } else if (message.equalsIgnoreCase("default")) {
                message = null;
            }
            this.manager.setMessage(warp, sender, message);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] {"Sets the welcome message which appears if you teleport to this warp." , "If set to 'default' it will show the default message."};
    }

    @Override
    public String getSmallHelpText() {
        return "Sets message";
    }

}
