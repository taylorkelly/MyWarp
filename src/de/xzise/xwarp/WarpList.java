package de.xzise.xwarp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
		
		public Warp getWarp(String playerName) {
			if (this.global != null) {
				return this.global;
			}
			
			Warp yourWarp = this.all.get(playerName.toLowerCase());
			if (yourWarp != null) {
				return yourWarp;
			} else if (this.all.size() == 1) {
				return this.all.values().toArray(new Warp[0])[0];
			} else {
				return null;
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
	
}
