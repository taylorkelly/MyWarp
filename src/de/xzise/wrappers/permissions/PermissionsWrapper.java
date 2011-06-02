package de.xzise.wrappers.permissions;

import org.bukkit.command.CommandSender;

import de.xzise.wrappers.Wrapper;

public interface PermissionsWrapper extends Wrapper {

    Boolean has(CommandSender sender, Permission<Boolean> permission);
    
    Integer getInteger(CommandSender sender, Permission<Integer> permission);

    Double getDouble(CommandSender sender, Permission<Double> permission);

    String getGroup(String world, String player);
    
}
