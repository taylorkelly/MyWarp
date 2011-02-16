package de.xzise.xwarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.taylorkelly.mywarp.Converter;
import me.taylorkelly.mywarp.WarpList;

public class ConvertCommand extends SubCommand {

	private boolean warning;

	public ConvertCommand(WarpList list, Server server) {
		super(list, server, "convert");
		this.warning = false;
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if ((sender instanceof Player && parameters.length == 0) || (parameters.length == 1)) {
			String owner = "";
			if (sender instanceof Player) {
				owner = ((Player) sender).getName();
			} else {
				owner = parameters[0];
			}
			
			if (!warning) {
				sender.sendMessage(ChatColor.RED + "Warning: " + ChatColor.WHITE + "Only use a copy of warps.txt.");
				sender.sendMessage("This will delete the warps.txt it uses");
				sender.sendMessage("Use " + ChatColor.RED + "'/warp convert'" + ChatColor.WHITE
						+ " again to confirm.");
				warning = true;
			} else {
				Converter.convert(sender, this.server, this.list, owner);
				warning = false;
			}
			return true;
		} else {
			return false;
		}
	}
}
