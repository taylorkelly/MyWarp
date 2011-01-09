package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.bukkit.Color;
import org.bukkit.Player;

public class Searcher {
	private WarpList warpList;
	private Player player;

	private ArrayList<Warp> exactMatches;
	private ArrayList<Warp> matches;

	private String query;

	public Searcher(WarpList warpList) {
		this.warpList = warpList;
	}

	public void addPlayer(Player player) {
		this.player = player;
	}

	public void setQuery(String name) {
		this.query = name;
		MatchList matches = warpList.getMatches(name, player);
		this.exactMatches = matches.exactMatches;
		this.matches = matches.matches;

	}

	public void search() {

		if (exactMatches.size() == 0 && matches.size() == 0) {
			player.sendMessage(Color.RED + "No warp matches for search: " + Color.GRAY + query);
		} else {
			if (exactMatches.size() > 0) {
				player.sendMessage(Color.YELLOW + "Exact matches for search: " + Color.GRAY + query);
				for (Warp warp : exactMatches) {
					String color;
					if (warp.playerIsCreator(player.getName())) {
						color = Color.AQUA.toString();
					} else if (warp.publicAll) {
						color = Color.GREEN.toString();
					} else {
						color = Color.RED.toString();
					}
					String creator = (warp.creator.equalsIgnoreCase(player.getName())) ? "you" : warp.creator;
					int x = warp.x;
					int y = warp.y;
					int z = warp.z;
					player.sendMessage(color + "'" + warp.name + "'" + Color.WHITE + " by " + creator + " @(" + x + ", " + y + ", " + z + ")");
				}
			}
			if (matches.size() > 0) {
				player.sendMessage(Color.YELLOW + "Partial matches for search: " + Color.GRAY + query);
				for (Warp warp : matches) {
					String color;
					if (warp.playerIsCreator(player.getName())) {
						color = Color.AQUA.toString();
					} else if (warp.publicAll) {
						color = Color.GREEN.toString();
					} else {
						color = Color.RED.toString();
					}
					String creator = (warp.creator.equalsIgnoreCase(player.getName())) ? "you" : warp.creator;
					int x = warp.x;
					int y = warp.y;
					int z = warp.z;
					player.sendMessage(color + "'" + warp.name + "'" + Color.WHITE + " by " + creator + " @(" + x + ", " + y + ", " + z + ")");
				}
			}
		}
	}
}

class MatchList {
	public MatchList(ArrayList<Warp> exactMatches, ArrayList<Warp> matches) {
		this.exactMatches = exactMatches;
		this.matches = matches;
	}

	public ArrayList<Warp> exactMatches;
	public ArrayList<Warp> matches;
}
