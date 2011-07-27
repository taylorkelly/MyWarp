package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;
import de.xzise.xwarp.wrappers.permission.PermissionValues;
import de.xzise.xwarp.wrappers.permission.PricePermissions;
import de.xzise.xwarp.wrappers.permission.WorldPermission;

public class PermissionsCommand extends CommonHelpableSubCommand {

    private final Server server = Bukkit.getServer();
    
    public PermissionsCommand(WarpManager list, Server server) {
        super("permissions");
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        Player player = null;
        boolean showGranted = true;
        boolean showDenied = true;
        switch (parameters.length) {
        case 2:
            if (!(parameters[1].equalsIgnoreCase("n") || parameters[1].equalsIgnoreCase("y"))) {
                player = this.server.getPlayer(parameters[1]);
                if (player == null) {
                    sender.sendMessage("Player is not logged in.");
                    return true;
                }
                break;
            } else {
                showGranted = parameters[1].equalsIgnoreCase("y");
                showDenied = parameters[1].equalsIgnoreCase("n");
            }
        case 1:
            if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                sender.sendMessage("You are not a player");
                return true;
            }
            break;
        case 3:
            if (parameters[2].equalsIgnoreCase("n") || parameters[2].equalsIgnoreCase("y")) {
                player = this.server.getPlayer(parameters[1]);
                if (player == null) {
                    sender.sendMessage("Player is not logged in.");
                    return true;
                }

                showGranted = parameters[2].equalsIgnoreCase("y");
                showDenied = parameters[2].equalsIgnoreCase("n");
                break;
            } else {
                return false;
            }
        }

        if (player != null) {
            if (sender == player) {
                sender.sendMessage("Your permissions:");
            } else {
                sender.sendMessage(player.getName() + " permissions:");
            }
            if (!MyWarp.permissions.isActive()) {
                sender.sendMessage("(Use build in permissions!)");
            }
            for (PermissionTypes type : PermissionTypes.values()) {
                boolean hasPermission = MyWarp.permissions.permission(player, type);
                if ((hasPermission && showGranted) || (!hasPermission && showDenied)) {
                    String message = (hasPermission ? ChatColor.GREEN : ChatColor.RED) + type.name + ": " + (hasPermission ? "Yes" : "No");
                    sender.sendMessage(message);
                }
            }
            for (PermissionValues value : PermissionValues.values()) {
                sender.sendMessage(value.getName() + ": " + MyWarp.permissions.getInteger(sender, value));
            }
            for (PricePermissions value : PricePermissions.values()) {
                sender.sendMessage(value.getName() + ": " + MyWarp.permissions.getDouble(sender, value));
            }
            sender.sendMessage("Allowed to warp within: " + this.worldPermission(sender, WorldPermission.WITHIN_WORLD));
            sender.sendMessage("Allowed to warp into: " + this.worldPermission(sender, WorldPermission.TO_WORLD));
            return true;
        } else {
            return false;
        }
    }
    
    private String worldPermission(CommandSender sender, WorldPermission permission) {
        int count = 0;
        String worlds = "";
        for (World world : this.server.getWorlds()) {
            if (MyWarp.permissions.permission(sender, permission.getPermission(world.getName(), false))) {
                if (count > 0) {
                    worlds += ", ";
                }
                worlds += world.getName();
                count++;
            }
        }
        if (count == this.server.getWorlds().size()) {
            return "All worlds";
        } else if (count == 0) {
            return "None";
        } else {
            return worlds;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Shows all your permissions." };
    }

    @Override
    public String getSmallHelpText() {
        return "Shows the permissions of you";
    }

    @Override
    public String getCommand() {
        return "warp permissions";
    }

    @Override
    public boolean listHelp(CommandSender sender) {
        // It is only a debug function so: false?
        return false;
    }

}
