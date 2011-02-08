package com.bukkit.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import me.taylorkelly.mywarp.WarpList;

/**
 * Command like list/create etc.
 * @author Fabian Neundorf.
 */
public abstract class SubCommand {
	
	protected final WarpList list;
	protected final Server server;
	
	protected SubCommand(WarpList list, Server server) {
		this.list = list;
		this.server = server;
	}

	public String getPlayer(String name) {
		Player player = this.server.getPlayer(name);
		return player == null ? name : player.getName();
	}
	
	/**
	 * Returns the possibility that this command could be meant.
	 * @param parameters The complete parameters including the command.
	 * @return The possibility. If negative it is impossible for this command.
	 */
	public abstract int getPossibility(String[] parameters);
	
	protected abstract boolean internalExecute(Player player, String[] parameters);
	
	public final boolean execute(Player player, String[] parameters) {
		player.sendMessage(this.getClass().getSimpleName());
		
		return this.internalExecute(player, parameters);
	}
}
