package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.bukkit.*;
import org.bukkit.entity.Player;

public class Warp {

    public int index;
    public String name;
    public String creator;
    public String world;
    public double x;
    public int y;
    public double z;
    public int yaw;
    public int pitch;
    public boolean publicAll;
    public String welcomeMessage;
    public ArrayList<String> permissions;
    public static int nextIndex = 1;

    public Warp(int index, String name, String creator, String world, double x, int y, double z, int yaw, int pitch, boolean publicAll, String permissions,
            String welcomeMessage) {
        this.index = index;
        this.name = name;
        this.creator = creator;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.publicAll = publicAll;
        this.permissions = processList(permissions);
        this.welcomeMessage = welcomeMessage;
        if (index > nextIndex) {
            nextIndex = index;
        }
        nextIndex++;
    }

    public Warp(String name, Player creator) {
        this.index = nextIndex;
        nextIndex++;
        this.name = name;
        this.creator = creator.getName();
        this.world = creator.getWorld().getName();
        this.x = creator.getLocation().getX();
        this.y = creator.getLocation().getBlockY();
        this.z = creator.getLocation().getZ();
        this.yaw = Math.round(creator.getLocation().getYaw()) % 360;
        this.pitch = Math.round(creator.getLocation().getPitch()) % 360;
        this.publicAll = true;
        this.permissions = new ArrayList<String>();
        this.welcomeMessage = "Welcome to '" + name + "'";
    }

    public Warp(String name, Location location) {
        this.index = nextIndex;
        nextIndex++;
        this.name = name;
        this.creator = "No Player";
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getBlockY();
        this.z = location.getZ();
        this.yaw = Math.round(location.getYaw()) % 360;
        this.pitch = Math.round(location.getPitch()) % 360;
        this.publicAll = true;
        this.permissions = new ArrayList<String>();
        this.welcomeMessage = "Welcome to '" + name + "'";
    }

    public Warp(String name, Player creator, boolean b) {
        this.index = nextIndex;
        nextIndex++;
        this.name = name;
        this.creator = creator.getName();
        this.world = creator.getWorld().getName();
        this.x = creator.getLocation().getX();
        this.y = creator.getLocation().getBlockY();
        this.z = creator.getLocation().getZ();
        this.yaw = Math.round(creator.getLocation().getYaw()) % 360;
        this.pitch = Math.round(creator.getLocation().getPitch()) % 360;
        this.publicAll = b;
        this.permissions = new ArrayList<String>();
        this.welcomeMessage = "Welcome to '" + name + "'";
    }

    private ArrayList<String> processList(String permissions) {
        String[] names = permissions.split(",");
        ArrayList<String> ret = new ArrayList<String>();
        for (String name : names) {
            if (name.equals("")) {
                continue;
            }
            ret.add(name.trim());
        }
        return ret;
    }

    public String permissionsString() {
        StringBuilder ret = new StringBuilder();
        for (String name : permissions) {
            ret.append(name);
            ret.append(",");
        }
        return ret.toString();
    }

    public boolean playerCanWarp(Player player) {
        if (creator.equals(player.getName())) {
            return true;
        }
        if (permissions.contains(player.getName())) {
            return true;
        }
        if (WarpPermissions.isAdmin(player) && WarpSettings.adminPrivateWarps) {
            return true;
        }

        return publicAll;
    }

    public void warp(Player player, Server server) {
        World currWorld = null;
        if (world.equals("0")) {
            currWorld = server.getWorlds().get(0);
        } else {
            currWorld = server.getWorld(world);
        }
        if (currWorld != null) {
            Location location = new Location(currWorld, x, y, z, yaw, pitch);
            player.teleportTo(location);
        } else {
            player.sendMessage(ChatColor.RED + "World " + world + " doesn't exist.");
        }
    }

    public boolean playerIsCreator(String name) {
        if (creator.equals(name)) {
            return true;
        }
        return false;
    }

    public void invite(String player) {
        permissions.add(player);
    }

    public boolean playerIsInvited(String player) {
        return permissions.contains(player);
    }

    public void uninvite(String inviteeName) {
        permissions.remove(inviteeName);
    }

    public boolean playerCanModify(Player player) {
        if (creator.equals(player.getName())) {
            return true;
        }
        if (WarpPermissions.isAdmin(player)) {
            return true;
        }
        return false;
    }

    public void setCreator(String giveeName) {
        this.creator = giveeName;
    }

    public String toString() {
        return name;
    }

    public Location getLocation(Server server) {
        World currWorld = null;
        if (world.equals("0")) {
            currWorld = server.getWorlds().get(0);
        } else {
            currWorld = server.getWorld(world);
        }
        if (currWorld == null) {
            return null;
        } else {
            Location location = new Location(currWorld, x, y, z, yaw, pitch);
            return location;
        }
    }
}
