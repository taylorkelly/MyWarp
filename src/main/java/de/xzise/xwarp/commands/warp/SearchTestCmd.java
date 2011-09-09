package de.xzise.xwarp.commands.warp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.MatchList;
import de.xzise.xwarp.Searcher;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.commands.DefaultSubCommand;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class SearchTestCmd extends DefaultSubCommand<WarpManager> {

    public SearchTestCmd(WarpManager list, Server server) {
        super(list, server, "searchtest");
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 3 || parameters.length == 4) {
            if (!XWarp.permissions.permission(sender, PermissionTypes.CMD_SEARCH)) {
                sender.sendMessage(ChatColor.RED + "You have no permission to search warps.");
            } else {
                Integer emCnt = MinecraftUtil.tryAndGetInteger(parameters[1]);
                Integer mCnt = MinecraftUtil.tryAndGetInteger(parameters[2]);
                Integer page = null;
                if (parameters.length == 4) {
                    page = MinecraftUtil.tryAndGetInteger(parameters[3]);
                } else {
                    page = 1;
                }
                if (emCnt != null && mCnt != null && page != null) {
                    Searcher searcher = new Searcher(this.manager);
                    searcher.addPlayer(sender);
                    searcher.setQuery("ttttest");
                    
                    Warp[] warps = this.manager.getWarpObjects().toArray(new Warp[0]);
                    int i = 0;
                    List<Warp> em = new ArrayList<Warp>(emCnt);
                    while (emCnt > 0) {
                        em.add(warps[i++ % warps.length]);
                        emCnt--;
                    }
                    List<Warp> m = new ArrayList<Warp>(mCnt);
                    while (mCnt > 0) {
                        m.add(warps[i++ % warps.length]);
                        mCnt--;
                    }
                    MatchList ml = new MatchList(em, m);
                    searcher.setMatches(ml);
                    
                    searcher.search(page);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid page number entered.");
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "List all warps which name contains the query text." };
    }

    @Override
    public String getSmallHelpText() {
        return "Search for " + ChatColor.GRAY + "<query>";
    }

    @Override
    public String getCommand() {
        return "warp search <query>";
    }
}
