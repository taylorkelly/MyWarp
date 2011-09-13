package de.xzise.xwarp.commands.warp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.DefaultArrays;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;

public class ShowMessageCommand extends WarpCommand {

    private final PluginProperties properties;
    
    public ShowMessageCommand(WarpManager manager, Server server, PluginProperties properties) {
        super(manager, server, DefaultArrays.EMPTY_STRING_ARRAY, "show-message", "shmsg");
        this.properties = properties;
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        if (parameters.length == 0) {
            String message = warp.getRawWelcomeMessage();
            if ("".equals(message)) {
                sender.sendMessage("Welcome message of '" + warp.getName() + "' is empty.");
            } else {
                sender.sendMessage("Welcome message of '" + warp.getName() + "' is:");
                boolean def = false;
                if (message == null) {
                    message = properties.getDefaultMessage().replace("{NAME}", warp.getName());
                    def = true;
                }
                sender.sendMessage(ChatColor.AQUA + message + (def ? ChatColor.WHITE + " (default)" : ""));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] {"Shows the welcome message which appears if you teleport to this warp."};
    }

    @Override
    public String getSmallHelpText() {
        return "Shows message";
    }

}
