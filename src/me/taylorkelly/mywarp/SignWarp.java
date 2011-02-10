package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignWarp {
	
	public enum SignWarpType {
		NONE,
		MY_WARP,
		X_WARP;
	}
	
	private Sign sign;
	
	public SignWarp(Sign sign) {
		this.sign = sign;
	}
	
	public SignWarpType getType() {
		return SignWarp.getType(SignWarp.getFilledLines(this.sign));
	}
	
	public boolean warp(WarpList list, Player player) {
		String[] lines = SignWarp.getFilledLines(this.sign);
		SignWarpType type = SignWarp.getType(lines);
		
		switch (type) {
		case MY_WARP :
			list.warpTo(lines[1], null, player, false);
			return true;
		case X_WARP :
			String creator = "";
			if (lines.length == 3) {
				creator = lines[2];
			}
			list.warpTo(lines[1], creator, player, false);
			return true;
		}
		return false;
	}
	
	private static SignWarpType getType(String[] lines) {
		if (lines[0].contains("MyWarp") && lines.length == 2) {
			return SignWarpType.MY_WARP;
		} else if ((lines[0].equalsIgnoreCase("xWarp") || lines[0].matches("(W|w)arp:?")) && (lines.length == 2 || lines.length == 3)) {
			return SignWarpType.X_WARP;
		} else {
			return SignWarpType.NONE;
		}
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
