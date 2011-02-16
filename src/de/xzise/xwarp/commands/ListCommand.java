package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.Lister;
import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ListCommand extends SubCommand {

	public ListCommand(WarpList list, Server server) {
		super(list, server, "list", "ls");
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length == 3 && !WMPlayerListener.isInteger(parameters[2])) {
			return false;
		}
		
		if (parameters.length == 2 && parameters[1].equalsIgnoreCase("legend")) {
			if (sender instanceof ConsoleCommandSender) {
				sender.sendMessage("No colors in console, so this command is useless here!");
			}
			for (String line : Lister.getLegend()) {
				sender.sendMessage(line);
			}
		} else {
			Lister lister = new Lister(this.list);
			lister.setPlayer(sender);
			
			if (parameters.length == 3 || (parameters.length == 2 && !WMPlayerListener.isInteger(parameters[1]))) {
				lister.setCreator(this.getPlayer(parameters[1]));
				if (parameters.length == 3) {
					int page = Integer.parseInt(parameters[2]);
					if (page < 1) {
						sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
						return true;
					} else if (page > lister.getMaxPages()) {
						sender.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages()
										+ " pages of warps");
						return true;
					}
					lister.setPage(page);
				} else {				
					lister.setPage(1);
				}
			} else if (parameters.length == 2) {
				int page = Integer.parseInt(parameters[1]);
				if (page < 1) {
					sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
					return true;
				} else if (page > lister.getMaxPages()) {
					sender.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages()
									+ " pages of warps");
					return true;
				}
				lister.setPage(page);
			} else {
				lister.setPage(1);
			}
			lister.list();
		}
		return true;
	}
}
