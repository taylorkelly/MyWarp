package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;

public class WMPlayerListener extends PlayerListener {
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
			/**
			 * /warp convert
			 */
			if (values.length == 2 && values[1].equalsIgnoreCase("convert")) {
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
				/**
				 * /warp list or /warp list #
				 */
			} else if ((values.length == 2 || (values.length == 3 && isInteger(values[2])))
					&& (values[1].equalsIgnoreCase("list") || values[1].equalsIgnoreCase("ls"))) {
				Lister lister = new Lister(warpList);
				lister.addPlayer(player);

				if (values.length == 3) {
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
				/**
				 * /warp search <name>
				 */
			} else if (values.length > 2 && (values[1].equalsIgnoreCase("search"))) {

				Searcher searcher = new Searcher(warpList);
				searcher.addPlayer(player);
				searcher.setQuery(concatArray(values, 2));
				searcher.search();
				/**
				 * /warp create <name>
				 */
			} else if (values.length > 2
					&& (values[1].equalsIgnoreCase("create") || values[1].equalsIgnoreCase("createp")
							|| values[1].equals("+") || values[1].equalsIgnoreCase("+p"))) {

				warpList.addWarp(concatArray(values, 2), player, values[1].equalsIgnoreCase("createp")
						|| values[1].equalsIgnoreCase("+p"));
				/**
				 * /warp delete <name>
				 */
			} else if (values.length > 2 && (values[1].equalsIgnoreCase("delete") || values[1].equals("-"))) {

				warpList.deleteWarp(concatArray(values, 2), player);
				/**
				 * /warp private <name>
				 */
			} else if (values.length > 2 && values[1].equalsIgnoreCase("private")) {

				warpList.privatize(concatArray(values, 2), player);
				/**
				 * /warp public <name>
				 */
			} else if (values.length > 2 && values[1].equalsIgnoreCase("public")) {

				warpList.publicize(concatArray(values, 2), player);
				/**
				 * /warp give <player> <name>
				 */
			} else if (values.length > 3 && values[1].equalsIgnoreCase("give")) {
				Player givee = plugin.getServer().getPlayer(values[2]);
				// TODO Change to matchPlayer
				String giveeName = (givee == null) ? values[2] : givee.getName();

				warpList.give(concatArray(values, 3), player, giveeName);

				/**
				 * /warp invite <player> <name>
				 */
			} else if (values.length > 3 && values[1].equalsIgnoreCase("invite")) {
				Player invitee = plugin.getServer().getPlayer(values[2]);
				// TODO Change to matchPlayer
				String inviteeName = (invitee == null) ? values[2] : invitee.getName();
				
				warpList.invite(concatArray(values, 3), player, inviteeName);
				/**
				 * /warp uninvite <player> <name>
				 */
			} else if (values.length > 3 && values[1].equalsIgnoreCase("uninvite")) {
				Player invitee = plugin.getServer().getPlayer(values[2]);
				// TODO Change to matchPlayer
				String inviteeName = (invitee == null) ? values[2] : invitee.getName();

				warpList.uninvite(concatArray(values, 3), player, inviteeName);
				/**
				 * /warp help
				 */
			} else if (values.length == 2 && (values[1].equalsIgnoreCase("help") || values[1].equalsIgnoreCase("?"))) {
				List<String> messages = new ArrayList<String>();
				messages.add(ChatColor.RED + "-------------------- " + ChatColor.WHITE + "/WARP HELP" + ChatColor.RED
						+ " --------------------");
				messages.add(ChatColor.RED + "/warp to <name>" + ChatColor.WHITE + "  -  Warp to " + ChatColor.GRAY
						+ "<name>");
				messages.add(ChatColor.RED + "/warp <name>" + ChatColor.WHITE + "  -  Warp to " + ChatColor.GRAY
						+ "<name>");
				messages.add(ChatColor.RED + "/warp create/+ <name>" + ChatColor.WHITE + "  -  Create warp "
						+ ChatColor.GRAY + "<name>");
				messages.add(ChatColor.RED + "/warp createp/+p <name>" + ChatColor.WHITE + "  -  Create private warp "
						+ ChatColor.GRAY + "<name>");
				messages.add(ChatColor.RED + "/warp delete/- <name>" + ChatColor.WHITE + "  -  Delete warp "
						+ ChatColor.GRAY + "<name>");
				messages.add(ChatColor.RED + "/warp list/ls <#>" + ChatColor.WHITE + "  -  Views warp page "
						+ ChatColor.GRAY + "<#>");
				messages.add(ChatColor.RED + "/warp search <query>" + ChatColor.WHITE + "  -  Search for "
						+ ChatColor.GRAY + "<query>");
				messages.add(ChatColor.RED + "/warp give <player> <name>" + ChatColor.WHITE + "  -  Give "
						+ ChatColor.GRAY + "<player>" + ChatColor.WHITE + " your " + ChatColor.GRAY + "<name>");
				messages.add(ChatColor.RED + "/warp invite <player> <name>" + ChatColor.WHITE + "  -  Invite "
						+ ChatColor.GRAY + "<player>" + ChatColor.WHITE + " to " + ChatColor.GRAY + "<name>");
				messages.add(ChatColor.RED + "/warp uninvite <player> <name>" + ChatColor.WHITE + "  -  Uninvite "
						+ ChatColor.GRAY + "<player>" + ChatColor.WHITE + " to " + ChatColor.GRAY + "<name>");
				messages.add(ChatColor.RED + "/warp public <name>" + ChatColor.WHITE + "  -  Makes warp "
						+ ChatColor.GRAY + "<name>" + ChatColor.WHITE + " public");
				messages.add(ChatColor.RED + "/warp private <name>" + ChatColor.WHITE + "  -  Makes warp "
						+ ChatColor.GRAY + "<name>" + ChatColor.WHITE + " private");
				messages.add(ChatColor.RED + "/warp message <name> <message>" + ChatColor.WHITE
						+ "  -  Sets the welcome message of warp " + ChatColor.GRAY + "<name>" + ChatColor.WHITE
						+ " to " + ChatColor.GRAY + "<message>" + ChatColor.WHITE);
				for (String message : messages) {
					player.sendMessage(message);
				}
				/*
				 * /warp message <name> <message>
				 */
			} else if (values.length == 4
					&& (values[1].equalsIgnoreCase("message") || values[1].equalsIgnoreCase("msg"))) {

				this.warpList.setMessage(values[2], player, values[3]);
				/*
				 * /warp <name>
				 */
			} else if (values.length > 1) {
				// TODO ChunkLoading
				int start = 1;
				if (values[1].equalsIgnoreCase("to") && values.length > 2) {
					start++;
				}
				this.warpList.warpTo(concatArray(values, start), player, start == 1);
			} else {
				// TODO help?
				player.sendMessage(ChatColor.RED + "Invalid /warp command!" + ChatColor.WHITE + "Use " + ChatColor.RED
						+ "/warp help " + ChatColor.WHITE + "for help.");
			}
		}
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
			if (word < 2) {
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

	public static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
