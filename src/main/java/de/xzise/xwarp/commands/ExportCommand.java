package de.xzise.xwarp.commands;

import java.io.File;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.DataConnectionFactory;

public class ExportCommand extends DefaultSubCommand {

    private final File pluginPath;

    public ExportCommand(WarpManager list, Server server, File pluginPath) {
        super(list, server, "export");
        this.pluginPath = pluginPath;
    }

    @Override
    protected boolean internalExecute(CommandSender sender, String[] parameters) {
        if (parameters.length > 1) {
            DataConnection connection = DataConnectionFactory.getConnection(this.server, parameters[1]);

            if (connection != null) {
                File file;
                if (parameters.length > 2) {
                    file = new File(parameters[2]);
                } else {
                    file = new File(pluginPath, connection.getFilename());
                }
                try {
                    connection.create(file);
                    connection.addWarp(this.list.getWarps().toArray(new Warp[0]));
                    connection.free();
                } catch (Exception e) {
                    MyWarp.logger.severe("Unable to export warps.", e);
                    sender.sendMessage(ChatColor.RED + "Unable to export warps.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid type given.");
            }
            return true;
        }
        return false;
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Exports the loaded warps to the given file.", "Valid types are: 'sqlite', 'hmod'" };
    }

    @Override
    protected String getSmallHelpText() {
        return "Export warps";
    }

    @Override
    protected String getCommand() {
        return "warp export <type> [file]";
    }
}
