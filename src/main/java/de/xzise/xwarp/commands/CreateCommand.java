package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.Warp.Visibility;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.WarperFactory;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;
import de.xzise.xwarp.wrappers.permission.PermissionValues;
import de.xzise.xwarp.WarpManager;

public class CreateCommand extends DefaultSubCommand<WarpManager> {

    private final Visibility visibility;

    protected CreateCommand(WarpManager manager, Server server, String suffix, Visibility visibility) {
        super(manager, server, CreateCommand.getCreateCommands(suffix));
        this.visibility = visibility;
    }

    public static CreateCommand newCreatePrivate(WarpManager manager, Server server) {
        return new CreateCommand(manager, server, "p", Visibility.PRIVATE);
    }

    public static CreateCommand newCreatePublic(WarpManager manager, Server server) {
        return new CreateCommand(manager, server, "", Visibility.PUBLIC);
    }

    public static CreateCommand newCreateGlobal(WarpManager manager, Server server) {
        return new CreateCommand(manager, server, "g", Visibility.GLOBAL);
    }

    private static String[] getCreateCommands(String suffix) {
        return new String[] { "create" + suffix, "+" + suffix, "add" + suffix };
    }

    private String getAmount(Player player, PermissionValues value) {
        int amount = MyWarp.permissions.getInteger(player, value);
        if (amount < 0) {
            return "Infinite";
        } else if (amount == 0) {
            return "None";
        } else {
            return Integer.toString(amount);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        Player player = MinecraftUtil.getPlayer(sender);
        if (parameters.length == 1 && player != null) {
            player.sendMessage("You could create following amount of warps:");
            player.sendMessage(GenericLister.GLOBAL_OWN + "Global: " + ChatColor.GREEN + getAmount(player, PermissionValues.WARP_LIMIT_GLOBAL) + ChatColor.WHITE + " (created: " + ChatColor.GREEN + this.manager.getAmountOfWarps(player.getName(), Visibility.GLOBAL, player.getWorld().getName()) + ChatColor.WHITE + ")");
            player.sendMessage(GenericLister.PUBLIC_OWN + "Public: " + ChatColor.GREEN + getAmount(player, PermissionValues.WARP_LIMIT_PUBLIC) + ChatColor.WHITE + " (created: " + ChatColor.GREEN + this.manager.getAmountOfWarps(player.getName(), Visibility.PUBLIC, player.getWorld().getName()) + ChatColor.WHITE + ")");
            player.sendMessage(GenericLister.PRIVATE_OWN + "Private: " + ChatColor.GREEN + getAmount(player, PermissionValues.WARP_LIMIT_PRIVATE) + ChatColor.WHITE + " (created: " + ChatColor.GREEN + this.manager.getAmountOfWarps(player.getName(), Visibility.PRIVATE, player.getWorld().getName()) + ChatColor.WHITE + ")");
            player.sendMessage(ChatColor.GREEN + "Total: " + getAmount(player, PermissionValues.WARP_LIMIT_TOTAL) + ChatColor.WHITE + " (created: " + ChatColor.GREEN + this.manager.getAmountOfWarps(player.getName(), null, player.getWorld().getName()) + ChatColor.WHITE + ")");
//            player.sendMessage(ChatColor.GREEN + "Global total: " + /* TODO: Get global value */ getAmount(player, PermissionValues.WARP_LIMIT_TOTAL) + ChatColor.WHITE + " (created: " + ChatColor.GREEN + this.list.getAmountOfWarps(player.getName(), null, null) + ChatColor.WHITE + ")");
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
                this.manager.addWarp(parameters[1], position, newOwner, this.visibility);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You are unable to create a warp, because you have no position.");
            }
            return true;
        }
    }

    @Override
    public String[] getFullHelpText() {
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
    public String getSmallHelpText() {
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
    public String getCommand() {
        return "warp " + this.commands[0] + " <name> [new owner]";
    }

    @Override
    public boolean listHelp(CommandSender sender) {
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
