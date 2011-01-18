package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Warp {
	public int index;
	public String name;
	public String creator;
	public int world;
	public double x;
	public int y;
	public double z;
	public int yaw;
	public int pitch;
	public boolean publicAll;
	public String welcomeMessage;
	public ArrayList<String> permissions;

	public static int nextIndex = 1;

	public Warp(int index, String name, String creator, int world, double x,
			int y, double z, int yaw, int pitch, boolean publicAll,
			String permissions, String welcomeMessage) {
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
		if (index > nextIndex)
			nextIndex = index;
		nextIndex++;
	}

	public Warp(String name, Player creator) {
		this.index = nextIndex;
		nextIndex++;
		this.name = name;
		this.creator = creator.getName();
		// TODO better world handling
		this.world = 0;
		this.x = creator.getLocation().getX();
		this.y = creator.getLocation().getBlockY()	;
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
		// TODO better world handling
		this.world = 0;
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.yaw = Math.round(location.getYaw());
		this.pitch = Math.round(location.getPitch());
		this.publicAll = true;
		this.permissions = new ArrayList<String>();
		this.welcomeMessage = "Welcome to '" + name + "'";
	}

	private ArrayList<String> processList(String permissions) {
		String[] names = permissions.split(",");
		ArrayList<String> ret = new ArrayList<String>();
		for (String name : names) {
			if (name.equals(""))
				continue;
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
		if (this.creator.equals(player.getName()))
			return true;
		if (this.permissions.contains(player.getName()))
			return true;
		if (((CraftPlayer) player).isOp())
			return true;
		return publicAll;
	}

	public void warp(Player player) {
		// Better world support
		World world = player.getWorld();
		Location location = new Location(world, x, y, z, yaw, pitch);
		player.teleportTo(location);
	}

	public boolean playerIsCreator(String name) {
		if (creator.equals(name))
			return true;
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
		if (creator.equals(player.getName()))
			return true;
		if (((CraftPlayer) player).isOp())
			return true;
		return false;
	}

	public void setCreator(String giveeName) {
		this.creator = giveeName;
	}
	
	public void setMessage(String message) {
		this.welcomeMessage = message;
	}
}
