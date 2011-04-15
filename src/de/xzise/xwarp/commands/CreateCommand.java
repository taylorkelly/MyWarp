package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;
import de.xzise.xwarp.PermissionWrapper.PermissionValues;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.WarperFactory;
import de.xzise.xwarp.WarpManager;

public class CreateCommand extends DefaultSubCommand {

    private final Visibility visibility;

    protected CreateCommand(WarpManager list, Server server, String suffix, Visibility visibility) {
        super(list, server, CreateCommand.getCreateCommands(suffix));
        this.visibility = visibility;
    }

    public static CreateCommand newCreatePrivate(WarpManager list, Server server) {
        return new CreateCommand(list, server, "p", Visibility.PRIVATE);
    }

    public static CreateCommand newCreatePublic(WarpManager list, Server server) {
        return new CreateCommand(list, server, "", Visibility.PUBLIC);
    }

    public static CreateCommand newCreateGlobal(WarpManager list, Server server) {
        return new CreateCommand(list, server, "g", Visibility.GLOBAL);
    }

    private static String[] getCreateCommands(String suffix) {
        return new String[] { "create" + suffix, "+" + suffix, "add" + suffix };
    }
    
    private String getAmount(Player player, PermissionValues value) {
        int amount = MyWarp.permissions.getInteger(player, value, -1);
        if (amount < 0) {
            return "Infinite";
        } else if (amount == 0) {
            return "None";
        } else {
            return Integer.toString(amount);
        }
    }

    @Override
    protected boolean internalExecute(CommandSender sender, String[] parameters) {
        Player player = MinecraftUtil.getPlayer(sender);
        if (parameters.length == 1 && player != null) {
            player.sendMessage("You could create following amount of warps:");
            player.sendMessage(GenericLister.GLOBAL_OWN + "Global: " + ChatColor.GREEN + getAmount(player, PermissionValues.WARP_LIMIT_GLOBAL) + ChatColor.WHITE + " (created: " + ChatColor.GREEN + this.list.getAmountOfWarps(player.getName(), Visibility.GLOBAL) + ChatColor.WHITE + ")");
            player.sendMessage(GenericLister.PUBLIC_OWN + "Public: " + ChatColor.GREEN + getAmount(player, PermissionValues.WARP_LIMIT_PUBLIC) + ChatColor.WHITE + " (created: " + ChatColor.GREEN + this.list.getAmountOfWarps(player.getName(), Visibility.PUBLIC) + ChatColor.WHITE + ")");
            player.sendMessage(GenericLister.PRIVATE_OWN + "Private: " + ChatColor.GREEN + getAmount(player, PermissionValues.WARP_LIMIT_PRIVATE) + ChatColor.WHITE + " (created: " + ChatColor.GREEN + this.list.getAmountOfWarps(player.getName(), Visibility.PRIVATE) + ChatColor.WHITE + ")");
            player.sendMessage(ChatColor.GREEN + "Total: " + getAmount(player, PermissionValues.WARP_LIMIT_TOTAL) + ChatColor.WHITE + " (created: " + ChatColor.GREEN + this.list.getAmountOfWarps(player.getName(), null) + ChatColor.WHITE + ")");
            return true;
        } else {
            Positionable position = WarperFactory.getPositionable(sender);
            if (position != null) {
                String newOwner;
                switch (parameters.length) {
                case 2:
                    if (sender instanceof Player) {
                        newOwner = ((Player) sender).getName();
                    } else {
                        sender.sendMessage(ChatColor.RED + "You couldn't own a warp.");
                        return true;
                    }
                    break;
                case 3:
                    newOwner = parameters[2];
                    break;
                default:
                    return false;
                }
                this.list.addWarp(parameters[1], position, newOwner, this.visibility);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You are unable to create a warp, because you have no position.");
            }
            return true;
        }
    }

    @Override
    protected String[] getFullHelpText() {
        String visibilityText = "";
        switch (this.visibility) {
        case PRIVATE:
            visibilityText = "private";
            break;
        case PUBLIC:
            visibilityText = "public";
            break;
        case GLOBAL:
            visibilityText = "global";
            break;
        }
        return new String[] { "Creates a new warp, the visibility is by default " + visibilityText };
    }

    @Override
    protected String getSmallHelpText() {
        switch (this.visibility) {
        case PRIVATE:
            return "Creates private warp";
        case PUBLIC:
            return "Creates public warp";
        case GLOBAL:
            return "Creates global warp";
        default:
            return "Missing help text";
        }
    }

    @Override
    protected String getCommand() {
        return "warp " + this.commands[0] + " <name> [new owner]";
    }

    @Override
    protected boolean listHelp(CommandSender sender) {
        switch (this.visibility) {
        case PRIVATE:
            return MyWarp.permissions.permission(sender, PermissionTypes.CREATE_PRIVATE);
        case PUBLIC:
            return MyWarp.permissions.permission(sender, PermissionTypes.CREATE_PUBLIC);
        case GLOBAL:
            return MyWarp.permissions.permission(sender, PermissionTypes.CREATE_GLOBAL);
        default:
            return false;
        }
    }
}
