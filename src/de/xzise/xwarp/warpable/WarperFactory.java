package de.xzise.xwarp.warpable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.metainterfaces.CommandSenderWrapper;

public final class WarperFactory {

    private WarperFactory() {}
    
    public static Warpable getWarpable(Object sender) {
        if (sender instanceof Warpable) {
            return (Warpable) sender;
        } else if (sender instanceof Player) {
            return new WarpablePlayer((Player) sender);
        } else {
            return null;
        }
    }
    
    public static Positionable getPositionable(Object sender) {
        if (sender instanceof Positionable) {
            return (Positionable) sender;
        } else if (sender instanceof Player) {
            return new WarpablePlayer((Player) sender);
        } else {
            return null;
        }
    }

    public static Player getPlayer(Object sender) {
        if (sender instanceof CommandSender) {
            sender = CommandSenderWrapper.getCommandSender((CommandSender) sender);
        }
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            return null;
        }
    }
}
