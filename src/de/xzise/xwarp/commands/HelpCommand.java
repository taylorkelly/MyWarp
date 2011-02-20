package de.xzise.xwarp.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;

public class HelpCommand extends SubCommand {
	
	private Collection<SubCommand> commands;
	private Map<String, SubCommand> commandMap;

	public HelpCommand(WarpList list, Server server) {
		super(list, server, "help", "?");
		this.commands = new ArrayList<SubCommand>();
		this.commandMap = new HashMap<String, SubCommand>();
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length > 2) {
			return false;
		}
		
		// First get all commands:
		List<String> lines = new ArrayList<String>(this.commands.size());
		for (SubCommand command : this.commands) {
			if (command.listHelp(sender)) {
				lines.add(command.getSmallHelp());
			}
		}
		
		int page = 1;
		int maxPage = lines.size() / (MinecraftUtil.MAX_LINES_VISIBLE - 1);
		if (parameters.length == 2) {
			if (WMPlayerListener.isInteger(parameters[1])) {
				page = Integer.parseInt(parameters[1]);
				if (page < 1) {
					sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
					return true;
				} else if (page > maxPage) {
					sender.sendMessage(ChatColor.RED + "There are only 2 pages of help");
					return true;
				}
			} else {
				SubCommand command = this.commandMap.get(parameters[1]);
				if (command != null) {
					for (String line : command.getFullHelp()) {
						sender.sendMessage(line);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Please input a valid number/command");
				}
				return true;
			}
		}
		sender.sendMessage(ChatColor.WHITE + "------------------ " + ChatColor.GREEN + "xWarp Help " + page + "/" + maxPage + ChatColor.WHITE + " ------------------");
		for (int i = (page - 1) * (MinecraftUtil.MAX_LINES_VISIBLE - 1); i < lines.size() && i < page * (MinecraftUtil.MAX_LINES_VISIBLE - 1); i++) {
			sender.sendMessage(lines.get(i));
		}
		return true;
	}
	
	public void setCommands(Collection<SubCommand> commands, Map<String, SubCommand> map) {
		this.commands.addAll(commands);
		this.commandMap.putAll(map);
	}
	
	public void showCommandHelp(CommandSender sender, SubCommand command) {
		sender.sendMessage("xWarp Help");
		for (String line : command.getFullHelp()) {
			sender.sendMessage(line);
		}
	}
	
	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Shows the selected help page." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Shows the help";
	}

	@Override
	protected String getCommand() {
		return "warp help [#page]";
	}
}
