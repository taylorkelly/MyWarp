package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.bukkit.*;

public class Warp {
	public int index;
	public String name;
	public String creator;
	public int world;
	public int x;
	public int y;
	public int z;
	public int yaw;
	public int pitch;
	public boolean publicAll;
	public String welcomeMessage;
	public ArrayList<String> permissions;
	
	public static int nextIndex = 1;
	
	public Warp(int index, String name, String creator, int world, int x, int y, int z, int yaw, int pitch, boolean publicAll, String permissions, String welcomeMessage) {
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
		if(index > nextIndex) nextIndex = index;
		nextIndex++;
	}
	
	public Warp(String name, Player creator) {
		this.index = nextIndex;
		nextIndex++;
		this.name = name;
		this.creator = creator.getName();
		//TODO better world handling
		this.world = 0;
		this.x = creator.getLocation().getBlockX();
		this.y = creator.getLocation().getBlockY();
		this.z = creator.getLocation().getBlockZ();
		this.yaw = Math.round(creator.getLocation().getYaw());
		this.pitch = Math.round(creator.getLocation().getPitch());
		this.publicAll = true;
		this.permissions = new ArrayList<String>();
		this.welcomeMessage = "Welcome to '" + name + "'";
	}

	private ArrayList<String> processList(String permissions) {
		String[] names = permissions.split(",");
		ArrayList<String> ret = new ArrayList<String>();
		for(String name: names) {
			if(name.equals("")) continue;
			ret.add(name.trim());
		}
		return ret;
	}
	
	public String permissionsString() {
		StringBuilder ret = new StringBuilder();
		for(String name: permissions) {
			ret.append(name);
			ret.append(",");
		}
		return ret.toString();
	}

	public boolean playerCanWarp(String player) {
		if(creator.equals(player)) return true;
		if(permissions.contains(player)) return true;
		return publicAll;
	}

	public void warp(Player player) {
		//Better world support
		World world = player.getWorld();
		Location location = new Location(world, x, y, z, yaw, pitch);
		player.teleportTo(location);
	}

	public boolean playerIsCreator(String name) {
		if(creator.equals(name)) return true;
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
}
