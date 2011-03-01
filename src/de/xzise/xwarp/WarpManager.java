package de.xzise.xwarp;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;
import de.xzise.xwarp.dataconnections.DataConnection;

/**
 * Wraps around {@link WarpList} to provide permissions support.
 * @author Fabian Neundorf
 */
public class WarpManager {
	
	private WarpList list;
	private Server server;
	private DataConnection data;
	
	public WarpManager(Server server, DataConnection data) {
		this.list = new WarpList();
		this.server = server;
		this.data = data;
		this.loadFromDatabase();
	}
	
	private void loadFromDatabase() {
		this.list.loadList(this.data.getWarps());
	}
	
	public void loadFromDatabase(CommandSender sender) {
		if (MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_RELOAD)) {
			this.loadFromDatabase();
		} else {
			sender.sendMessage(ChatColor.RED + "You have no permission to reload.");
		}
	}

	public void addWarp(String name, Player player, String newOwner, Visibility visibility) {
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
			Warp warp = this.list.getWarp(name, newOwner, player.getName());
			Warp globalWarp = (visibility == Visibility.GLOBAL ? this.list.getWarp(name) : null);
			if (warp != null) {
				player.sendMessage(ChatColor.RED + "Warp called '" + name
						+ "' already exists (" + warp.name + ").");
			} else if (visibility == Visibility.GLOBAL && globalWarp != null) {
				player.sendMessage(ChatColor.RED + "Global warp called '" + name
						+ "' already exists (" + globalWarp.name + ").");				
			} else {
				warp = new Warp(name, newOwner, player.getLocation());
				warp.visibility = visibility;
				this.list.addWarp(warp);
				this.data.addWarp(warp);
				player.sendMessage("Successfully created '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
				switch (visibility) {
				case PRIVATE :
					WarpManager.printPrivatizeMessage(player, warp);
					break;
				case PUBLIC :
					if (MyWarp.permissions.permissionOr(player, PermissionTypes.CREATE_PRIVATE, PermissionTypes.ADMIN_PRIVATE)) {
						player.sendMessage("If you'd like to privatize it, use:");
						player.sendMessage(ChatColor.GREEN + "/warp private \"" + warp.name + "\" " + warp.creator);
					}
					break;
				case GLOBAL :
					player.sendMessage("This warp is now global available.");
					break;
				}
			}
		} else {
			player.sendMessage(ChatColor.RED + "You have no permission to add a warp.");
		}
	}
	
	public void blindAdd(Warp warp) {
		this.list.addWarp(warp);
//		if (this.getWarp(warp.name) == null) {
//			this.global.put(warp.name.toLowerCase(), warp);
//		} else if (warp.visibility == Visibility.GLOBAL) {
//			throw new IllegalArgumentException("A global warp could not override an existing one.");
//		}
//		if (!putIntoPersonal(personal, warp)) {
//			throw new IllegalArgumentException("A personal warp could not override an existing one.");
//		}
	}
	
	public void deleteWarp(String name, String creator, CommandSender sender) {
		Warp warp = this.getWarp(name, creator, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (playerCanModifyWarp(sender, warp, Permissions.DELETE)) {
				this.list.deleteWarp(warp);
				this.data.deleteWarp(warp);
				sender.sendMessage("You have deleted '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to delete '" + warp.name + "'");
			}
		} else {
			this.sendMissingWarp(name, creator, sender);
		}
	}
	
	public void give(String name, String creator, CommandSender sender, String giveeName) {
		Warp warp = this.getWarp(name, creator, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (playerCanModifyWarp(sender, warp, Permissions.GIVE)) {
				if (warp.playerIsCreator(giveeName)) {
					sender.sendMessage(ChatColor.RED + giveeName
							+ " is already the owner.");
				} else {
					Warp giveeWarp = this.getWarp(name, giveeName, null);
					if (giveeWarp == null) {
						warp.setCreator(giveeName);
						this.data.updateCreator(warp);
						sender.sendMessage("You have given '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' to " + ChatColor.GREEN + giveeName + ChatColor.WHITE + ".");
						Player match = server.getPlayer(giveeName);
						if (match != null) {
							match.sendMessage("You've been given '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' by " + MinecraftUtil.getName(sender));
						}
					} else {
						sender.sendMessage(ChatColor.RED + "The new owner already has a warp named " + giveeWarp.name);
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to give '"
						+ warp.name + "'");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}
	
	public void privatize(String name, String creator, CommandSender sender) {
		Warp warp = this.list.getWarp(name, creator, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.PRIVATE)) {
				warp.visibility = Visibility.PRIVATE;
				this.list.updateVisibility(warp);
				this.data.updateVisibility(warp);
				WarpManager.printPrivatizeMessage(sender, warp);
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to privatize '" + name
						+ "'");
			}
		} else {
			this.sendMissingWarp(name, creator, sender);
		}
	}

	public void publicize(String name, String creator, CommandSender sender) {
		Warp warp = this.list.getWarp(name, creator, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.PUBLIC)) {
				warp.visibility = Visibility.PUBLIC;
				this.list.updateVisibility(warp);
				this.data.updateVisibility(warp);
				sender.sendMessage(ChatColor.AQUA + "You have publicized '"
						+ warp.name + "'");
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to publicize '" + warp.name
						+ "'");
			}
		} else {
			this.sendMissingWarp(name, creator, sender);
		}
	}
	
	public void globalize(String name, String creator, CommandSender sender) {
		Warp warp = this.list.getWarp(name, creator, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (playerCanModifyWarp(sender, warp, Permissions.GLOBAL)) {
				Warp existing = this.list.getWarp(name);
				if (existing == null || existing.visibility != Visibility.GLOBAL) {
					warp.visibility = Visibility.GLOBAL;
					this.data.updateVisibility(warp);
					this.list.updateVisibility(warp);
					sender.sendMessage("You have globalized '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'");	
				} else if (existing.equals(warp) && existing.visibility == Visibility.GLOBAL) {
					sender.sendMessage(ChatColor.RED + "This warp is already globalized.");
				} else {
					sender.sendMessage(ChatColor.RED + "One global warp with this name already exists.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to globalize '" + warp.name + "'");
			}
		} else {
			this.sendMissingWarp(name, creator, sender);
		}
	}
	
	public void invite(String name, String creator, CommandSender sender, String inviteeName) {
		Warp warp = this.list.getWarp(name, creator, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (playerCanModifyWarp(sender, warp, Permissions.INVITE)) {
				if (warp.playerIsInvited(inviteeName)) {
					sender.sendMessage(ChatColor.RED + inviteeName + " is already invited to this warp.");
				} else if (warp.playerIsCreator(inviteeName)) {
					sender.sendMessage(ChatColor.RED + inviteeName + " is the creator, of course he's the invited!");
				} else {
					warp.invite(inviteeName);
					this.data.updateEditor(warp, inviteeName);
					sender.sendMessage("You have invited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " to '"+ ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
					if (warp.visibility != Visibility.PRIVATE) {
						sender.sendMessage(ChatColor.RED + "But '" + warp.name	+ "' is still public.");
					}
					Player match = this.server.getPlayer(inviteeName);
					if (match != null) {
						match.sendMessage("You've been invited to warp '" + ChatColor.GREEN + warp.name	+ ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ".");
						match.sendMessage("Use: " + ChatColor.GREEN + "/warp [to] \"" + warp.name + "\" " + warp.creator + ChatColor.WHITE + " to warp to it.");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to invite players to '"	+ name + "'.");
			}
		} else {
			this.sendMissingWarp(name, creator, sender);
		}
	}
	
	public void uninvite(String name, String creator, CommandSender sender, String inviteeName) {
		Warp warp = this.list.getWarp(name, creator, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.UNINVITE)) {
				if (!warp.playerIsInvited(inviteeName)) {
					sender.sendMessage(ChatColor.RED + inviteeName + " is not invited to this warp.");
				} else if (warp.playerIsCreator(inviteeName)) {
					sender.sendMessage(ChatColor.RED +  "You can't uninvite yourself. You're the creator!");
				} else {
					warp.addEditor(inviteeName, "w");
					this.data.updateEditor(warp, inviteeName);
					sender.sendMessage("You have uninvited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " from '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
					if (warp.visibility != Visibility.PRIVATE) {
						sender.sendMessage(ChatColor.RED + "But '" + warp.name + "' is still public.");
					}
					Player match = this.server.getPlayer(inviteeName);
					if (match != null) {
						match.sendMessage("You've been uninvited to warp '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ". Sorry.");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to uninvite players from '" + warp.name + "'.");
			}
		} else {
			this.sendMissingWarp(name, creator, sender);
		}
	}
	
	public void rename(String name, String creator, CommandSender sender, String newName) {
		Warp warp = this.getWarp(name, creator, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (playerCanModifyWarp(sender, warp, Permissions.RENAME)) {
				// Creator has to exists!
				if (creator == null || creator.isEmpty()) {
					creator = warp.creator;
				}
				if (warp.visibility == Visibility.GLOBAL && (this.getWarp(newName, null, null) != null)) {
					sender.sendMessage(ChatColor.RED + "A global warp with this name already exists!");
				} else if (this.getWarp(newName, creator, null) != null) {
					sender.sendMessage(ChatColor.RED + "You already have a warp with this name.");
				} else {
					this.list.deleteWarp(warp);
					this.list.addWarp(warp);

					warp.rename(newName);
					this.data.updateName(warp);
					sender.sendMessage(ChatColor.AQUA + "You have renamed '" + warp.name + "'");
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to change the position from '"
						+ warp.name + "'");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}
	
	public void updateLocation(String name, String creator, Player player) {
		Warp warp = this.getWarp(name, creator, player.getName());
		if (warp != null) {
			if (WarpManager.playerCanModifyWarp(player, warp, Permissions.UPDATE)) {
				warp.update(player);
				this.data.updateLocation(warp);
				player.sendMessage("You have updated '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to change the position from '" + warp.name + "'");
			}
		} else {
			this.sendMissingWarp(name, creator, player);
		}
	}
	
	public void addEditor(String name, String owner, CommandSender sender, String editor, String permissions) {
		Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.ADD_EDITOR)) {
				warp.addEditor(editor, permissions);
				this.data.updateEditor(warp, editor);
				sender.sendMessage("You have added " + ChatColor.GREEN + editor + ChatColor.WHITE + " to '" + warp.name + ChatColor.WHITE + "'.");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to add an editor from '" + warp.name + "'");
			}
		} else {
			this.sendMissingWarp(name, owner, sender);
		}
	}

	public void removeEditor(String name, String owner, CommandSender sender, String editor) {
		Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
		if (warp != null) {
			if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.REMOVE_EDITOR)) {
				warp.removeEditor(editor);
				this.data.updateEditor(warp, editor);
				sender.sendMessage("You have removed " + ChatColor.GREEN + editor + ChatColor.WHITE + " from '" + warp.name + ChatColor.WHITE + "'.");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to remove an editor from '" + warp.name + "'");
			}
		} else {
			this.sendMissingWarp(name, owner, sender);
		}
	}
	
	public void warpTo(String name, String creator, Player player, boolean viaSign) {
		Warp warp = this.getWarp(name, creator, player.getName());
		if (warp != null) {
			if (warp.playerCanWarp(player, viaSign)) {
				warp.warp(player);
				player.sendMessage(ChatColor.AQUA + warp.welcomeMessage);
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to warp to '" + warp.name + "'.");
			}
		} else {
			this.sendMissingWarp(name, creator, player);
		}
	}
	
	public Warp getWarp(String name, String owner, String playerName) {
		return this.list.getWarp(name, owner, playerName);
	}
	
	public List<Warp> getWarps() {
		return this.list.getWarps();
	}
	
	public MatchList getMatches(String name, CommandSender sender) {
		ArrayList<Warp> exactMatches = new ArrayList<Warp>();
		ArrayList<Warp> matches = new ArrayList<Warp>();
		List<Warp> all = this.getWarps();

		final Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(all, Warp.WARP_NAME_COMPARATOR);

		for (int i = 0; i < all.size(); i++) {
			Warp warp = all.get(i);
			if (warp.listWarp(sender)) {
				if (warp.name.equalsIgnoreCase(name)) {
					exactMatches.add(warp);
				} else if (warp.name.toLowerCase().contains(name.toLowerCase())) {
					matches.add(warp);
				}
			}
		}
		return new MatchList(exactMatches, matches);
	}
	
	public List<Warp> getSortedWarps(CommandSender sender, String creator, int start, int size) {
		List<Warp> ret = new ArrayList<Warp>(size);
		List<Warp> names;
		if (creator == null || creator.isEmpty()) {
			names = this.getWarps();
		} else {
			names = this.list.getWarps(creator);
		}
		
		final Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(names, Warp.WARP_NAME_COMPARATOR);

		int index = 0;
		int currentCount = 0;
		while (index < names.size() && ret.size() < size) {
			Warp warp = names.get(index);
			if (warp.listWarp(sender)) {
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

	private void sendMissingWarp(String name, String creator, CommandSender sender) {
		if (creator == null || creator.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "Global warp '" + name + "' doesn't exist.");
		} else {
			sender.sendMessage(ChatColor.RED + "Player '" + creator + "' don't owns a warp named '" + name + "'.");
		}
	}
	
	private static void printPrivatizeMessage(CommandSender sender, Warp warp) {
		sender.sendMessage(ChatColor.WHITE + "You have privatized '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'");
		sender.sendMessage("If you'd like to invite others to it, use:");
		sender.sendMessage(ChatColor.GREEN + "/warp invite \"" + warp.name + "\" " + warp.creator + " <player>");
	}
	
	private static boolean playerCanModifyWarp(CommandSender sender, Warp warp, Permissions permission) {
		if (permission.defaultPermission != null) {
			return ((sender instanceof Player && warp.playerCanModify((Player) sender, permission) && MyWarp.permissions.permission(sender, permission.defaultPermission)) || MyWarp.permissions.permission(sender, permission.adminPermission));
		} else {
			return ((sender instanceof Player && warp.playerCanModify((Player) sender, permission)) || MyWarp.permissions.permission(sender, permission.adminPermission));
		}
	}

	public int getSize(CommandSender sender, String creator) {
		return this.list.getSize(sender, creator);
	}
}
