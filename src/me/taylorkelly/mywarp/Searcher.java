package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.lister.GenericLister;

public class Searcher {
	private WarpList warpList;
	private CommandSender sender;

	private ArrayList<Warp> exactMatches;
	private ArrayList<Warp> matches;

	private String query;

	public Searcher(WarpList warpList) {
		this.warpList = warpList;
	}

	public void addPlayer(CommandSender sender) {
		this.sender = sender;
	}

	public void setQuery(String name) {
		this.query = name;
		MatchList matches = warpList.getMatches(name, sender);
		this.exactMatches = matches.exactMatches;
		this.matches = matches.matches;

	}

	public void search() {

		if (exactMatches.size() == 0 && matches.size() == 0) {
			this.sender.sendMessage(ChatColor.RED + "No warp matches for search: " + ChatColor.GRAY + query);
		} else {
			if (exactMatches.size() > 0) {
				this.sender.sendMessage(ChatColor.YELLOW + "Exact matches for search: " + ChatColor.GRAY + query);
				for (Warp warp : exactMatches) {
					Searcher.printWarpLine(warp, this.sender);
				}
			}
			if (matches.size() > 0) {
				this.sender.sendMessage(ChatColor.YELLOW + "Partial matches for search: " + ChatColor.GRAY + query);
				for (Warp warp : matches) {
					Searcher.printWarpLine(warp, this.sender);
				}
			}
		}
	}
	
	public static void printWarpLine(Warp warp, CommandSender player) {
		ChatColor color = player instanceof Player ? GenericLister.getColor(warp, (Player) player) : ChatColor.WHITE;
		String creator = warp.creator;
		if (player instanceof Player && warp.creator.equalsIgnoreCase(((Player) player).getName())) {
			creator = "you";
		}
		player.sendMessage(color + "'" + warp.name + "'" + ChatColor.WHITE + " by " + creator + GenericLister.getLocationString(warp));
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
