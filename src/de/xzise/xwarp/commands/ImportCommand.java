package de.xzise.xwarp.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.taylorkelly.mywarp.Warp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.HModConnection;
import de.xzise.xwarp.dataconnections.SQLiteConnection;

public class ImportCommand extends DefaultSubCommand {

	private final DataConnection data;
	private final File directory;
	
	public ImportCommand(WarpManager list, File directory, DataConnection data, Server server) {
		super(list, server, "import");
		this.data = data;
		this.directory = directory;
	}
	
	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length >= 2) {
			String type = parameters[1];
			DataConnection connection;
			if (type.equalsIgnoreCase("sqlite")) {
				connection = new SQLiteConnection(this.server);
			} else if (type.equalsIgnoreCase("hmod")) {
				connection = new HModConnection(this.server);
			} else {
				sender.sendMessage(ChatColor.RED + "Unrecognized import type.");
				return true;
			}
			
			if (connection instanceof HModConnection) {
				if (parameters.length > 4) {
					return false;
				}
			} else if (parameters.length > 3) {
				return false;
			}
			
			if (parameters.length >= 3) {
				connection.load(new File(parameters[2]));
			} else {
				connection.loadDefault(this.directory);
			}
			
			List<Warp> warps;
			if (connection instanceof HModConnection) {
				String owner = null;
				if (parameters.length == 4) {
					owner = parameters[3];
				} else if (sender instanceof Player) {
					owner = ((Player) sender).getName();
				}
				
				warps = ((HModConnection) connection).getWarps(owner);
			} else {
				warps = connection.getWarps();
			}
			List<Warp> allowedWarps = new ArrayList<Warp>(warps.size());
			List<Warp> notAllowedWarps = new ArrayList<Warp>(warps.size());
			for (Warp warp : warps) {
				if (this.list.isNameAvailable(warp)) {
					allowedWarps.add(warp);
				} else {
					notAllowedWarps.add(warp);
				}
			}
			
			if (allowedWarps.size() > 0) {
				this.list.blindAdd(allowedWarps);
				this.data.addWarp(allowedWarps.toArray(new Warp[0]));
				sender.sendMessage("Imported " + ChatColor.GREEN + allowedWarps.size() + ChatColor.WHITE + " warps into the database.");
			}
			
			if (notAllowedWarps.size() > 0) {
				sender.sendMessage(ChatColor.RED + "Found " + notAllowedWarps.size() + " which cause naming conflicts.");
				// Max lines - 1 (for the header) - 1 (for the succeed message) 
				if (notAllowedWarps.size() < MinecraftUtil.MAX_LINES_VISIBLE - 1) {
					for (Warp warp : notAllowedWarps) {
						sender.sendMessage(ChatColor.GREEN + warp.name + ChatColor.WHITE + " by " + ChatColor.GREEN + warp.creator);
					}
				}
			}
			connection.free();
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Imports a warplist and store it in the database.", "Types could be: 'sqlite' or 'hmod'.", "In hmod mode the creator is either the 3rd parameter (if set) or the initiator (if player) or nobody." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Imports a warplist";
	}

	@Override
	protected String getCommand() {
		return "warp import <type> [file]";
	}

}
