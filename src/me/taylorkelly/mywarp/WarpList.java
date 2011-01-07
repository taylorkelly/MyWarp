package me.taylorkelly.mywarp;

import java.util.HashMap;

import org.bukkit.*;

public class WarpList {
	private HashMap<String, Warp> warpList;
	private Server server;

	public WarpList(Server server) {
		this.server = server;
		WarpDataSource.initialize();
		warpList = WarpDataSource.getMap();
	}

	public void addWarp(String name, Player player) {
		if (warpList.containsKey(name)) {
			player.sendMessage(Color.RED + "Warp called '" + name + "' already exists.");
		} else {
			Warp warp = new Warp(name, player);
			warpList.put(name, warp);
			WarpDataSource.addWarp(warp);
			player.sendMessage(Color.AQUA + "Successfully created '" + name + "'");
			player.sendMessage("If you'd like to privatize it,");
			player.sendMessage("Use: " + Color.RED + "/warp private " + name);
		}
	}
	
	public void blindAdd(Warp warp) {
		warpList.put(warp.name, warp);
	}

	public void warpTo(String name, Player player) {
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanWarp(player.getName())) {
				warp.warp(player);
				player.sendMessage(Color.AQUA + warp.welcomeMessage);
			} else {
				player.sendMessage(Color.RED + "You do not have permission to warp to '" + name + "'");
			}
		} else {
			player.sendMessage(Color.RED + "No such warp '" + name + "'");
		}
	}

	public void deleteWarp(String name, Player player) {
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerIsCreator(player.getName())) {
				warpList.remove(name);
				WarpDataSource.deleteWarp(warp);
				player.sendMessage(Color.AQUA + "You have deleted '" + name + "'");
			} else {
				player.sendMessage(Color.RED + "You do not have permission to delete '" + name + "'");
			}
		} else {
			player.sendMessage(Color.RED + "No such warp '" + name + "'");
		}
	}

	public void privatize(String name, Player player) {
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerIsCreator(player.getName())) {
				warp.publicAll = false;
				WarpDataSource.publicizeWarp(warp, false);
				player.sendMessage(Color.AQUA + "You have privatized '" + name + "'");
				player.sendMessage("If you'd like to invite others to it,");
				player.sendMessage("Use: " + Color.RED + "/warp invite <player> " + name);
			} else {
				player.sendMessage(Color.RED + "You do not have permission to privatize '" + name + "'");
			}
		} else {
			player.sendMessage(Color.RED + "No such warp '" + name + "'");
		}
	}

	public void invite(String name, Player player, String inviteeName) {
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerIsCreator(player.getName())) {
				if (warp.playerIsInvited(inviteeName)) {
					player.sendMessage(Color.RED + inviteeName + " is already invited to this warp.");
				} else if(warp.playerIsCreator(inviteeName)) {
					player.sendMessage(Color.RED + inviteeName + " is the creator, of course he's the invited!");
				}else {
					warp.invite(inviteeName);
					WarpDataSource.updatePermissions(warp);
					player.sendMessage(Color.AQUA + "You have invited " + inviteeName + " to '" + name + "'");
					if (warp.publicAll) {
						player.sendMessage(Color.RED + "But '" + name + "' is still public.");
					}
					Player match = server.getPlayer(inviteeName);
					if (match != null) {
						match.sendMessage(Color.AQUA + "You've been invited to warp '" + name + "' by " + player.getName());
						match.sendMessage("Use: " + Color.RED + "/warp " + name + Color.WHITE + " to warp to it.");
					}
				}
			} else {
				player.sendMessage(Color.RED + "You do not have permission to invite players to '" + name + "'");
			}
		} else {
			player.sendMessage(Color.RED + "No such warp '" + name + "'");
		}
	}

	public void publicize(String name, Player player) {
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerIsCreator(player.getName())) {
				warp.publicAll = true;
				WarpDataSource.publicizeWarp(warp, true);
				player.sendMessage(Color.AQUA + "You have publicized '" + name + "'");
			} else {
				player.sendMessage(Color.RED + "You do not have permission to publicize '" + name + "'");
			}
		} else {
			player.sendMessage(Color.RED + "No such warp '" + name + "'");
		}
	}

	public void uninvite(String name, Player player, String inviteeName) {
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerIsCreator(player.getName())) {
				if (!warp.playerIsInvited(inviteeName)) {
					player.sendMessage(Color.RED + inviteeName + " is not invited to this warp.");
				} else if (warp.playerIsCreator(inviteeName)) {
					player.sendMessage(Color.RED + "You can't uninvite yourself. You're the creator!");
				} else {
					warp.uninvite(inviteeName);
					WarpDataSource.updatePermissions(warp);
					player.sendMessage(Color.AQUA + "You have uninvited " + inviteeName + " from '" + name + "'");
					if (warp.publicAll) {
						player.sendMessage(Color.RED + "But '" + name + "' is still public.");
					}
					Player match = server.getPlayer(inviteeName);
					if (match != null) {
						match.sendMessage(Color.RED + "You've been uninvited to warp '" + name + "' by " + player.getName() + ". Sorry.");
					}
				}
			} else {
				player.sendMessage(Color.RED + "You do not have permission to uninvite players from '" + name + "'");
			}
		} else {
			player.sendMessage(Color.RED + "No such warp '" + name + "'");
		}
	}
}
