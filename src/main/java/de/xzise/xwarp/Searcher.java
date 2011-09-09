package de.xzise.xwarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
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
        this.setMatches(this.warpList.getMatches(name, sender));
    }

    public void setMatches(MatchList matches) {
        this.exactMatches = matches.exactMatches;
        this.matches = matches.matches;
    }

    public void search(int page) {
        // DEBUG
        final boolean testOut = page < 0;
        if (page < 0) page = -page;
        // EDBUG END

        final int elementsPerPage = MinecraftUtil.getMaximumLines(this.sender) - 2;
        final int elementsLeftOnMixedPage = elementsPerPage - this.exactMatches.size() % elementsPerPage - 1;
        final int maxPages = (int) (elementsLeftOnMixedPage > 1 ? Math.ceil((this.exactMatches.size() + this.matches.size() + 1) / (double) elementsPerPage) : Math.ceil(this.exactMatches.size() / (double) elementsPerPage) + Math.ceil(this.matches.size() / (double) elementsPerPage));
        int elementsLeft = elementsPerPage + 1;

        if (testOut && page == 1) {
            this.sender.sendMessage("Exact matches: " + this.exactMatches.size());
            for (Warp w : this.exactMatches) {
                this.sender.sendMessage("'" + w.getName() + "' by " + w.getOwner());
            }
            this.sender.sendMessage("Partitial matches: " + this.matches.size());
            for (Warp w : this.matches) {
                this.sender.sendMessage("'" + w.getName() + "' by " + w.getOwner());
            }
            this.sender.sendMessage("Maximum page number: " + maxPages);
        }

        if (exactMatches.size() == 0 && matches.size() == 0) {
            this.sender.sendMessage(ChatColor.RED + "No warp matches for search: " + ChatColor.GRAY + query);
        } else if (page <= 0) {
            this.sender.sendMessage(ChatColor.RED + "Only page numbers greater equals 1 are allowed.");
        } else if (maxPages < page) {
            this.sender.sendMessage(ChatColor.RED + "There are only " + maxPages + " pages of warps");
        } else {
            List<ListSection> sections = new ArrayList<ListSection>(2);

            final int exactMatchesPages = Math.max((int) Math.ceil(this.exactMatches.size() / ((double) elementsPerPage)), 1);
            if (exactMatchesPages >= page) {
                ListSection section = new ListSection("Exact matches for search: " + ChatColor.GREEN + this.query, MinecraftUtil.getMaximumLines(sender));
                elementsLeft--;
                for (int i = (page - 1) * elementsLeft; i < this.exactMatches.size() && elementsLeft > 0; i++) {
                    section.addWarp(this.exactMatches.get(i));
                    elementsLeft--;
                }
                sections.add(section);
            }

            final int offset = (elementsLeftOnMixedPage > 1 && page > exactMatchesPages? elementsLeftOnMixedPage : 0) + Math.max((page - Math.max(exactMatchesPages, 1) - 1) * elementsPerPage, 0);
            // DEBUG
            if (testOut) this.sender.sendMessage("elementsPerPage: " + elementsPerPage + " exactMatchesPages: " + exactMatchesPages + " elementsLeftOnMixedPage: " + elementsLeftOnMixedPage + " offset: " + offset);
            // DEBUG END

            if (this.matches.size() > offset && elementsLeft > 2) {
                ListSection section = new ListSection("Partial matches for search: " + ChatColor.GREEN + this.query);
                elementsLeft--;
                for (int i = offset; i < this.matches.size() && elementsLeft > 0; i++) {
                    section.addWarp(this.matches.get(i));
                    elementsLeft--;
                }
                sections.add(section);
            }

            GenericLister.listPage(page, maxPages, this.sender, sections.toArray(new ListSection[0]));
        }
    }
}
