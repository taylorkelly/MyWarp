package de.xzise.xwarp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.commands.AddEditorCommand;
import de.xzise.xwarp.commands.ChangeCreatorCommand;
import de.xzise.xwarp.commands.CreateCommand;
import de.xzise.xwarp.commands.DeleteCommand;
import de.xzise.xwarp.commands.ExportCommand;
import de.xzise.xwarp.commands.GiveCommand;
import de.xzise.xwarp.commands.GlobalizeCommand;
import de.xzise.xwarp.commands.HelpCommand;
import de.xzise.xwarp.commands.ImportCommand;
import de.xzise.xwarp.commands.InfoCommand;
import de.xzise.xwarp.commands.InviteCommand;
import de.xzise.xwarp.commands.ListCommand;
import de.xzise.xwarp.commands.MessageCommand;
import de.xzise.xwarp.commands.PermissionsCommand;
import de.xzise.xwarp.commands.PriceCommand;
import de.xzise.xwarp.commands.PrivatizeCommand;
import de.xzise.xwarp.commands.PublicizeCommand;
import de.xzise.xwarp.commands.ReloadCommand;
import de.xzise.xwarp.commands.RemoveEditorCommand;
import de.xzise.xwarp.commands.RenameCommand;
import de.xzise.xwarp.commands.SearchCommand;
import de.xzise.xwarp.commands.SubCommand;
import de.xzise.xwarp.commands.UninviteCommand;
import de.xzise.xwarp.commands.UpdateCommand;
import de.xzise.xwarp.commands.WarpForceToCommand;
import de.xzise.xwarp.commands.WarpToCommand;
import de.xzise.xwarp.dataconnections.DataConnection;

public class CommandMap {

    private Map<String, SubCommand> commands;
    private HelpCommand helper;
    private WarpToCommand warper;

    public CommandMap(WarpManager list, EconomyWrapper economyWrapper, Server server, DataConnection data, File pluginPath, PluginProperties properties) {
        this.commands = new HashMap<String, SubCommand>();

        this.helper = new HelpCommand();
        this.warper = new WarpToCommand(list, server, properties);

        Collection<SubCommand> subCommands = new ArrayList<SubCommand>();

        subCommands.add(this.warper);
        subCommands.add(CreateCommand.newCreatePrivate(list, server));
        subCommands.add(CreateCommand.newCreatePublic(list, server));
        subCommands.add(CreateCommand.newCreateGlobal(list, server));
        subCommands.add(new DeleteCommand(list, server));
        subCommands.add(new UpdateCommand(list, server));
        subCommands.add(new RenameCommand(list, server));
        subCommands.add(this.helper);
        subCommands.add(new UninviteCommand(list, server));
        subCommands.add(new InviteCommand(list, server));
        subCommands.add(new GiveCommand(list, server));
        subCommands.add(new MessageCommand(list, server));
        subCommands.add(new AddEditorCommand(list, server));
        subCommands.add(new RemoveEditorCommand(list, server));
        subCommands.add(new PrivatizeCommand(list, server));
        subCommands.add(new PublicizeCommand(list, server));
        subCommands.add(new GlobalizeCommand(list, server));
        subCommands.add(new PriceCommand(list, server));
        subCommands.add(new SearchCommand(list, server));
        subCommands.add(new ListCommand(list, server));
        subCommands.add(new InfoCommand(list, server, economyWrapper));
        subCommands.add(new ReloadCommand(list, server));
        subCommands.add(new ChangeCreatorCommand(list, server));
        subCommands.add(new PermissionsCommand(list, server));
        subCommands.add(new ExportCommand(list, server, pluginPath));
        subCommands.add(new ImportCommand(list, pluginPath, data, server));
        subCommands.add(new WarpForceToCommand(list, server));

        for (SubCommand subCommand : subCommands) {
            this.registerCommand(subCommand);
        }

        this.helper.setCommands(subCommands, this.commands);
    }

    private void registerCommand(SubCommand command) {
        for (String text : command.getCommands()) {
            if (this.commands.put(text, command) != null) {
                throw new IllegalArgumentException("command was already registered!");
            }
        }
    }
    
    public SubCommand getCommand(String name) {
        if (MinecraftUtil.isSet(name)) {
            return this.commands.get(name);
        } else {
            return this.warper;
        }
    }

    public boolean executeCommand(CommandSender sender, String[] parameters) {
        if (parameters.length == 0) {
            return this.helper.execute(sender, parameters);
        } else {
            SubCommand command = this.commands.get(parameters[0]);
            if (command != null) {
                if (command.execute(sender, parameters)) {
                    return true;
                } else {
                    this.helper.showCommandHelp(sender, command);
                    return false;
                }
            } else {
                return this.warper.execute(sender, parameters);
            }
        }
    }
}