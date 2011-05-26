 package de.xzise.wrappers.permissions;

import java.util.Comparator;
import java.util.LinkedHashSet;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.Entry;
import com.nijiko.permissions.Group;
import com.nijiko.permissions.PermissionHandler;
import com.nijiko.permissions.User;
import com.nijikokun.bukkit.Permissions.Permissions;

import de.xzise.XLogger;

public class Permissions3Wrapper implements PermissionsWrapper {

    private final PermissionHandler handler;
    private final Plugin plugin;
    private final XLogger logger;
    
    // Maybe needed to get first found value (and not biggest)?
    private final class FirstFoundComparator<T> implements Comparator<T> {

        private T found = null;
        
        @Override
        public int compare(T o1, T o2) {
            if (this.found != null && o1.equals(this.found)) {
                return 1;
            } else if (this.found == null){
                this.found = o1;
                return 1;
            } else {
                return -1;
            }
        }
    }
    
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

    // For testing purposes!
    private static String otos(Object o) {
        if (o == null) {
            return "null";
        } else {
            return o.toString();
        }
    }
    
    @Override
    public Integer getInteger(CommandSender sender, Permission<Integer> permission) {
        if (sender instanceof Player) {
            // world, entryname, path, isgroup (?)
            Integer a = this.handler.getInfoInteger(((Player) sender).getWorld().getName(), ((Player) sender).getName(), permission.getName(), false, new FirstFoundComparator<Integer>());
            Integer b = this.handler.getInfoInteger(((Player) sender).getWorld().getName(), ((Player) sender).getName(), permission.getName(), false);
            if (a != null && !a.equals(b)) {
                this.logger.severe("Different results in retrieving integer: " + otos(a) + " =: a vs. b := " + otos(b));
            }
            return a;
        } else {
            return null;
        }
    }

    @Override
    public Double getDouble(CommandSender sender, Permission<Double> permission) {
        if (sender instanceof Player) {
            // world, entryname, path, isgroup (?)
            return this.handler.getInfoDouble(((Player) sender).getWorld().getName(), ((Player) sender).getName(), permission.getName(), false, new FirstFoundComparator<Double>());
        } else {
            return null;
        }
    }
    
    /*
     * Copied code from the 3.0.2 source code.
     */
    public String getPrimaryGroup(String world, String user) {
         User u = this.handler.getUserObject(world, user);
         if(u == null)
             return null;
         LinkedHashSet<Entry> parents = u.getParents();
         if(parents == null || parents.isEmpty()) return null;
         for(Entry e : parents) {
             if(e instanceof Group)
                 return e.getName();
         }
         return null;
    }

    @Override
    public String getGroup(String world, String player) {
        try {
            // 3.0.2 !
            return this.handler.getPrimaryGroup(world, player);
        } catch (NoSuchMethodError e) {
            return this.getPrimaryGroup(world, player);
        }
    }

}
