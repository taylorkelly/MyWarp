package de.xzise.xwarp.commands;

import java.io.File;
import java.util.List;

import me.taylorkelly.mywarp.Warp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.HModConnection;
import de.xzise.xwarp.dataconnections.SQLiteConnection;

public class ImportCommand extends SubCommand {

	private final Server server;
	private final DataConnection data;
	private final File directory;
	
	public ImportCommand(File directory, DataConnection data, Server server) {
		super("import");
		this.server = server;
		this.data = data;
		this.directory = directory;
	}
	
	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length == 2 || parameters.length == 3) {
			String type = parameters[1];
			DataConnection connection;
			if (type.equalsIgnoreCase("sqlite")) {
				connection = new SQLiteConnection(this.server);
			} else if (type.equalsIgnoreCase("hmod")) {
				connection = new HModConnection(this.server);
			} else {
				sender.sendMessage(ChatColor.RED + "Unrecognized type.");
				return true;
			}
			
			if (parameters.length == 3) {
				connection.load(new File(parameters[2]));
			} else {
				connection.loadDefault(this.directory);
			}
			
			List<Warp> warps;
			if (connection instanceof HModConnection) {
				String owner = null;
				if (sender instanceof Player) {
					owner = ((Player) sender).getName();
				}
				
				warps = ((HModConnection) connection).getWarps(owner);
			} else {
				warps = connection.getWarps();
			}
			//TODO: Test if there are conflicts
			this.data.addWarp(warps.toArray(new Warp[0]));
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Imports a warplist and store it in the database.", "Types could be: 'sqlite' or 'hmod'" };
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
