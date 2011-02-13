package de.xzise.xwarp.commands;

import java.util.ArrayList;
import java.util.List;

import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class HelpCommand extends SubCommand {

	public HelpCommand(WarpList list, Server server) {
		super(list, server, "help", "?");
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		int page = 1;
		if (parameters.length == 2) {
			if (WMPlayerListener.isInteger(parameters[1])) {
				page = Integer.parseInt(parameters[1]);
				if (page < 1) {
					player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
					return true;
				} else if (page > HELP_PAGES_MAXIMUM) {
					player.sendMessage(ChatColor.RED + "There are only 2 pages of help");
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "Please input a valid number");
				return true;
			}
		}
		String[] messages = helpPage(page);

		for (String message : messages) {
			player.sendMessage(message);
		}
		return true;
	}
	
	private static String[] helpLines = new String[] { 
		ChatColor.RED + "/warp [to] <name>" + ChatColor.WHITE + "  -  Warp to " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp create|+ <name>" + ChatColor.WHITE + "  -  Create warp " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp createp|+p <name>" + ChatColor.WHITE + "  -  Create private warp " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp createg|+g <name>" + ChatColor.WHITE + "  -  Create global warp " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp delete|- <name>" + ChatColor.WHITE + "  -  Delete warp " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp list|ls <#>" + ChatColor.WHITE + "  -  Views warp page " + ChatColor.GRAY + "<#>",
		ChatColor.RED + "/warp update|* <name>" + ChatColor.WHITE + "  -  Updates position of " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp search <query>" + ChatColor.WHITE + "  -  Search for " + ChatColor.GRAY + "<query>",
		ChatColor.RED + "/warp list|ls legend" + ChatColor.WHITE + "  -  Shows the warp page's legend.",
		ChatColor.RED + "/warp message|msg <name> <message>" + ChatColor.WHITE + "  -  Sets message",
		ChatColor.RED + "/warp give <player> <name>" + ChatColor.WHITE + "  -  Give " + ChatColor.GRAY + "<player>" + ChatColor.WHITE + " your " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp invite <player> <name>" + ChatColor.WHITE + "  -  Invite " + ChatColor.GRAY + "<player>" + ChatColor.WHITE + " to " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp uninvite <player> <name>" + ChatColor.WHITE + "  -  Uninvite " + ChatColor.GRAY + "<player>" + ChatColor.WHITE + " to " + ChatColor.GRAY + "<name>",
		ChatColor.RED + "/warp global <name>" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "<name>" + ChatColor.WHITE + " global",
		ChatColor.RED + "/warp public <name>" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "<name>" + ChatColor.WHITE + " public",
		ChatColor.RED + "/warp private <name>" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "<name>" + ChatColor.WHITE + " private",
		ChatColor.RED + "/warp convert" + ChatColor.WHITE + "  -  Imports the hmod file",
		ChatColor.RED + "/warp reload" + ChatColor.WHITE + "  -  Reloads from database",
	};
	
	private static final int HELP_PAGES_MAXIMUM = (int) Math.ceil(helpLines.length / (double) WMPlayerListener.LINES_PER_PAGE);
	
	public static String[] helpPage(int page) {
		List<String> lines = new ArrayList<String>(WMPlayerListener.LINES_PER_PAGE);
		lines.add(ChatColor.RED + "------------------ " + ChatColor.WHITE + "/WARP HELP" + ChatColor.RED + " " + page
				+ "/" + HELP_PAGES_MAXIMUM + "------------------");
		
		for (int i = page * (WMPlayerListener.LINES_PER_PAGE - 1); i < helpLines.length && i < (page + 1) * (WMPlayerListener.LINES_PER_PAGE - 1); i++) {
			lines.add(helpLines[i]);
		}
		return lines.toArray(new String[0]);
	}

	@Override
	public boolean isValid(String[] parameters) {
		return parameters.length == 1 || (parameters.length == 2 && WMPlayerListener.isInteger(parameters[1]));
	}

}
