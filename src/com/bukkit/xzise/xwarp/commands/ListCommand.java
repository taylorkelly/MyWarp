package com.bukkit.xzise.xwarp.commands;

import me.taylorkelly.mywarp.Lister;
import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ListCommand extends SubCommand {

	public ListCommand(WarpList list, Server server) {
		super(list, server);
	}

	@Override
	public int getPossibility(String[] parameters) {
		if (parameters.length > 0) {
			if (!(parameters[0].equalsIgnoreCase("list") || parameters[0].equalsIgnoreCase("ls"))) {
				return -1;
			}
			if (parameters.length <= 2) {
				return 1;
			} else if (parameters.length == 3 && WMPlayerListener.isInteger(parameters[2])) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		Lister lister = new Lister(this.list);
		lister.setPlayer(player);
		
		if (parameters.length == 3 ||(parameters.length == 2 && !WMPlayerListener.isInteger(parameters[1]))) {
			lister.setCreator(this.getPlayer(parameters[1]));
			if (parameters.length == 3) {
				int page = Integer.parseInt(parameters[2]);
				if (page < 1) {
					player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
					return true;
				} else if (page > lister.getMaxPages()) {
					player.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages()
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
				player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
				return true;
			} else if (page > lister.getMaxPages()) {
				player
						.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages()
								+ " pages of warps");
				return true;
			}
			lister.setPage(page);
		} else {
			lister.setPage(1);
		}
		lister.list();
		return true;
	}

}
