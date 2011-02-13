package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.Searcher;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class SearchCommand extends FixedParametersCommand {

	public SearchCommand(WarpList list, Server server) {
		super(list, server, 1, "search");
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
