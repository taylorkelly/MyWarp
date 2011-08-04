package de.xzise.xwarp.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Server;

import de.xzise.commands.CommonCommandMap;
import de.xzise.commands.CommonHelpCommand;
import de.xzise.commands.SubCommand;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WPAManager;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.commands.xwarp.CDWUConvCommand;
import de.xzise.xwarp.commands.xwarp.ExportCommand;
import de.xzise.xwarp.commands.xwarp.ImportCommand;
import de.xzise.xwarp.commands.xwarp.PermissionsCommand;
import de.xzise.xwarp.commands.xwarp.ReloadCommand;
import de.xzise.xwarp.commands.xwarp.StatusCommand;

public class ManageCommandMap extends CommonCommandMap {

    public ManageCommandMap(EconomyHandler economyHandler, PluginProperties properties, Server server, File dataPath, WarpManager warpManager, WPAManager wpaManager) {
        super();

        CommonHelpCommand helper = new CommonHelpCommand("xWarp");

        Collection<SubCommand> subCommands = new ArrayList<SubCommand>();
        subCommands.add(helper);
        subCommands.add(new ReloadCommand(economyHandler, properties, warpManager, wpaManager));
        subCommands.add(new StatusCommand(economyHandler, XWarp.permissions, warpManager, wpaManager));
        subCommands.add(new PermissionsCommand(server));
        subCommands.add(new ExportCommand(warpManager, wpaManager, dataPath, server));
        subCommands.add(new ImportCommand(warpManager, wpaManager, dataPath, server));
        subCommands.add(new CDWUConvCommand(warpManager)); // Only temporary

        this.populate(subCommands);
        this.setHelper(helper);
    }
}
