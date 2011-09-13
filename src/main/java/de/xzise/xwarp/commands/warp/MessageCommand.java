package de.xzise.xwarp.commands.warp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;

public class MessageCommand extends WarpCommand {
    
    public MessageCommand(WarpManager manager, Server server) {
        super(manager, server, "message", "message", "msg");
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        String message = parameters[0];
        if (message.equalsIgnoreCase("none")) {
            message = "";
        } else if (message.equalsIgnoreCase("default")) {
            message = null;
        }
        this.manager.setMessage(warp, sender, message);
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] {"Sets the welcome message which appears if you teleport to this warp." , "If set to 'default' it will show the default message.", "If set to 'none' it will show no message."};
    }

    @Override
    public String getSmallHelpText() {
        return "Sets message";
    }

}
