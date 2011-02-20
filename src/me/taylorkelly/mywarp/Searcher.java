package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.lister.ListSection;

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

	public void search(int page) {
		int startIndex = (page - 1) * (MinecraftUtil.MAX_LINES_VISIBLE - 1);
		int elementsLeft = MinecraftUtil.MAX_LINES_VISIBLE - 1;
		int maxPages = (int) Math.ceil((this.exactMatches.size() + this.matches.size()) / (double) (MinecraftUtil.MAX_LINES_VISIBLE - 1));
		
		if (exactMatches.size() == 0 && matches.size() == 0) {
			this.sender.sendMessage(ChatColor.RED + "No warp matches for search: " + ChatColor.GRAY + query);
		} else if (maxPages < page) {
			this.sender.sendMessage(ChatColor.RED + "There are only " + maxPages + " pages of warps");
		} else {
			List<ListSection> sections = new ArrayList<ListSection>(2);
				
			if (this.exactMatches.size() > startIndex) {
				ListSection section = new ListSection("Exact matches for search: " + ChatColor.GREEN + this.query);
				elementsLeft--;
				for (Warp warp : exactMatches) {
					if (elementsLeft > 0) {
						section.addWarp(warp);
						elementsLeft--;
					} else {
						break;
					}
				}
				sections.add(section);
			}
			if (this.matches.size() > startIndex && elementsLeft > 2) {
				ListSection section = new ListSection("Partial matches for search: " + ChatColor.GREEN + this.query);
				elementsLeft--;
				for (Warp warp : matches) {
					if (elementsLeft > 0) {
						section.addWarp(warp);
						elementsLeft--;
					} else {
						break;
					}
				}
				sections.add(section);
			}
			
			GenericLister.listPage(page, maxPages, sections.toArray(new ListSection[0]), this.sender);
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
