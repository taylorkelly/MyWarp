package de.xzise.xwarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.PermissionWrapper.PermissionTypes;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.dataconnections.DataConnection;

import me.taylorkelly.mywarp.Converter;
import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;

public class ConvertCommand extends SubCommand {

	private boolean warning;
	private final DataConnection data;

	public ConvertCommand(WarpManager list, Server server, DataConnection data) {
		super(list, server, "convert");
		this.warning = false;
		this.data = data;
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
				List<Warp> warps = Converter.convert(sender, this.server, owner);
				for (Warp warp : warps) {
					this.list.blindAdd(warp);
				}
				this.data.addWarp(warps.toArray(new Warp[warps.size()]));
				warning = false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Converts the hmod warps.txt into the xWarp database.", "If executed ingame the creator is the initiator.", "Otherwise define the creator in the parameter." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Converts the hmod warps.txt";
	}

	@Override
	protected String getCommand() {
		return "warp convert (owner)";
	}
	
	@Override
	protected boolean listHelp(CommandSender sender) {
		return MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_CONVERT);
	}
}
