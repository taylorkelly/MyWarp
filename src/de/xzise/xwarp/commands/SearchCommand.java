package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.Searcher;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class SearchCommand extends SubCommand {

	public SearchCommand(WarpList list, Server server) {
		super(list, server, "search");
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length == 2) {
			Searcher searcher = new Searcher(this.list);
			searcher.addPlayer(sender);
			searcher.setQuery(parameters[1]);
			searcher.search();
			return true;
		} else {
			return false;
		}
	}
}
