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
import de.xzise.xwarp.commands.wpa.CreateCommand;
import de.xzise.xwarp.commands.wpa.InfoCommand;
import de.xzise.xwarp.commands.wpa.StopCreateCommand;
import de.xzise.xwarp.commands.xwarp.ReloadCommand;
import de.xzise.xwarp.dataconnections.DataConnection;

public class WPACommandMap extends CommonCommandMap {

    private static final String LABEL = "wpa";
    public final CreateCommand createCommand;

    public WPACommandMap(WPAManager manager, EconomyHandler economyWrapper, Server server, DataConnection data, File pluginPath, PluginProperties properties) {
        super();

        CommonHelpCommand helper = new CommonHelpCommand("xWarp");
        this.createCommand = new CreateCommand(manager, server);

        Collection<SubCommand> subCommands = new ArrayList<SubCommand>();

        subCommands.add(this.createCommand);
        subCommands.add(new StopCreateCommand(this.createCommand));
        subCommands.add(DeleteCommand.create(manager, server, LABEL));
        // subCommands.add(new UpdateCommand(manager, server));
        subCommands.add(RenameCommand.create(manager, server, LABEL));
        subCommands.add(helper);
        subCommands.add(UninviteCommand.create(manager, server, LABEL));
        subCommands.add(InviteCommand.create(manager, server, LABEL));
        subCommands.add(GiveCommand.create(manager, server, LABEL));
        subCommands.add(AddEditorCommand.create(manager, server, LABEL));
        subCommands.add(RemoveEditorCommand.create(manager, server, LABEL));
        // subCommands.add(new SearchCommand(manager, server));
        // subCommands.add(new ListCommand(manager, server));
        subCommands.add(new ChangeWorldCommand(manager, server, LABEL));
        subCommands.add(ChangeCreatorCommand.create(manager, server, LABEL));
        subCommands.add(new InfoCommand(manager, server));
        // subCommands.add(new ListedCommand(manager, server));
        subCommands.add(new ReloadCommand(economyWrapper, properties, manager));

        this.populate(subCommands);

        this.setHelper(helper);
    }
}