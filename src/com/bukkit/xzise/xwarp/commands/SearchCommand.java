package com.bukkit.xzise.xwarp.commands;

import me.taylorkelly.mywarp.Searcher;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class SearchCommand extends SubCommand {

	public SearchCommand(WarpList list, Server server) {
		super(list, server);
	}

	@Override
	public int getPossibility(String[] parameters) {
		if (parameters.length == 2 && parameters[0].equalsIgnoreCase("search")) {
			return 1;
		}
		return -1;
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		Searcher searcher = new Searcher(this.list);
		searcher.addPlayer(player);
		searcher.setQuery(parameters[1]);
		searcher.search();
		return true;
	}

}
