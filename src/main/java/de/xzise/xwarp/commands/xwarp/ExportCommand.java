package de.xzise.xwarp.commands.xwarp;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WPAManager;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.commands.DefaultSubCommand;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.DataConnections;
import de.xzise.xwarp.dataconnections.WarpProtectionConnection;
import de.xzise.xwarp.wrappers.permission.GeneralPermissions;

public class ExportCommand extends DefaultSubCommand<WarpManager> {

    private final File pluginPath;
    private final WPAManager wpaManager;

    public ExportCommand(WarpManager list, WPAManager wpaManager, File pluginPath, Server server) {
        super(list, server, "export");
        this.pluginPath = pluginPath;
        this.wpaManager = wpaManager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length > 1) {
            if (XWarp.permissions.permission(sender, GeneralPermissions.EXPORT)) {
                DataConnection connection = DataConnections.getConnection(this.server, parameters[1]);

                if (connection != null) {
                    File file;
                    if (parameters.length > 2) {
                        file = new File(parameters[2]);
                    } else {
                        file = new File(pluginPath, connection.getFilename());
                    }
                    try {
                        connection.create(file);
                        connection.addWarp(this.manager.getWarpObjects().toArray(new Warp[0]));
                        if (connection instanceof WarpProtectionConnection) {
                            ((WarpProtectionConnection) connection).addProtectionArea(this.wpaManager.getWarpObjects().toArray(new WarpProtectionArea[0]));
                        }
                        connection.free();
                        sender.sendMessage("Successfully xWarp database exported.");
                    } catch (Exception e) {
                        XWarp.logger.severe("Unable to export database.", e);
                        sender.sendMessage(ChatColor.RED + "Unable to export database.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid type given.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to export the database.");
            }
            return true;
        }
        return false;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Exports the loaded warps to the given file.", "Valid types are: 'sqlite', 'hmod'" };
    }

    @Override
    public String getSmallHelpText() {
        return "Export warps";
    }

    @Override
    public String getCommand() {
        return "warp export <type> [file]";
    }
}
