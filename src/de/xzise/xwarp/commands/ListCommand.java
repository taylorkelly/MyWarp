package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.WarpList;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.lister.ListDataReciever;
import de.xzise.xwarp.lister.ListSection;

public class ListCommand extends SubCommand {

	private class WarpListReciever implements ListDataReciever {

		private final WarpList list;
		private CommandSender sender;
		private String creator;
		
		public WarpListReciever(WarpList list) {
			this.list = list;
		}
		
		public void setSender(CommandSender sender, String creator) {
			this.sender = sender;
			this.creator = creator;
		}
		
		@Override
		public ListSection[] getListSections(int start, int length) {
			ListSection section = new ListSection(null);
			
			section.addWarps(this.list.getSortedWarps(sender, creator, start, length));
			
			return new ListSection[] { section };
		}

		@Override
		public int getSize() {
			return this.list.getSize(sender, creator);
		}
		
	}
	
	private GenericLister lister;
	private WarpListReciever reciever;
	
	public ListCommand(WarpList list, Server server) {
		super(list, server, "list", "ls");
		
		this.reciever = new WarpListReciever(list);
		this.lister = new GenericLister(this.reciever);
	}

	@Override
	protected boolean internalExecute(CommandSender sender, String[] parameters) {
		if (parameters.length == 3 && !WMPlayerListener.isInteger(parameters[2])) {
			return false;
		}
		
		if (parameters.length == 2 && parameters[1].equalsIgnoreCase("legend")) {
			if (sender instanceof ConsoleCommandSender) {
				sender.sendMessage("No colors in console, so this command is useless here!");
			}
			for (String line : GenericLister.getLegend()) {
				sender.sendMessage(line);
			}
		} else {
			this.lister.setSender(sender);			
			String creator = null;
			int page;
			
			if (parameters.length == 3 || (parameters.length == 2 && !WMPlayerListener.isInteger(parameters[1]))) {
				creator = this.getPlayer(parameters[1]);
				if (parameters.length == 3) {
					page = Integer.parseInt(parameters[2]);
					if (page < 1) {
						sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
						return true;
					} else if (page > lister.getMaxPages()) {
						sender.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages()
										+ " pages of warps");
						return true;
					}
				} else {				
					page = 1;
				}
			} else if (parameters.length == 2) {
				page = Integer.parseInt(parameters[1]);
				if (page < 1) {
					sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
					return true;
				} else if (page > lister.getMaxPages()) {
					sender.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages()
									+ " pages of warps");
					return true;
				}
			} else {
				page = 1;
			}
			this.reciever.setSender(sender, creator);
			this.lister.listPage(page);
		}
		return true;
	}
}
