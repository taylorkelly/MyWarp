package de.xzise.wrappers.permissions;

import org.bukkit.command.CommandSender;


public class DefaultPermissions {

    public boolean has(CommandSender sender, Permission<Boolean> permission) {
        if (permission.getDefault()) {
            return true;
        } else {
            return sender.isOp();
        }
    }

    public int getInteger(CommandSender sender, Permission<Integer> permission) {
        return permission.getDefault();
    }

}
