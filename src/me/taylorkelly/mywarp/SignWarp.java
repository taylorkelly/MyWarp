package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignWarp {
	
	public enum SignWarpType {
		MY_WARP,
		X_WARP,
		SINGLE_LINE;
	}
	
	private Sign sign;
	
	public SignWarp(Sign sign) {
		this.sign = sign;
	}
	
	public SignWarpType getType() {
		WarpDestination destination = SignWarp.getType(SignWarp.getFilledLines(this.sign)); 
		return destination == null ? null : destination.type;
	}
	
	public boolean warp(WarpList list, Player player) {
		String[] lines = SignWarp.getFilledLines(this.sign);
		WarpDestination destination = SignWarp.getType(lines);
	
		if (destination != null) {
			list.warpTo(destination.name, destination.creator, player, false);
			return true;
		} else {
			return false;
		}
	}
	
	private static WarpDestination getType(String[] lines) {
		// My Warp
		if (lines[0].contains("MyWarp") && lines.length == 2) {
			return new WarpDestination(lines[1], null, SignWarpType.MY_WARP);
		}
		
		// Single Line
		// line < 0 → No line, line ≥ lines.length → More than one line
		// All other cases: One line & valid!
		int line = -1;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].matches("(W|w)arp:?\\s+.+")) {
				if (line < 0) {
					line = i;
				} else {
					// Invalid
					line = lines.length;
					break;
				}
			}
		}
		
		// If only one valid line found
		if (line >= 0 && line < lines.length) {
			// Extract name:
			String command = lines[line];
			String name = "";
			boolean spaceReach = false;
			for (int i = 0; i < command.length(); i++) {
				if (command.charAt(i) == ' ') {
					spaceReach = true;
				} else if (spaceReach) {
					name = command.substring(i);
					break;
				}
			}
			if (name.isEmpty()) {
				throw new IllegalArgumentException("Empty warp name");
			}
			return new WarpDestination(name, "", SignWarpType.SINGLE_LINE);
		}
		
		// xWarp
		if ((lines[0].equalsIgnoreCase("xWarp") || lines[0].matches("(W|w)arp:?")) && (lines.length == 2 || lines.length == 3)) {
			String creator = "";
			if (lines.length == 3) {
				creator = lines[2];
			}
			return new WarpDestination(lines[1], creator, SignWarpType.X_WARP);
		}
		

		return null;
	}
	
	private static String[] getFilledLines(Sign sign) {
		String[] lines = sign.getLines();
		List<String> result = new ArrayList<String>(); 
		for (int i = 0; i < lines.length; i++) {
			if (!lines[i].trim().isEmpty()) {
				result.add(lines[i]);
			}
		}
		return result.toArray(new String[0]);
	}
}

class WarpDestination {
	public final String name;
	public final String creator;
	public final SignWarp.SignWarpType type;
	
	public WarpDestination(String name, String creator, SignWarp.SignWarpType type) {
		this.name = name;
		this.creator = creator;
		this.type = type;
	}
}
