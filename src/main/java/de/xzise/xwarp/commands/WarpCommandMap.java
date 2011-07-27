package de.xzise.xwarp.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.Server;

import de.xzise.commands.CommonCommandMap;
import de.xzise.commands.CommonHelpCommand;
import de.xzise.commands.SubCommand;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.commands.warp.GlobalizeCommand;
import de.xzise.xwarp.commands.warp.InfoCommand;
import de.xzise.xwarp.commands.warp.ListCommand;
import de.xzise.xwarp.commands.warp.ListedCommand;
import de.xzise.xwarp.commands.warp.MessageCommand;
import de.xzise.xwarp.commands.warp.PriceCommand;
import de.xzise.xwarp.commands.warp.PrivatizeCommand;
import de.xzise.xwarp.commands.warp.PublicizeCommand;
import de.xzise.xwarp.commands.warp.UpdateCommand;
import de.xzise.xwarp.dataconnections.DataConnection;

public class WarpCommandMap extends CommonCommandMap {

    private static final String LABEL = "warp";
    
    public WarpCommandMap(WarpManager list, EconomyHandler economyWrapper, Server server, DataConnection data, File pluginPath, PluginProperties properties) {
        super();

        CommonHelpCommand helper = new CommonHelpCommand("xWarp");
        WarpToCommand warper = new WarpToCommand(list, server);

        Collection<SubCommand> subCommands = new ArrayList<SubCommand>();

        subCommands.add(warper);
        subCommands.add(CreateCommand.newCreatePrivate(list, server));
        subCommands.add(CreateCommand.newCreatePublic(list, server));
        subCommands.add(CreateCommand.newCreateGlobal(list, server));
        subCommands.add(new DeleteCommand<Warp, WarpManager>(list, server, LABEL));
        subCommands.add(new UpdateCommand(list, server));
        subCommands.add(new RenameCommand<Warp, WarpManager>(list, server, LABEL));
        subCommands.add(helper);
        subCommands.add(new UninviteCommand<Warp, WarpManager>(list, server, LABEL));
        subCommands.add(new InviteCommand<Warp, WarpManager>(list, server, LABEL));
        subCommands.add(new GiveCommand<Warp, WarpManager>(list, server, LABEL));
        subCommands.add(new MessageCommand(list, server));
        subCommands.add(new AddEditorCommand<Warp, WarpManager>(list, server, LABEL));
        subCommands.add(new RemoveEditorCommand<Warp, WarpManager>(list, server, LABEL));
        subCommands.add(new PrivatizeCommand(list, server));
        subCommands.add(new PublicizeCommand(list, server));
        subCommands.add(new GlobalizeCommand(list, server));
        subCommands.add(new PriceCommand(list, server));
        subCommands.add(new SearchCommand(list, server));
        subCommands.add(new ListCommand(list, server));
        subCommands.add(new ChangeCreatorCommand<Warp, WarpManager>(list, server, LABEL));
        subCommands.add(new InfoCommand(list, server, economyWrapper));
        subCommands.add(new ListedCommand(list, server));
        subCommands.add(new ReloadCommand(list));
        subCommands.add(new StatusCommand(list, economyWrapper, MyWarp.permissions));
        subCommands.add(new PermissionsCommand(list, server));
        subCommands.add(new ExportCommand(list, server, pluginPath));
        subCommands.add(new ImportCommand(list, pluginPath, data, server));
        subCommands.add(new WarpForceToCommand(list, server));
        
        this.populate(subCommands);
        
        this.setHelper(helper);
    }
}