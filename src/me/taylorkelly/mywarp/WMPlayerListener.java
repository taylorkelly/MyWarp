package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.List;

import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;

import de.xzise.xwarp.PermissionWrapper;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;


public class WMPlayerListener extends PlayerListener {
	
	public static final int LINES_PER_PAGE = 10;
		
	private boolean warning;
	private Plugin plugin;
	private WarpList warpList;

	public WMPlayerListener(Plugin plugin, WarpList warpList) {
		this.warning = false;
		this.plugin = plugin;
		this.warpList = warpList;
	}

	public void onPlayerCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String[] values = parseLine(event.getMessage());

		// TODO permissions
		if (values[0].equalsIgnoreCase("/warp")) {
			event.setCancelled(true);
			
//			for (int i = 0; i < values.length; i++) {
//				player.sendMessage(i + "=" + values[i]);
//			}
			
			/*
			 * /warp convert
			 */
			if (values.length == 2 && values[1].equalsIgnoreCase("convert")) {
//				player.sendMessage("convert");
				if (!warning) {
					player.sendMessage(ChatColor.RED + "Warning: " + ChatColor.WHITE + "Only use a copy of warps.txt.");
					player.sendMessage("This will delete the warps.txt it uses");
					player.sendMessage("Use " + ChatColor.RED + "'/warp convert'" + ChatColor.WHITE
							+ " again to confirm.");
					warning = true;
				} else {
					Converter.convert(player, plugin.getServer(), warpList);
					warning = false;
				}
				
				/*
				 * /warp list|ls legend
				 */
			} else if (values.length == 3 && (values[1].equalsIgnoreCase("list") || values[1].equalsIgnoreCase("ls"))
					&& values[2].equalsIgnoreCase("legend")) {
//				player.sendMessage("list legend");
				for (String string : Lister.getLegend()) {
					player.sendMessage(string);
				}
				
				/*
				 * /warp list|ls [name] [#]
				 */
			} else if ((values.length == 2 || values.length == 3 || (values.length == 4 && isInteger(values[3])))
					&& (values[1].equalsIgnoreCase("list") || values[1].equalsIgnoreCase("ls"))) {
//				player.sendMessage("list");
				Lister lister = new Lister(warpList);
				lister.setPlayer(player);
				
				if (values.length == 4 ||(values.length == 3 && !isInteger(values[2]))) {
					Player listPlayer = this.plugin.getServer().getPlayer(values[2]);
					// TODO Change to matchPlayer
					String listName = (listPlayer == null) ? values[2] : listPlayer.getName();
										
					lister.setCreator(listName);
					if (values.length == 4) {
						int page = Integer.parseInt(values[3]);
						if (page < 1) {
							player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
							return;
						} else if (page > lister.getMaxPages()) {
							player.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages()
											+ " pages of warps");
							return;
						}
						lister.setPage(page);
					} else {				
						lister.setPage(1);
					}
				} else if (values.length == 3) {
					int page = Integer.parseInt(values[2]);
					if (page < 1) {
						player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
						return;
					} else if (page > lister.getMaxPages()) {
						player
								.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages()
										+ " pages of warps");
						return;
					}
					lister.setPage(page);
				} else {
					lister.setPage(1);
				}
				lister.list();
				
				/*
				 * /warp search <name>
				 */
			} else if (values.length > 2 && (values[1].equalsIgnoreCase("search"))) {
//				player.sendMessage("search");
				Searcher searcher = new Searcher(warpList);
				searcher.addPlayer(player);
				searcher.setQuery(concatArray(values, 2));
				searcher.search();
				
				/*
				 * /warp create <name>
				 */
			} else if (values.length > 2
					&& (values[1].equalsIgnoreCase("create") || values[1].equalsIgnoreCase("createp") || values[1].equalsIgnoreCase("createg")
							|| values[1].equals("+") || values[1].equalsIgnoreCase("+p") || values[1].equalsIgnoreCase("+g"))) {

//				player.sendMessage("create");
				Visibility visibility = Visibility.PUBLIC;
				if (values[1].equalsIgnoreCase("createp") || values[1].equalsIgnoreCase("+p")) {
					visibility = Visibility.PRIVATE;
				} else if (values[1].equalsIgnoreCase("createg") || values[1].equalsIgnoreCase("+g")) {
					visibility = Visibility.GLOBAL;
				}
				
				warpList.addWarp(concatArray(values, 2), player, visibility);
				/*
				 * /warp delete <name>
				 */
			} else if (((values.length == 3) || (values.length == 4)) && (values[1].equalsIgnoreCase("delete") || values[1].equals("-"))) {
//				player.sendMessage("delete");
				String creator = "";
				if (values.length == 4) {
					creator = values[3];
				}

				this.warpList.deleteWarp(values[2], creator, player);
				
				/*
				 * /warp private <name>
				 */
			} else if (((values.length == 3) || (values.length == 4)) && values[1].equalsIgnoreCase("private")) {
//				player.sendMessage("private");
				String creator = "";
				if (values.length == 4) {
					creator = values[3];
				}
				
				this.warpList.privatize(values[2], creator, player);
				
				/*
				 * /warp public <name>
				 */
			} else if (((values.length == 3) || (values.length == 4)) && values[1].equalsIgnoreCase("public")) {
//				player.sendMessage("public");
				String creator = "";
				if (values.length == 4) {
					creator = values[3];
				}
				
				this.warpList.publicize(values[2], creator, player);
				
				/*
				 * /warp global <name>
				 */
			} else if (((values.length == 3) || (values.length == 4)) && values[1].equalsIgnoreCase("global")) {
//				player.sendMessage("global");
				String creator = "";
				if (values.length == 4) {
					creator = values[3];
				}
				
				this.warpList.globalize(values[2], creator, player);
				
				/*
				 * /warp give <player> <name>
				 */
			} else if (((values.length == 4) || (values.length == 5)) && values[1].equalsIgnoreCase("give")) {
//				player.sendMessage("give");
				String giveeName = this.getPlayer(values[values.length - 1]);
				String creator = "";
				if (values.length == 5) {
					creator = values[3];
				}
				
				this.warpList.give(values[2], creator, player, giveeName);

				/*
				 * /warp invite <player> <name>
				 */
			} else if (((values.length == 4) || (values.length == 5)) && values[1].equalsIgnoreCase("invite")) {
//				player.sendMessage("invite");
				String inviteeName = this.getPlayer(values[values.length - 1]);
				String creator = "";
				if (values.length == 5) {
					creator = values[3];
				}
				
				warpList.invite(values[2], creator, player, inviteeName);
				
				/*
				 * /warp uninvite <player> <name>
				 */
			} else if (((values.length == 4) || (values.length == 5)) && values[1].equalsIgnoreCase("uninvite")) {
//				player.sendMessage("uninvite");
				String inviteeName = this.getPlayer(values[values.length - 1]);
				String creator = "";
				if (values.length == 5) {
					creator = values[3];
				}
				
				warpList.uninvite(values[2], creator, player, inviteeName);
				
				/*
				 * /warp message <name> <message>
				 */
			} else if (((values.length == 4) || (values.length == 5))
					&& (values[1].equalsIgnoreCase("message") || values[1].equalsIgnoreCase("msg"))) {
//				player.sendMessage("message");
				String creator = "";
				if (values.length == 5) {
					creator = values[3];
				}
				
				this.warpList.setMessage(values[2], creator, player, values[values.length - 1]);

				/*
				 * /warp update <name>
				 */
			} else if (((values.length == 3) || (values.length == 4)) && (values[1].equalsIgnoreCase("update") || values[1].equalsIgnoreCase("*"))) {
//				player.sendMessage("update");
				String creator = "";
				if (values.length == 4) {
					creator = values[3];
				}
				this.warpList.update(values[2], creator, player);
				
				/*
				 * /warp reload
				 */
			} else if (values.length == 2 && values[1].equalsIgnoreCase("reload")) {
//				player.sendMessage("reload");
				this.warpList.loadFromDatabase(player);
				
				/*
				 * /warp permissions
				 */
			} else if (values.length == 2 && values[1].equalsIgnoreCase("permissions")) {
//				player.sendMessage("permissions");
				
				player.sendMessage("Your permissions:");
				if (!MyWarp.permissions.useOfficial()) {
					player.sendMessage("(Use build in permissions!)");
				}
				for (PermissionTypes type : PermissionWrapper.PermissionTypes.values()) {
					printPermission(type, player);
				}
				
				/*
				 * /warp [to] <name>
				 */
			} else if (values.length > 1 && values.length < 4) {
//				player.sendMessage("to");
				// TODO ChunkLoading
				int start = 1;
				if (values[1].equalsIgnoreCase("to") && values.length > 2) {
					start++;
				}
				String creator = "";
				if (values.length > start + 1) {
					creator = values[start + 1];
				}
				this.warpList.warpTo(values[start], creator, player, start == 1);
			} else {
				int page = 1;
				if (values.length == 3) {
					if (isInteger(values[2])) {
						page = Integer.parseInt(values[2]);
						if (page < 1) {
							player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
							return;
						} else if (page > 2) {
							player.sendMessage(ChatColor.RED + "There are only 2 pages of help");
							return;
						}
					} else {
						player.sendMessage(ChatColor.RED + "Please input a valid number");
						return;
					}
				}
				String[] messages = helpPage(page);

				for (String message : messages) {
					player.sendMessage(message);
				}
			}
		}
	}

	public static void printPermission(PermissionTypes permission, Player player) {
		boolean hasPermission = MyWarp.permissions.permission(player, permission);
		String message = (hasPermission ? ChatColor.GREEN : ChatColor.RED) + permission.name + ": " + (hasPermission ? "Yes": "No");
		player.sendMessage(message);
	}
	
	public String getPlayer(String name) {
		Player player = this.plugin.getServer().getPlayer(name);
		return player == null ? name : player.getName();
	}
	
	private static String[] helpLines = new String[] { 
		ChatColor.RED + "/warp [to] <name>" + ChatColor.WHITE + "  -  Warp to " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp create|+ <name>" + ChatColor.WHITE + "  -  Create warp " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp createp|+p <name>" + ChatColor.WHITE + "  -  Create private warp " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp createg|+g <name>" + ChatColor.WHITE + "  -  Create global warp " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp delete|- <name>" + ChatColor.WHITE + "  -  Delete warp " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp list|ls <#>" + ChatColor.WHITE + "  -  Views warp page " + ChatColor.GRAY + "<#>",
		ChatColor.RED + "/warp update|* <name>" + ChatColor.WHITE + "  -  Updates position of " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp search <query>" + ChatColor.WHITE + "  -  Search for " + ChatColor.GRAY + "<query>",
		ChatColor.RED + "/warp list|ls legend" + ChatColor.WHITE + "  -  Shows the warp page's legend.",
		ChatColor.RED + "/warp message|msg <name> <message>" + ChatColor.WHITE + "  -  Sets message",
		ChatColor.RED + "/warp give <player> <name>" + ChatColor.WHITE + "  -  Give " + ChatColor.GRAY + "<player>" + ChatColor.WHITE + " your " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp invite <player> <name>" + ChatColor.WHITE + "  -  Invite " + ChatColor.GRAY + "<player>" + ChatColor.WHITE + " to " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp uninvite <player> <name>" + ChatColor.WHITE + "  -  Uninvite " + ChatColor.GRAY + "<player>" + ChatColor.WHITE + " to " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp global <name>" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "<name>" + ChatColor.WHITE + " global",
		ChatColor.RED + "/warp public <name>" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "<name>" + ChatColor.WHITE + " public",
		ChatColor.RED + "/warp private <name>" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "<name>" + ChatColor.WHITE + " private",
		ChatColor.RED + "/warp convert" + ChatColor.WHITE + "  -  Imports the hmod file",
		ChatColor.RED + "/warp reload" + ChatColor.WHITE + "  -  Reloads from database",
	};
	
	public static String[] helpPage(int page) {
		if (helpLines.length / (LINES_PER_PAGE - 1) > page) {
			return new String[] { ChatColor.RED + "Invalid /warp help page." };
		}
		List<String> lines = new ArrayList<String>(LINES_PER_PAGE);
		lines.add(ChatColor.RED + "------------------ " + ChatColor.WHITE + "/WARP HELP" + ChatColor.RED + " " + page
				+ "/" + ((int) Math.ceil(helpLines.length / (LINES_PER_PAGE - 1))) + "------------------");
		
		for (int i = page * (LINES_PER_PAGE - 1); i < helpLines.length && i < (page + 1) * (LINES_PER_PAGE - 1); i++) {
			lines.add(helpLines[i]);
		}
		return lines.toArray(new String[0]);
	}

	public static String concatArray(String[] array, int start) {
		String result = "";
		for (int i = start; i < array.length; i++) {
			result += array[i];
			if (i + 1 < array.length)
				result += " ";
		}
		return result;
	}

	/**
	 * Parses a command line. Reads the two first commands like "split" and the
	 * following with quotes/escaping.
	 * 
	 * <ul>
	 * <li>Example 1:
	 * <ul>
	 * <li>/warp create "hello world"</li>
	 * <li>/warp create hello\ world</li>
	 * </ul>
	 * produces:
	 * <ol>
	 * <li>/warp</li>
	 * <li>create</li>
	 * <li>hello world</li>
	 * </ol>
	 * </li>
	 * </ul>
	 * 
	 * @param line
	 *            The command line.
	 * @return The parsed segments.
	 */
	public static String[] parseLine(String line) {
		boolean quoted = false;
		boolean escaped = false;
		int lastStart = 0;
		int word = 0;
		String value = "";
		List<String> values = new ArrayList<String>(2);
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (word < 1) {
				if (c == ' ') {
					values.add(value);
					value = "";
					word++;
				} else {
					value += c;
				}
			} else {
				if (escaped) {
					value += c;
					escaped = false;
				} else {
					switch (c) {
					case '"':
						quoted = !quoted;
						break;
					case '\\':
						escaped = true;
						break;
					case ' ':
						if (!quoted) {
							if (lastStart < i) {
								values.add(value);
								value = "";
								word++;
							}
							lastStart = i + 1;
							break;
						}
					default:
						value += c;
						break;
					}
				}
			}
		}
		if (!value.isEmpty()) {
			values.add(value);
		}
		return values.toArray(new String[0]);
	}
	
	public static String[] parseLine(String[] line) {
		boolean quoted = false;
		boolean escaped = false;
		int lastStart = 0;
		int offset = 0;
		int word = 0;
		String value = "";
		
		List<String> values = new ArrayList<String>();
		// Skip first (is only command)
		values.add(line[0]);
		for (int i = 1; i < line.length; i++) {
			for (int j = 0; j < line[i].length(); j++) {
				char c = line[i].charAt(j);
				if (escaped) {
					value += c;
					escaped = false;
				} else {
					switch (c) {
					case '"':
						quoted = !quoted;
						break;
					case '\\':
						escaped = true;
						break;
					case ' ':
						if (!quoted) {
							if (lastStart < i) {
								values.add(value);
								value = "";
								word++;
							}
							lastStart = i + 1;
							break;
						}
					default:
						value += c;
						break;
					}
				}
			}
			offset += line[i].length();
			if (quoted || escaped) {
				value += " ";
				escaped = false;
			} else {
				values.add(value);
				value = "";
				word++;
				lastStart = offset;
			}
		}
		if (!value.isEmpty()) {
			values.add(value);
		}
		return values.toArray(new String[0]);
	}

	public static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
