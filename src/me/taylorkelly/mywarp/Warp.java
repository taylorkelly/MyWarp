package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bukkit.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class Warp {
	
	public enum Visibility {
		PRIVATE,
		PUBLIC,
		GLOBAL;
	}
	
	public int index;
	public String name;
	public String creator;
	public int world;
	public double x;
	public int y;
	public double z;
	public int yaw;
	public int pitch;
	public Visibility visibility;
	public String welcomeMessage;
	public List<String> permissions;
	public List<String> editors;

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
		this.visibility = publicAll ? Visibility.PUBLIC : Visibility.PRIVATE;
		this.permissions = processList(permissions);
		this.welcomeMessage = welcomeMessage;
		if (index > nextIndex)
			nextIndex = index;
		nextIndex++;
	}
	
	public Warp(String name, String creator, Location location) {
		this(nextIndex, name, creator, 0, location.getX(), location.getBlockY(), location.getZ(), normalizeRotation(location.getYaw()), normalizeRotation(location.getPitch()), true, "", "Welcome to '" + name + "'");
	}
	
	private static int normalizeRotation(float degrees) {
		return Math.round(degrees) % 360;
	}

	public Warp(String name, Player creator) {
		this(name, creator.getName(), creator.getLocation());
	}

	public Warp(String name, Location location) {
		this(name, "No Player", location);
	}

	private static List<String> processList(String permissions) {
		String[] names = permissions.split(",");
		List<String> ret = new ArrayList<String>();
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
		if (this.creator.equals(player.getName()) && MyWarp.permissions.permission(player, PermissionTypes.TO_OWN))
			return true;
		if (this.permissions.contains(player.getName()) && MyWarp.permissions.permission(player, PermissionTypes.TO_INVITED))
			return true;
		if (this.visibility == Visibility.PUBLIC && MyWarp.permissions.permission(player, PermissionTypes.TO_OTHER))
			return true;
		// Add to global
		if (this.visibility == Visibility.GLOBAL && MyWarp.permissions.permission(player, PermissionTypes.TO_GLOBAL))
			return true;
		return MyWarp.permissions.permission(player, PermissionTypes.ADMIN_TO_ALL);
	}

	public void warp(Player player) {
		// Better world support
		World world = player.getWorld();
		Location location = new Location(world, x, y, z, yaw, pitch);
		player.teleportTo(location);
	}

	public void update(Player player) {
		this.x = player.getLocation().getX();
		this.y = player.getLocation().getBlockY()	;
		this.z = player.getLocation().getZ();
		this.yaw = Math.round(player.getLocation().getYaw()) % 360;
		this.pitch = Math.round(player.getLocation().getPitch()) % 360;		
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
		return false;
	}
	
	public boolean listWarp(Player player) {
		// Can warp
		if (this.playerCanWarp(player))
			return true;
		// Creator permissions
		if (this.playerIsCreator(player.getName()))
			return true;
		// Admin permissions
		if (MyWarp.permissions.hasAdminPermission(player))
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
