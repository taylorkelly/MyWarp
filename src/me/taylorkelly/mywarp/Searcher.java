package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.MatchList;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.lister.ListSection;

public class Searcher {
    private WarpManager warpList;
    private CommandSender sender;

    private List<Warp> exactMatches;
    private List<Warp> matches;

    private String query;

    public Searcher(WarpManager warpList) {
        this.warpList = warpList;
    }

    public void addPlayer(CommandSender sender) {
        this.sender = sender;
    }

    public void setQuery(String name) {
        this.query = name;
        MatchList matches = this.warpList.getMatches(name, sender);
        this.exactMatches = matches.exactMatches;
        this.matches = matches.matches;
    }

    public void search(int page) {
        int startIndex = (page - 1) * (MinecraftUtil.getMaximumLines(sender) - 1);
        int elementsLeft = MinecraftUtil.getMaximumLines(sender) - 1;
        int maxPages = (int) Math.ceil((this.exactMatches.size() + this.matches.size()) / (double) (MinecraftUtil.getMaximumLines(sender) - 1));

        if (exactMatches.size() == 0 && matches.size() == 0) {
            this.sender.sendMessage(ChatColor.RED + "No warp matches for search: " + ChatColor.GRAY + query);
        } else if (maxPages < page) {
            this.sender.sendMessage(ChatColor.RED + "There are only " + maxPages + " pages of warps");
        } else {
            List<ListSection> sections = new ArrayList<ListSection>(2);

            if (this.exactMatches.size() > startIndex) {
                ListSection section = new ListSection("Exact matches for search: " + ChatColor.GREEN + this.query, MinecraftUtil.getMaximumLines(sender));
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

            GenericLister.listPage(page, maxPages, this.sender, sections.toArray(new ListSection[0]));
        }
    }
}
