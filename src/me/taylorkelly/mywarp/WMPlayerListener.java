package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import de.xzise.xwarp.CommandMap;

public class WMPlayerListener extends PlayerListener {
	
	private CommandMap commands;

	public WMPlayerListener(CommandMap commands) {
		this.commands = commands;
	}
	
	public void onPlayerCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String[] values = parseCommand(event.getMessage());		

		if (values[0].equalsIgnoreCase("/warp")) {
			
			String[] parameters = Arrays.copyOfRange(values, 1, values.length);
			
			event.setCancelled(true);
			
//			for (int i = 0; i < parameters.length; i++) {
//				player.sendMessage(i + "=" + parameters[i]);
//			}
//			
//			for (int i = 0; i < parameters.length; i++) {
//				MyWarp.logger.info(i + "=" + parameters[i]);
//			}
			
			this.commands.executeCommand(player, parameters);
		}
	}
	
	public void onPlayerCommandPreprocess(PlayerChatEvent event) {
		this.onPlayerCommand(event);
    }
	
	public static String[] parseCommand(String line) {
		return parseLine(line, ' ');
	}
	
	/**
	 * Parses a command line. Reads with quotes/escaping.
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
	public static String[] parseLine(String line, char delimiter) {
		boolean quoted = false;
		boolean escaped = false;
		int lastStart = 0;
		int word = 0;
		String value = "";
		List<String> values = new ArrayList<String>(2);
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
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
				default:
					if (c == delimiter && !quoted) {
						if (lastStart < i) {
							values.add(value);
							value = "";
							word++;
						}
						lastStart = i + 1;						
					} else {
						value += c;
					}
					break;
				}
			}
		}
		if (!value.isEmpty()) {
			values.add(value);
		}
		return values.toArray(new String[0]);
	}
}
