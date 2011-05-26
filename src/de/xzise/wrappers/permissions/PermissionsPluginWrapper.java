package de.xzise.wrappers.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.wrappers.Factory;

public class PermissionsPluginWrapper implements PermissionsWrapper {

    private PermissionHandler handler;
    private Plugin plugin;
    
    @Override
    public Boolean has(CommandSender sender, Permission<Boolean> permission) {
        Player player = MinecraftUtil.getPlayer(sender);
        if (player != null) {
            return this.handler.has(player, permission.getName());
        } else {
            return null;
        }
    }

    @Override
    public Integer getInteger(CommandSender sender, Permission<Integer> permission) {
        Player player = MinecraftUtil.getPlayer(sender);
        if (player != null) {
            int i = this.handler.getPermissionInteger(player.getWorld().getName(), player.getName(), permission.getName());
            if (i < 0) {
                return null;
            } else {
                return i;
            }
        } else {
            return null;
        }
    }
    
    @Override
    public Double getDouble(CommandSender sender, Permission<Double> permission) {
        Player player = MinecraftUtil.getPlayer(sender);
        if (player != null) {
            double i = this.handler.getPermissionDouble(player.getWorld().getName(), player.getName(), permission.getName());
            if (i < 0) {
                return null;
            } else {
                return i;
            }
        } else {
            return null;
        }
    }
    
    @Override
    public String getGroup(String world, String player) {
        return this.handler.getGroup(world, player);
    }
    
    public PermissionsPluginWrapper(Permissions plugin) {
        this.handler = plugin.getHandler();
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }
}
