package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.Lister;
import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ListCommand extends SubCommand {

	public ListCommand(WarpList list, Server server) {
		super(list, server, "list", "ls");
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		Lister lister = new Lister(this.list);
		lister.setPlayer(player);
		
		if (parameters.length == 2 && parameters[1].equalsIgnoreCase("legend")) {
			for (String line : Lister.getLegend()) {
				player.sendMessage(line);
			}
		} else if (parameters.length == 3 ||(parameters.length == 2 && !WMPlayerListener.isInteger(parameters[1]))) {
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

	@Override
	public boolean isValid(String[] parameters) {
		return parameters.length == 1 || parameters.length == 2 || (parameters.length == 3 && WMPlayerListener.isInteger(parameters[2]));
	}

}
