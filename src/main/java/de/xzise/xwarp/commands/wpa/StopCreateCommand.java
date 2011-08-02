package de.xzise.xwarp.commands.wpa;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.wrappers.permission.WPAPermissions;

public class StopCreateCommand extends CommonHelpableSubCommand {

    private final CreateCommand createCommand;
    
    public StopCreateCommand(CreateCommand createCommand) {
        super("stop-create", "stop-+", "stop-add");
        this.createCommand = createCommand;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Stops the creation of a protection area.", "To stop the creation of another one, define the player name." };
    }

    @Override
    public String getSmallHelpText() {
        return "Stop wpa creation";
    }

    @Override
    public String getCommand() {
        return "wpa stop-create [player name]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        String playerName = null;
        boolean notify = false;
        if (parameters.length == 1) {
            if (sender instanceof Player) {
                playerName = ((Player) sender).getName();
            } else {
                sender.sendMessage(ChatColor.RED + "Only players could stop their own creations.");
            }
        } else if (parameters.length == 2) {
            if (XWarp.permissions.permission(sender, WPAPermissions.ADMIN_CREATE_STOP)) {
                playerName = parameters[1];
                notify = true;
            } else {
                sender.sendMessage(ChatColor.RED + "You have no permission to stop creation of others.");
            }
        } else {
            return false;
        }
        
        if (playerName != null) {
            this.createCommand.stopCreation(playerName, notify ? sender : null);
        }
        return true;
    }

}
