package de.xzise.wrappers.permissions;

import org.bukkit.command.CommandSender;

import de.xzise.wrappers.Wrapper;

public interface PermissionsWrapper extends Wrapper {

    public Boolean has(CommandSender sender, Permission<Boolean> permission);
    
    public Integer getInteger(CommandSender sender, Permission<Integer> permission);

    Double getDouble(CommandSender sender, Permission<Double> permission);
    
    public String getGroup(String world, String player);
    
}
