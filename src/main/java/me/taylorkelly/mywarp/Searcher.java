package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


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
			player.sendMessage(ChatColor.RED + "No warp matches for search: " + ChatColor.GRAY + query);
		} else {
			if (exactMatches.size() > 0) {
				player.sendMessage(ChatColor.YELLOW + "Exact matches for search: " + ChatColor.GRAY + query);
				for (Warp warp : exactMatches) {
					String color;
					if (warp.playerIsCreator(player.getName())) {
						color = ChatColor.AQUA.toString();
					} else if (warp.publicAll) {
						color = ChatColor.GREEN.toString();
					} else {
						color = ChatColor.RED.toString();
					}
					String creator = (warp.creator.equalsIgnoreCase(player.getName())) ? "you" : warp.creator;
					int x = (int) Math.round(warp.x);
					int y = warp.y;
					int z = (int) Math.round(warp.z);
					player.sendMessage(color + "'" + warp.name + "'" + ChatColor.WHITE + " by " + creator + " @(" + x + ", " + y + ", " + z + ")");
				}
			}
			if (matches.size() > 0) {
				player.sendMessage(ChatColor.YELLOW + "Partial matches for search: " + ChatColor.GRAY + query);
				for (Warp warp : matches) {
					String color;
					if (warp.playerIsCreator(player.getName())) {
						color = ChatColor.AQUA.toString();
					} else if (warp.publicAll) {
						color = ChatColor.GREEN.toString();
					} else {
						color = ChatColor.RED.toString();
					}
					String creator = (warp.creator.equalsIgnoreCase(player.getName())) ? "you" : warp.creator;
                    int x = (int) Math.round(warp.x);
                    int y = warp.y;
                    int z = (int) Math.round(warp.z);
					player.sendMessage(color + "'" + warp.name + "'" + ChatColor.WHITE + " by " + creator + " @(" + x + ", " + y + ", " + z + ")");
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
    public String getMatch(String name) {
        if(exactMatches.size() == 1) {
            return exactMatches.get(0).name;
        }
        if(exactMatches.size() == 0 && matches.size() == 1) {
            return matches.get(0).name;
        }
        return name;
    }
}