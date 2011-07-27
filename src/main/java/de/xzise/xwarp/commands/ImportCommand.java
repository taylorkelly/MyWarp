package de.xzise.xwarp.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.DataConnectionFactory;
import de.xzise.xwarp.dataconnections.HModConnection;

public class ImportCommand extends DefaultSubCommand<WarpManager> {

    private final DataConnection data;
    private final File directory;

    public ImportCommand(WarpManager manager, File directory, DataConnection data, Server server) {
        super(manager, server, "import");
        this.data = data;
        this.directory = directory;
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length >= 2) {
            DataConnection connection = DataConnectionFactory.getConnection(this.server, parameters[1]);
            if (connection == null) {
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
                connection.load(new File(this.directory, connection.getFilename()));
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
                if (this.manager.isNameAvailable(warp)) {
                    allowedWarps.add(warp);
                } else {
                    notAllowedWarps.add(warp);
                }
            }

            for (Warp warp : allowedWarps) {
                warp.assignNewId();
            }

            if (allowedWarps.size() > 0) {
                this.manager.blindAdd(allowedWarps);
                this.data.addWarp(allowedWarps.toArray(new Warp[0]));
                sender.sendMessage("Imported " + ChatColor.GREEN + allowedWarps.size() + ChatColor.WHITE + " warps into the database.");
            }

            if (notAllowedWarps.size() > 0) {
                sender.sendMessage(ChatColor.RED + "Found " + notAllowedWarps.size() + " which cause naming conflicts.");
                // Max lines - 1 (for the header) - 1 (for the succeed message)
                if (notAllowedWarps.size() < MinecraftUtil.getMaximumLines(sender) - 1) {
                    for (Warp warp : notAllowedWarps) {
                        sender.sendMessage(ChatColor.GREEN + warp.getName() + ChatColor.WHITE + " by " + ChatColor.GREEN + warp.getOwner());
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
    public String[] getFullHelpText() {
        return new String[] { "Imports a warplist and store it in the database.", "Types could be: 'sqlite' or 'hmod'.", "In hmod mode the creator is either the 3rd parameter (if set) or the initiator (if player) or nobody." };
    }

    @Override
    public String getSmallHelpText() {
        return "Imports a warplist";
    }

    @Override
    public String getCommand() {
        return "warp import <type> [file]";
    }

}
