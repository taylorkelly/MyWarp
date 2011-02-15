package me.taylorkelly.mywarp;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class WarpList {
	private Map<String, Warp> global;
	private Map<String, Map<String, Warp>> personal;
	private Server server;
	
	private static final WarpComparator warpComparator = new WarpComparator();

	public WarpList(Server server) {
		this.server = server;
		this.loadFromDatabase();
	}

	private void loadFromDatabase() {
		WarpDataSource.initialize(this.server);
		this.global = new HashMap<String, Warp>();
		this.personal = new HashMap<String, Map<String,Warp>>();
		WarpDataSource.getMap(this.global, this.personal, this.server);
	}
	
	public void loadFromDatabase(Player player) {
		if (MyWarp.permissions.permission(player, PermissionTypes.ADMIN_RELOAD)) {
			this.loadFromDatabase();
		} else {
			player.sendMessage(ChatColor.RED + "You have no permission to reload.");
		}
	}

	public void addWarp(String name, Player player, Visibility visibility) {
		PermissionTypes type;
		switch (visibility) {
		case PRIVATE :
			type = PermissionTypes.CREATE_PRIVATE;
			break;
		case PUBLIC:
			type = PermissionTypes.CREATE_PUBLIC;
			break;
		case GLOBAL :
			type = PermissionTypes.CREATE_GLOBAL;
			break;
		default :
			return;
		}
		if (MyWarp.permissions.permission(player, type)) {
			Warp warp = this.getWarp(name, player.getName());
			Warp globalWarp = this.getWarp(name);
			if (warp != null) {
				player.sendMessage(ChatColor.RED + "Warp called '" + name
						+ "' already exists (" + warp.name + ").");
			} else if (visibility == Visibility.GLOBAL && globalWarp != null) {
				player.sendMessage(ChatColor.RED + "Warp called '" + name
						+ "' already exists (" + globalWarp.name + ").");				
			} else {
				warp = new Warp(name, player);
				putIntoPersonal(this.personal, warp);
				if (warp != this.getWarp(name, player.getName())) {
					MyWarp.logger.severe("Warp saving error!");
				}
				WarpDataSource.addWarp(warp);
				player.sendMessage(ChatColor.AQUA + "Successfully created '"
						+ warp.name + "'");
				switch (visibility) {
				case PRIVATE :
					this.privatize(name, player.getName(), player);
					break;
				case PUBLIC :
					player.sendMessage("If you'd like to privatize it,");
					player.sendMessage("Use: " + ChatColor.RED + "/warp private " + warp.name);
					break;
				case GLOBAL :
					this.globalize(name, player.getName(), player);
					break;
				}
			}
		} else {
			player.sendMessage(ChatColor.RED
					+ "You have no permission to add a warp.");
		}
	}
	
	public static boolean putIntoPersonal(Map<String, Map<String, Warp>> personal, Warp warp) {
		Map<String, Warp> creatorWarps = personal.get(warp.creator.toLowerCase());
		if (creatorWarps == null) {
			creatorWarps = new HashMap<String, Warp>();
			personal.put(warp.creator.toLowerCase(), creatorWarps);
		}
		if (creatorWarps.containsKey(warp.name)) {
			return false;
		}
		creatorWarps.put(warp.name.toLowerCase(), warp);
		return true;
	}

	public void blindAdd(Warp warp) {
		if (this.getWarp(warp.name) == null) {
			this.global.put(warp.name.toLowerCase(), warp);
		} else if (warp.visibility == Visibility.GLOBAL) {
			throw new IllegalArgumentException("A global warp could not override an existing one.");
		}
		if (!putIntoPersonal(personal, warp)) {
			throw new IllegalArgumentException("A personal warp could not override an existing one.");
		}
	}

	public void warpTo(String name, String creator, Player player) {
		Warp warp = this.getWarp(name, creator);
		if (warp != null) {
			if (warp.playerCanWarp(player)) {
				warp.warp(player);
				player.sendMessage(ChatColor.AQUA + warp.welcomeMessage);
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to warp to '" + warp.name
						+ "'");
			}
		} else {
			this.sendMissingWarp(name, creator, player);
		}
	}

	public void deleteWarp(String name, String creator, Player player) {
		Warp warp = this.getWarp(name, creator);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.ADMIN_DELETE)
					|| warp.playerCanModify(player)) {
				this.global.remove(warp);
				if (creator == null || creator.isEmpty()) {
					creator = warp.creator;
				}
				this.personal.get(creator.toLowerCase()).remove(warp);
				WarpDataSource.deleteWarp(warp);
				player.sendMessage(ChatColor.AQUA + "You have deleted '" + name
						+ "'");
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to delete '" + name + "'");
			}
		} else {
			this.sendMissingWarp(name, creator, player);
		}
	}

	public void privatize(String name, String creator, Player player) {
		Warp warp = this.getWarp(name, creator);
		if (warp != null) {
			if ((MyWarp.permissions.permission(player,
					PermissionTypes.CREATE_PRIVATE)
					&& warp.playerCanModify(player))
					|| MyWarp.permissions.permission(player, PermissionTypes.ADMIN_PRIVATE)) {
				warp.visibility = Visibility.PRIVATE;
				WarpDataSource.updateVisibility(warp, warp.visibility);
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
			this.sendMissingWarp(name, creator, player);
		}
	}

	public void invite(String name, String creator, Player player, String inviteeName) {
		Warp warp = this.getWarp(name, creator);
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
					if (warp.visibility != Visibility.PRIVATE) {
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
			this.sendMissingWarp(name, creator, player);
		}
	}

	public void publicize(String name, String creator, Player player) {
		Warp warp = this.getWarp(name, creator);
		if (warp != null) {
			if ((MyWarp.permissions.permission(player,
					PermissionTypes.CREATE_PUBLIC)
					&& warp.playerCanModify(player))
					|| MyWarp.permissions.permission(player, PermissionTypes.ADMIN_PUBLIC)) {
				warp.visibility = Visibility.PUBLIC;
				WarpDataSource.updateVisibility(warp, warp.visibility);
				player.sendMessage(ChatColor.AQUA + "You have publicized '"
						+ warp.name + "'");
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to publicize '" + warp.name
						+ "'");
			}
		} else {
			this.sendMissingWarp(name, creator, player);
		}
	}
	
	public void globalize(String name, String creator, Player player) {
		Warp warp = this.getWarp(name, creator);
		if (warp != null) {
			if ((MyWarp.permissions.permission(player,
					PermissionTypes.CREATE_GLOBAL)
					&& warp.playerCanModify(player))
					|| MyWarp.permissions.permission(player, PermissionTypes.ADMIN_GLOBAL)) {
				Warp existing = this.getWarp(name);
				if (existing == null || existing.visibility != Visibility.GLOBAL) {
					warp.visibility = Visibility.GLOBAL;
					WarpDataSource.updateVisibility(warp, warp.visibility);
					this.global.put(name.toLowerCase(), warp);
					player.sendMessage(ChatColor.AQUA + "You have globalized '"
							+ warp.name + "'");	
				} else if (existing.equals(warp) && existing.visibility == Visibility.GLOBAL) {
					player.sendMessage(ChatColor.RED + "This warp is already globalized.");
				} else {
					player.sendMessage(ChatColor.RED + "One global warp with this name already exists.");
				}
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to globalize '" + warp.name
						+ "'");
			}
		} else {
			this.sendMissingWarp(name, creator, player);
		}
	}

	public void uninvite(String name, String creator, Player player, String inviteeName) {
		Warp warp = this.getWarp(name, creator);
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
					if (warp.visibility != Visibility.PRIVATE) {
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
			this.sendMissingWarp(name, creator, player);
		}
	}
	
	public void sendMissingWarp(String name, String creator, Player player) {
		if (creator == null || creator.isEmpty()) {
			player.sendMessage(ChatColor.RED + "Global warp '" + name + "' doesn't exists.");
		} else {
			player.sendMessage(ChatColor.RED + "Player '" + creator + "' don't owns a warp named '" + name + "'.");
		}
	}

	public ArrayList<Warp> getSortedWarps(Player player, String creator, int start, int size) {
		ArrayList<Warp> ret = new ArrayList<Warp>();
		List<Warp> names;
		if (creator == null || creator.isEmpty()) {
			names = this.getAllWarps();
		} else {
			names = new ArrayList<Warp>();
			Map<String, Warp> map = this.personal.get(creator.toLowerCase());
			if (map != null) {
				names.addAll(map.values());
			}
		}
		
		final Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(names, WarpList.warpComparator);

		int index = 0;
		int currentCount = 0;
		while (index < names.size() && ret.size() < size) {
			Warp warp = names.get(index);
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

	/**
	 * Returns the number of warps the player can modify/use.
	 * 
	 * @param player
	 *            The given player.
	 * @return The number of warps the player can modify/use.
	 */
	public int getSize(Player player) {
		int size = 0;
		for (Map<String, Warp> map : this.personal.values()) {
			for (Warp warp : map.values()) {
				if (warp.listWarp(player)) {
					size++;
				}
			}
		}
		return size;
	}
	
	public int getSize(Player player, String creator) {
		if (creator == null || creator.isEmpty())
			return this.getSize(player);
		else {
			Map<String, Warp> map = this.personal.get(creator);
			return map == null ? 0 : map.size();
		}
	}
	
	public List<Warp> getAllWarps() {
		List<Warp> result = new ArrayList<Warp>();
		for (Map<String, Warp> map : this.personal.values()) {
			result.addAll(map.values());
		}		
		return result;
	}

	public MatchList getMatches(String name, Player player) {
		ArrayList<Warp> exactMatches = new ArrayList<Warp>();
		ArrayList<Warp> matches = new ArrayList<Warp>();
		List<Warp> all = this.getAllWarps();

		final Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(all, WarpList.warpComparator);

		for (int i = 0; i < all.size(); i++) {
			Warp warp = all.get(i);
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

	public void give(String name, String creator, Player player, String giveeName) {
		Warp warp = this.getWarp(name, creator);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.ADMIN_GIVE)
					|| warp.playerCanModify(player)) {
				if (warp.playerIsCreator(giveeName)) {
					player.sendMessage(ChatColor.RED + giveeName
							+ " is already the owner.");
				} else {
					warp.setCreator(giveeName);
					WarpDataSource.updateCreator(warp);
					player.sendMessage(ChatColor.AQUA + "You have given '"
							+ warp.name + "' to " + giveeName);
					Player match = server.getPlayer(giveeName);
					if (match != null) {
						match.sendMessage(ChatColor.AQUA
								+ "You've been given '" + warp.name + "' by "
								+ player.getName());
					}
				}
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to give '"
						+ warp.name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void setMessage(String name, String creator, Player player, String message) {
		Warp warp = this.getWarp(name, creator);
		if (warp != null) {
			if (MyWarp.permissions.permission(player, PermissionTypes.ADMIN_MESSAGE)
					|| warp.playerCanModify(player)) {
				warp.setMessage(message);
				WarpDataSource.updateMessage(warp);
				player.sendMessage(ChatColor.AQUA
						+ "You have set the welcome message for '" + warp.name + "'");
				player.sendMessage(message);
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to change the message from '"
						+ warp.name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void update(String name, String creator, Player player) {
		Warp warp = this.getWarp(name, creator);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.ADMIN_UPDATE)
					|| warp.playerCanModify(player)) {
				warp.update(player);
				WarpDataSource.updateWarp(warp);
				player.sendMessage(ChatColor.AQUA + "You have updated '" + warp.name + "'");
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to change the position from '"
						+ warp.name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}
	
	public void rename(String name, String creator, Player player, String newName) {
		Warp warp = this.getWarp(name, creator);
		if (warp != null) {
			if (MyWarp.permissions.permission(player,
					PermissionTypes.ADMIN_RENAME)
					|| warp.playerCanModify(player)) {
				// Creator has to exists!
				if (creator == null || creator.isEmpty()) {
					creator = warp.creator;
				}
				if (warp.visibility == Visibility.GLOBAL && (this.getWarp(newName) != null)) {
					player.sendMessage(ChatColor.RED + "A global warp with this name already exists!");
				} else if (this.getWarp(newName, creator) != null) {
					player.sendMessage(ChatColor.RED + "You already have a warp with this name.");
				} else {
					// Rename in global list.
					// Is in global list
					if (this.getWarp(name) == warp) {
						this.global.remove(name.toLowerCase());
						// Only put new in, if there is no existing
						if (!this.global.containsKey(newName.toLowerCase())) {
							this.global.put(newName.toLowerCase(), warp);
						}
					}
					// Rename in personal list.
					this.personal.get(creator.toLowerCase()).remove(name.toLowerCase());
					this.personal.get(creator.toLowerCase()).put(newName.toLowerCase(), warp);
					
					warp.rename(newName);
					WarpDataSource.updateWarp(warp);
					player.sendMessage(ChatColor.AQUA + "You have renamed '" + warp.name + "'");
				}
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to change the position from '"
						+ warp.name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public boolean warpExists(String name) {
		return this.global.containsKey(name.toLowerCase());
	}

	/**
	 * Returns the warp specified by the name and the player. If no player is given it searchs the global warps otherwise the player warps.
	 * @param name Name of the warp.
	 * @param creator Creator of the searched warp. If null or empty it searchs the global warps.
	 * @return The searched warp. If the warp or player doesn't exists (if the player is not null or empty) it returns null.
	 */
	public Warp getWarp(String name, String creator) {
		if (creator == null || creator.isEmpty()) {
			return this.global.get(name.toLowerCase());
		} else {
			Map<String, Warp> playerWarps = this.personal.get(creator.toLowerCase());
			if (playerWarps != null) {
				return playerWarps.get(name.toLowerCase());
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Returns the global warp.
	 * @param name The name of the global warp.
	 * @return The global warp. If no global warp exists it returns null.
	 * @see {@link #getWarp(String, String)} Calls this method with creator == <code>null</code>.
	 */
	public Warp getWarp(String name) {
		return this.getWarp(name, null);
	}
}

/**
 * Compares to warps about the name.
 * 
 * @author Fabian Neundorf.
 */
class WarpComparator implements Comparator<Warp> {

	@Override
	public int compare(Warp o1, Warp o2) {
		return o1.name.compareTo(o2.name);
	}	
}