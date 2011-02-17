package de.xzise.xwarp;

import java.util.HashMap;
import java.util.Map;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

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
import de.xzise.xwarp.commands.RenameCommand;
import de.xzise.xwarp.commands.SearchCommand;
import de.xzise.xwarp.commands.SubCommand;
import de.xzise.xwarp.commands.UninviteCommand;
import de.xzise.xwarp.commands.UpdateCommand;
import de.xzise.xwarp.commands.WarpToCommand;

public class CommandMap {
	
	private Map<String, SubCommand> commands;
	private HelpCommand helper;
	private WarpToCommand warper;
	
	public CommandMap(WarpList list, Server server) {
		this.commands = new HashMap<String, SubCommand>();
		
		this.helper = new HelpCommand(list, server);
		this.warper = new WarpToCommand(list, server);
		
		this.registerCommand(this.helper);
		this.registerCommand(this.warper);
		
		this.registerCommand(new ConvertCommand(list, server));
		this.registerCommand(new DeleteCommand(list, server));
		this.registerCommand(new UpdateCommand(list, server));
		this.registerCommand(new RenameCommand(list, server));
		this.registerCommand(new PrivatizeCommand(list, server));
		this.registerCommand(new PublicizeCommand(list, server));
		this.registerCommand(new GlobalizeCommand(list, server));
		this.registerCommand(new SearchCommand(list, server));
		this.registerCommand(CreateCommand.newCreatePrivate(list, server));
		this.registerCommand(CreateCommand.newCreatePublic(list, server));
		this.registerCommand(CreateCommand.newCreateGlobal(list, server));
		this.registerCommand(new ListCommand(list, server));
		this.registerCommand(new UninviteCommand(list, server));
		this.registerCommand(new InviteCommand(list, server));
		this.registerCommand(new ReloadCommand(list, server));
		this.registerCommand(new GiveCommand(list, server));
		this.registerCommand(new PermissionsCommand(list, server));
		this.registerCommand(new InfoCommand(list, server));
		
		this.helper.setCommands(this.commands.values());
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
					//TODO: Add specific helper here?
					return false;
				}
			} else {
				return this.warper.execute(sender, parameters);
			}
		}
	}

}
