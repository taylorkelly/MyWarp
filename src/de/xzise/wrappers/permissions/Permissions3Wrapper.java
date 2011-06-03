package de.xzise.wrappers.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.Entry;
import com.nijiko.permissions.Entry.EntryVisitor;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import de.xzise.wrappers.permissions.Permissions3Legacy;
import de.xzise.XLogger;

public class Permissions3Wrapper implements PermissionsWrapper {

    private final PermissionHandler handler;
    private final Plugin plugin;
    private final XLogger logger;
    
    public Permissions3Wrapper(Permissions permissions, XLogger logger) {
        this.handler = permissions.getHandler();
        this.plugin = permissions;
        this.logger = logger;
    }
    
    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Boolean has(CommandSender sender, Permission<Boolean> permission) {
        if (sender instanceof Player) {
            return this.handler.permission((Player) sender, permission.getName());
        } else {
            return null;
        }
    }
    
    private <T> T getValue(CommandSender sender, EntryVisitor<T> visitor) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Entry entry = this.handler.getUserObject(player.getWorld().getName(), player.getName());
            if (entry != null) {
                return entry.recursiveCheck(visitor);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    @Override
    public Integer getInteger(CommandSender sender, Permission<Integer> permission) {
        return this.getValue(sender, Permissions3Legacy.getIntVisitor(permission.getName(), this.logger));
    }

    @Override
    public Double getDouble(CommandSender sender, Permission<Double> permission) {
        return this.getValue(sender, Permissions3Legacy.getDoubleVisitor(permission.getName(), this.logger));
    }

    @Override
    public String getGroup(String world, String player) {
        try {
            // 3.0.2 !
            return this.handler.getPrimaryGroup(world, player);
        } catch (NoSuchMethodError e) {
            this.logger.info("You are using a outdated version of Permissions.");
            return Permissions3Legacy.getPrimaryGroup(world, player, this.handler);
        }
    }

}
