package de.xzise.xwarp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;

public class WarpList {

	private class GlobalMap {
		
		private Warp global;
		private final Map<String, Warp> all;
		
		public GlobalMap() {
			this.global = null;
			this.all = new HashMap<String, Warp>();
		}
		
		public void put(Warp warp) {
			if (warp.visibility == Visibility.GLOBAL) {
				this.global = warp;
			}
			this.all.put(warp.creator.toLowerCase(), warp);
		}
		
		public void delete(Warp warp) {
			if (warp.equals(this.global)) {
				this.global = null;
			}
			this.all.remove(warp.creator.toLowerCase());
		}
		
		public Warp getWarp(String playerName) {
			if (this.global != null) {
				return this.global;
			}
			
			if (this.all.size() == 1) {
				return this.all.values().toArray(new Warp[1])[0];
			} else if (playerName == null || playerName.isEmpty()) {
				return this.all.get(playerName.toLowerCase());	
			} else {
				return null;
			}
		}
		
		public void updateGlobal(Warp warp) {
			if (this.global == null && warp.visibility == Visibility.GLOBAL) {
				this.global = warp;
			} else if (warp.equals(this.global) && warp.visibility != Visibility.GLOBAL) {
				this.global = null;
			}
		}
		
		public void clear() {
			this.all.clear();
			this.global = null;
		}
	}
	
	// Warps sorted by creator, name
	private final Map<String, Map<String, Warp>> personal;
	// Warps sorted by name
	private final Map<String, GlobalMap> global;
	
	public WarpList() {
		this.personal = new HashMap<String, Map<String, Warp>>();
		this.global = new HashMap<String, GlobalMap>();
	}
	
	public void loadList(Collection<Warp> warps) {
		for (Map<String, Warp> personalWarps : this.personal.values()) {
			personalWarps.clear();
		}
		for (GlobalMap globalWarps : this.global.values()) {
			globalWarps.clear();
		}
		
		// Load elements here
		for (Warp warp : warps) {
			this.addWarp(warp);
		}
	}
	
	public void addWarp(Warp warp) {
		GlobalMap namedWarps = this.global.get(warp.name.toLowerCase());
		if (namedWarps == null) {
			namedWarps = new GlobalMap();
			this.global.put(warp.name.toLowerCase(), namedWarps);
		}
		namedWarps.put(warp);
		
		Map<String, Warp> personalWarps = this.personal.get(warp.creator.toLowerCase());
		if (personalWarps == null) {
			personalWarps = new HashMap<String, Warp>();
			this.personal.put(warp.creator.toLowerCase(), personalWarps);
		}
		personalWarps.put(warp.name.toLowerCase(), warp);
	}
	
	public void deleteWarp(Warp warp) {
		this.global.get(warp.name.toLowerCase()).delete(warp);
		this.personal.get(warp.creator.toLowerCase()).remove(warp.name.toLowerCase());
	}
	
	public void updateVisibility(Warp warp) {
		this.global.get(warp.name.toLowerCase()).updateGlobal(warp);
	}
	
	public Warp getWarp(String name, String owner, String playerName) {
		if (owner == null || owner.isEmpty()) {
			GlobalMap namedWarps = this.global.get(name.toLowerCase());
			if (namedWarps != null) {
				return namedWarps.getWarp(playerName);
			} else {
				return null;
			}
		} else {
			Map<String, Warp> ownerWarps = this.personal.get(owner.toLowerCase());
			if (ownerWarps != null) {
				return ownerWarps.get(name.toLowerCase());
			}
			return null;
		}
	}
	
	public Warp getWarp(String name) {
		return this.getWarp(name, null, null);
	}

	public List<Warp> getWarps() {
		List<Warp> result = new ArrayList<Warp>();
		for (Map<String, Warp> map : this.personal.values()) {
			result.addAll(map.values());
		}		
		return result;
	}

	public List<Warp> getWarps(String owner) {
		List<Warp> result = null;
		Map<String, Warp> personalWarps = this.personal.get(owner.toLowerCase());
		if (personalWarps != null) {
			result = new ArrayList<Warp>(personalWarps.size());
			result.addAll(personalWarps.values());
		}
		return result;
	}

	/**
	 * Returns the number of warps the player can modify/use.
	 * 
	 * @param player
	 *            The given player.
	 * @return The number of warps the player can modify/use.
	 */
	public int getSize(CommandSender sender) {
		int size = 0;
		for (Map<String, Warp> map : this.personal.values()) {
			size += this.getSize(sender, map);
		}
		return size;
	}
	
	public int getSize(CommandSender sender, String creator) {
		if (creator == null || creator.isEmpty())
			return this.getSize(sender);
		else {
			Map<String, Warp> map = this.personal.get(creator.toLowerCase());
			return map == null ? 0 : this.getSize(sender, map);
		}
	}
	
	private int getSize(CommandSender sender, Map<String, Warp> map) {
		int size = 0;
		for (Warp warp : map.values()) {
			if (warp.listWarp(sender)) {
				size++;
			}
		}
		return size;
	}
	
}
