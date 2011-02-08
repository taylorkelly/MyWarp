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

import com.bukkit.xzise.xwarp.PermissionWrapper.PermissionTypes;
import com.bukkit.xzise.xwarp.commands.ConvertCommand;
import com.bukkit.xzise.xwarp.commands.CreateCommand;
import com.bukkit.xzise.xwarp.commands.DeleteCommand;
import com.bukkit.xzise.xwarp.commands.GiveCommand;
import com.bukkit.xzise.xwarp.commands.GlobalizeCommand;
import com.bukkit.xzise.xwarp.commands.HelpCommand;
import com.bukkit.xzise.xwarp.commands.InviteCommand;
import com.bukkit.xzise.xwarp.commands.ListCommand;
import com.bukkit.xzise.xwarp.commands.PermissionsCommand;
import com.bukkit.xzise.xwarp.commands.PrivatizeCommand;
import com.bukkit.xzise.xwarp.commands.PublicizeCommand;
import com.bukkit.xzise.xwarp.commands.ReloadCommand;
import com.bukkit.xzise.xwarp.commands.SearchCommand;
import com.bukkit.xzise.xwarp.commands.SubCommand;
import com.bukkit.xzise.xwarp.commands.UninviteCommand;
import com.bukkit.xzise.xwarp.commands.UpdateCommand;
import com.bukkit.xzise.xwarp.commands.WarpToCommand;

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
	
	public static void printPermission(PermissionTypes permission, Player player) {
		boolean hasPermission = MyWarp.permissions.permission(player, permission);
		String message = (hasPermission ? ChatColor.GREEN : ChatColor.RED) + permission.name + ": " + (hasPermission ? "Yes": "No");
		player.sendMessage(message);
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
