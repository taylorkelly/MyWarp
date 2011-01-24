package me.taylorkelly.mywarp;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class WarpList {
	private HashMap<String, Warp> warpList;
	private Server server;

	public WarpList(Server server) {
		this.server = server;
		this.loadFromDatabase();
	}

	public void loadFromDatabase() {
		WarpDataSource.initialize();
		this.warpList = WarpDataSource.getMap();
	}

	public void addWarp(String name, Player player, boolean privateWarp) {
		if (MyWarp.permissions.permission(player,
				privateWarp ? PermissionTypes.CREATE_PRIVATE
						: PermissionTypes.CREATE_PUBLIC)) {
			Warp warp = this.getWarp(name);
			if (warp != null) {
				player.sendMessage(ChatColor.RED + "Warp called '" + name
						+ "' already exists (" + warp.name + ").");
			} else {
				warp = new Warp(name, player);
				this.warpList.put(name.toLowerCase(), warp);
				WarpDataSource.addWarp(warp);
				player.sendMessage(ChatColor.AQUA + "Successfully created '"
						+ name + "'");
				if (privateWarp) {
					this.privatize(name, player);
				} else {
					player.sendMessage("If you'd like to privatize it,");
					player.sendMessage("Use: " + ChatColor.RED
							+ "/warp private " + name);
				}
			}
		} else {
			player.sendMessage(ChatColor.RED
					+ "You have no permission to add a warp.");
		}
	}

	public void blindAdd(Warp warp) {
		warpList.put(warp.name.toLowerCase(), warp);
	}

	public void warpTo(String name, Player player, boolean toAlternative) {
		Warp warp = this.getWarp(name);
		if (warp != null) {
			if (warp.playerCanWarp(player)) {
				warp.warp(player);
				player.sendMessage(ChatColor.AQUA + warp.welcomeMessage);
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to warp to '" + name
						+ "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
			if (this.warpExists("to " + name) && toAlternative) {
				player.sendMessage("Did you mean '" + ChatColor.AQUA + "to "
						+ name + ChatColor.WHITE + "'?");
			}
		}
	}

	public void deleteWarp(String name, Player player) {
		Warp warp = this.getWarp(name);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.ADMIN_DELETE)
					|| warp.playerCanModify(player)) {
				warpList.remove(name);
				WarpDataSource.deleteWarp(warp);
				player.sendMessage(ChatColor.AQUA + "You have deleted '" + name
						+ "'");
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to delete '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void privatize(String name, Player player) {
		Warp warp = this.getWarp(name);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.CREATE_PRIVATE)
					|| warp.playerCanModify(player)) {
				warp.publicAll = false;
				WarpDataSource.publicizeWarp(warp, false);
				player.sendMessage(ChatColor.AQUA + "You have privatized '"
						+ name + "'");
				player.sendMessage("If you'd like to invite others to it,");
				player.sendMessage("Use: " + ChatColor.RED
						+ "/warp invite <player> " + name);
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to privatize '" + name
						+ "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void invite(String name, Player player, String inviteeName) {
		Warp warp = this.getWarp(name);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.ADMIN_INVITE)
					|| warp.playerCanModify(player)) {
				if (warp.playerIsInvited(inviteeName)) {
					player.sendMessage(ChatColor.RED + inviteeName
							+ " is already invited to this warp.");
				} else if (warp.playerIsCreator(inviteeName)) {
					player.sendMessage(ChatColor.RED + inviteeName
							+ " is the creator, of course he's the invited!");
				} else {
					warp.invite(inviteeName);
					WarpDataSource.updatePermissions(warp);
					player.sendMessage(ChatColor.AQUA + "You have invited "
							+ inviteeName + " to '" + name + "'");
					if (warp.publicAll) {
						player.sendMessage(ChatColor.RED + "But '" + name
								+ "' is still public.");
					}
					Player match = server.getPlayer(inviteeName);
					if (match != null) {
						match.sendMessage(ChatColor.AQUA
								+ "You've been invited to warp '" + name
								+ "' by " + player.getName());
						match.sendMessage("Use: " + ChatColor.RED + "/warp "
								+ name + ChatColor.WHITE + " to warp to it.");
					}
				}
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to invite players to '"
						+ name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void publicize(String name, Player player) {
		Warp warp = this.getWarp(name);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.CREATE_PUBLIC)
					|| warp.playerCanModify(player)) {
				warp.publicAll = true;
				WarpDataSource.publicizeWarp(warp, true);
				player.sendMessage(ChatColor.AQUA + "You have publicized '"
						+ name + "'");
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to publicize '" + name
						+ "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void uninvite(String name, Player player, String inviteeName) {
		Warp warp = this.getWarp(name);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.ADMIN_UNINVITE)
					|| warp.playerCanModify(player)) {
				if (!warp.playerIsInvited(inviteeName)) {
					player.sendMessage(ChatColor.RED + inviteeName
							+ " is not invited to this warp.");
				} else if (warp.playerIsCreator(inviteeName)) {
					player.sendMessage(ChatColor.RED
							+ "You can't uninvite yourself. You're the creator!");
				} else {
					warp.uninvite(inviteeName);
					WarpDataSource.updatePermissions(warp);
					player.sendMessage(ChatColor.AQUA + "You have uninvited "
							+ inviteeName + " from '" + name + "'");
					if (warp.publicAll) {
						player.sendMessage(ChatColor.RED + "But '" + name
								+ "' is still public.");
					}
					Player match = server.getPlayer(inviteeName);
					if (match != null) {
						match.sendMessage(ChatColor.RED
								+ "You've been uninvited to warp '" + name
								+ "' by " + player.getName() + ". Sorry.");
					}
				}
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to uninvite players from '"
						+ name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public ArrayList<Warp> getSortedWarps(Player player, int start, int size) {
		ArrayList<Warp> ret = new ArrayList<Warp>();
		List<String> names = new ArrayList<String>(warpList.keySet());
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(names, collator);

		int index = 0;
		int currentCount = 0;
		while (index < names.size() && ret.size() < size) {
			String currName = names.get(index);
			Warp warp = warpList.get(currName);
			if (warp.listWarp(player) || warp.playerCanWarp(player)) {
				if (currentCount >= start) {
					ret.add(warp);
				} else {
					currentCount++;
				}
			}
			index++;
		}
		return ret;
	}

	public ArrayList<Warp> getSortedWarps(Player player, String creator,
			int start, int size) {
		ArrayList<Warp> ret = new ArrayList<Warp>();
		List<String> names = new ArrayList<String>(warpList.keySet());
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(names, collator);

		int index = 0;
		int currentCount = 0;
		while (index < names.size() && ret.size() < size) {
			String currName = names.get(index);
			Warp warp = warpList.get(currName);
			if (warp.listWarp(player) && warp.playerIsCreator(creator)) {
				if (currentCount >= start) {
					ret.add(warp);
				} else {
					currentCount++;
				}
			}
			index++;
		}
		return ret;
	}

	public int getSize() {
		return warpList.size();
	}

	/**
	 * Returns the number of warps the player can modify.
	 * 
	 * @param player
	 *            The given player.
	 * @return The number of warps the player can modify.
	 */
	public int getSize(Player player) {
		int size = 0;
		for (Warp warp : this.warpList.values()) {
			if (warp.listWarp(player)) {
				size++;
			}
		}
		return size;
	}
	
	public int getSize(Player player, String creator) {
		if (creator == null || creator.isEmpty())
			return this.getSize(player);
		
		int size = 0;
		for (Warp warp : this.warpList.values()) {
			if (warp.listWarp(player) && warp.playerIsCreator(creator)) {
				size++;
			}
		}
		return size;
	}

	public MatchList getMatches(String name, Player player) {
		ArrayList<Warp> exactMatches = new ArrayList<Warp>();
		ArrayList<Warp> matches = new ArrayList<Warp>();

		List<String> names = new ArrayList<String>(warpList.keySet());
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(names, collator);

		for (int i = 0; i < names.size(); i++) {
			String currName = names.get(i);
			Warp warp = warpList.get(currName);
			if (warp.playerCanWarp(player)) {
				if (warp.name.equalsIgnoreCase(name)) {
					exactMatches.add(warp);
				} else if (warp.name.toLowerCase().contains(name.toLowerCase())) {
					matches.add(warp);
				}
			}
		}
		return new MatchList(exactMatches, matches);
	}

	public void give(String name, Player player, String giveeName) {
		Warp warp = this.getWarp(name);
		if (warp != null) {
			if (warp.playerCanModify(player)) {
				if (MyWarp.permissions.permission(player,
						PermissionTypes.ADMIN_GIVE)
						|| warp.playerIsCreator(giveeName)) {
					player.sendMessage(ChatColor.RED + giveeName
							+ " is already the owner.");
				} else {
					warp.setCreator(giveeName);
					WarpDataSource.updateCreator(warp);
					player.sendMessage(ChatColor.AQUA + "You have given '"
							+ name + "' to " + giveeName);
					Player match = server.getPlayer(giveeName);
					if (match != null) {
						match.sendMessage(ChatColor.AQUA
								+ "You've been given '" + name + "' by "
								+ player.getName());
					}
				}
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to uninvite players from '"
						+ name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void setMessage(String name, Player player, String message) {
		Warp warp = this.getWarp(name);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.ADMIN_MESSAGE)
					|| warp.playerCanModify(player)) {
				warp.setMessage(message);
				WarpDataSource.updateMessage(warp);
				player.sendMessage(ChatColor.AQUA
						+ "You have set the welcome message for '" + name + "'");
				player.sendMessage(message);
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to change the message from '"
						+ name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void update(String name, Player player) {
		Warp warp = this.getWarp(name);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.ADMIN_UPDATE)
					|| warp.playerCanModify(player)) {
				warp.update(player);
				WarpDataSource.updateWarp(warp);
				player.sendMessage(ChatColor.AQUA + "You have updated warp '"
						+ name + "'");
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to change the position from '"
						+ name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public boolean warpExists(String name) {
		return this.warpList.containsKey(name.toLowerCase());
	}

	public Warp getWarp(String name) {
		return this.warpList.get(name.toLowerCase());
	}
}
