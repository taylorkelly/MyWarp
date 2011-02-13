package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;

import de.xzise.xwarp.commands.ConvertCommand;
import de.xzise.xwarp.commands.CreateCommand;
import de.xzise.xwarp.commands.DeleteCommand;
import de.xzise.xwarp.commands.GiveCommand;
import de.xzise.xwarp.commands.GlobalizeCommand;
import de.xzise.xwarp.commands.HelpCommand;
import de.xzise.xwarp.commands.InviteCommand;
import de.xzise.xwarp.commands.ListCommand;
import de.xzise.xwarp.commands.PermissionsCommand;
import de.xzise.xwarp.commands.PrivatizeCommand;
import de.xzise.xwarp.commands.PublicizeCommand;
import de.xzise.xwarp.commands.ReloadCommand;
import de.xzise.xwarp.commands.SearchCommand;
import de.xzise.xwarp.commands.SubCommand;
import de.xzise.xwarp.commands.UninviteCommand;
import de.xzise.xwarp.commands.UpdateCommand;
import de.xzise.xwarp.commands.WarpToCommand;

public class WMPlayerListener extends PlayerListener {
	
	public static final int LINES_PER_PAGE = 10;
		
	private Plugin plugin;
	private WarpList warpList;
	
	private List<SubCommand> subCommands;

	public WMPlayerListener(Plugin plugin, WarpList warpList) {
		this.plugin = plugin;
		this.warpList = warpList;
	
		this.subCommands = new ArrayList<SubCommand>();
		this.subCommands.add(new ConvertCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new DeleteCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new UpdateCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new PrivatizeCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new PublicizeCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new GlobalizeCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new SearchCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new CreateCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new ListCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new UninviteCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new InviteCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new ReloadCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new HelpCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new GiveCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new PermissionsCommand(this.warpList, plugin.getServer()));
		this.subCommands.add(new WarpToCommand(this.warpList, plugin.getServer()));
		// Initialize sub commands
	}
	
	private class SubCommandElement implements Comparable<SubCommandElement> {
		public final int possibility;
		public final SubCommand command;
		
		public SubCommandElement(int possibility, SubCommand command) {
			this.possibility = possibility;
			this.command = command;
		}

		@Override
		public int compareTo(SubCommandElement o) {
			return (new Integer(possibility)).compareTo(o.possibility);
		}
	}	

	private void subCommandExecutor(Player player, String[] values) {
		String[] parameters = new String[values.length - 1];
		PriorityQueue<SubCommandElement> queue = new PriorityQueue<SubCommandElement>();
		
		for (SubCommand subCommand : this.subCommands) {
			int possibility = subCommand.getPossibility(parameters);
			if (possibility >= 0) {
				queue.add(new SubCommandElement(possibility, subCommand));
			}
		}
		
		Iterator<SubCommandElement> i = queue.iterator();
		while (i.hasNext()) {
			if (i.next().command.execute(player, parameters)) {
				return;
			}
		}
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
			
			this.subCommandExecutor(player, values);
		}
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
		if (helpLines.length / (LINES_PER_PAGE - 1) < page || page <= 0) {
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
