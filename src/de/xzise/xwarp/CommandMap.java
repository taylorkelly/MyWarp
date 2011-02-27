package de.xzise.xwarp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.commands.AddEditorCommand;
import de.xzise.xwarp.commands.ConvertCommand;
import de.xzise.xwarp.commands.CreateCommand;
import de.xzise.xwarp.commands.DeleteCommand;
import de.xzise.xwarp.commands.GiveCommand;
import de.xzise.xwarp.commands.GlobalizeCommand;
import de.xzise.xwarp.commands.HelpCommand;
import de.xzise.xwarp.commands.InfoCommand;
import de.xzise.xwarp.commands.InviteCommand;
import de.xzise.xwarp.commands.ListCommand;
import de.xzise.xwarp.commands.PermissionsCommand;
import de.xzise.xwarp.commands.PrivatizeCommand;
import de.xzise.xwarp.commands.PublicizeCommand;
import de.xzise.xwarp.commands.ReloadCommand;
import de.xzise.xwarp.commands.RemoveEditorCommand;
import de.xzise.xwarp.commands.RenameCommand;
import de.xzise.xwarp.commands.SearchCommand;
import de.xzise.xwarp.commands.SubCommand;
import de.xzise.xwarp.commands.UninviteCommand;
import de.xzise.xwarp.commands.UpdateCommand;
import de.xzise.xwarp.commands.WarpToCommand;
import de.xzise.xwarp.dataconnections.DataConnection;

public class CommandMap {
	
	private Map<String, SubCommand> commands;
	private HelpCommand helper;
	private WarpToCommand warper;
	
	public CommandMap(WarpManager list, Server server, DataConnection data) {
		this.commands = new HashMap<String, SubCommand>();
		
		this.helper = new HelpCommand(list, server);
		this.warper = new WarpToCommand(list, server);
		
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
		subCommands.add(new AddEditorCommand(list, server));
		subCommands.add(new RemoveEditorCommand(list, server));
		subCommands.add(new PrivatizeCommand(list, server));
		subCommands.add(new PublicizeCommand(list, server));
		subCommands.add(new GlobalizeCommand(list, server));
		subCommands.add(new SearchCommand(list, server));
		subCommands.add(new ListCommand(list, server));
		subCommands.add(new InfoCommand(list, server));
		subCommands.add(new ReloadCommand(list, server));
		subCommands.add(new PermissionsCommand(server));
		subCommands.add(new ConvertCommand(list, server, data));
		
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