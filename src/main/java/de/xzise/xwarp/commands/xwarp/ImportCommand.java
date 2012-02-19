package de.xzise.xwarp.commands.xwarp;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.WPAManager;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.WarpManager.WarpGetter;
import de.xzise.xwarp.commands.DefaultSubCommand;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.DataConnections;
import de.xzise.xwarp.dataconnections.HModConnection;

public class ImportCommand extends DefaultSubCommand<WarpManager> {

    private final File directory;
    private final WPAManager wpaManager;

    public ImportCommand(WarpManager manager, WPAManager wpaManager, File directory, Server server) {
        super(manager, server, "import");
        this.wpaManager = wpaManager;
        this.directory = directory;
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length >= 2) {
            DataConnection connection = DataConnections.getConnection(this.server, parameters[1]);
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

            String owner = null;
            if (connection instanceof HModConnection) {
                if (parameters.length == 4) {
                    owner = parameters[3];
                } else if (sender instanceof Player) {
                    owner = ((Player) sender).getName();
                }
            }

            this.manager.importWarpObjects(connection, new WarpGetter(connection, owner), sender);
            this.wpaManager.importWarpObjects(connection, new WPAManager.WPAGetter(connection), sender);

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
