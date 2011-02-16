package de.xzise.xwarp.lister;

import java.util.ArrayList;
import java.util.List;

import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.Warp;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GenericLister {
	
	private interface WidthCalculator {
		int getWidth(String text);
	}
	
	private class MinecraftWidth implements WidthCalculator {

		@Override
		public int getWidth(String text) {
			return MinecraftFontWidthCalculator.getStringWidth(text);
		}
		
	}
	
	private class ConsoleWidth implements WidthCalculator {

		@Override
		public int getWidth(String text) {
			// Assume that the font is non proportional!
			
			//TODO: Remove color codes!
			return text.length();
		}
		
	}
	
	public static final ChatColor GLOBAL_OWN = ChatColor.DARK_BLUE;
	public static final ChatColor PUBLIC_OWN = ChatColor.BLUE;
	public static final ChatColor PRIVATE_OWN = ChatColor.AQUA;
	
	public static final ChatColor GLOBAL_OTHER = ChatColor.DARK_GREEN;
	public static final ChatColor PUBLIC_OTHER = ChatColor.GREEN;
	public static final ChatColor PRIVATE_OTHER = ChatColor.RED;
	
	public static final ChatColor PRIVATE_INVITED = ChatColor.YELLOW;
	
	private ListDataReciever dataReciever;
	private CommandSender sender;
	
	private int maxPages;
	private String introRight;
	private String introLeft;
	
	private ListSection[] listSections;
	
	public GenericLister(ListDataReciever dataReciever) {
		this.dataReciever = dataReciever;
		this.maxPages = -1;
	}
	
	public void setSender(CommandSender sender) {
		if (sender != this.sender) {
			this.sender = sender;
			this.maxPages = -1;
		}
	}
	
	private void calculateMaxPages() {
		int size = this.dataReciever.getSize();
		this.maxPages = (int) Math.ceil(size / (double) (WMPlayerListener.LINES_PER_PAGE - 1));
		this.introRight = "/" + maxPages + " ";
		int width = 20 - GenericLister.getWidth(maxPages, 10);
		while (width > 0) {
			this.introRight += "-";
			width--;
		}
	}

	public int getMaxPages() {
		if (this.maxPages < 0) {
			this.calculateMaxPages();
		}
		return this.maxPages;
	}
	
	public void setPage(int page) {
		this.listSections = this.dataReciever.getListSections((page-1) * (WMPlayerListener.LINES_PER_PAGE - 1), WMPlayerListener.LINES_PER_PAGE);
		
		// Generate header with the same length every time
		this.introLeft = "";
		int width = 20 - GenericLister.getWidth(page, 10);
		while (width > 0) {
			this.introLeft += "-";
			width--;
		}
		this.introLeft += " Page " + page;
	}
	
	public void listPage(int page) {
		this.setPage(page);
		this.listPage();
	}
	
	public void listPage() {
		if (this.maxPages < 0) {
			this.calculateMaxPages();
		}		
		
		this.sender.sendMessage(ChatColor.YELLOW + this.introLeft + this.introRight);
		
		WidthCalculator widther = null;
		
		// Get the correct width calculator!
		if (sender instanceof ConsoleCommandSender) {
			widther = new ConsoleWidth();
		} else if (sender instanceof Player) {
			widther = new MinecraftWidth();
		}
		
		int width = widther.getWidth(this.introLeft + this.introRight); 
		
		for (ListSection listSection : this.listSections) {
			if (listSection.title != null && !listSection.title.isEmpty()) {
				this.sender.sendMessage(ChatColor.GREEN + listSection.title);
			}
			
			for (Warp warp : listSection) {
				String name = warp.name;
				
				String creator = warp.creator;
				ChatColor color = ChatColor.WHITE;
				if (this.sender instanceof Player) {
					if (warp.creator.equalsIgnoreCase(((Player) this.sender).getName())) {
						creator = "you";
					}
					color = GenericLister.getColor(warp, (Player) this.sender);
				}
			
				String location = GenericLister.getLocationString(warp);
				String creatorString = " by " + creator;
				
				//Find remaining length left
				int left = width - widther.getWidth("''" + creatorString + location);
				
				int nameLength = widther.getWidth(name);
				if(left > nameLength) {
					name = "'" + name + "'" + ChatColor.WHITE + creatorString  + whitespace(left - nameLength, widther.getWidth(" "));
				} else if (left < nameLength) {
					name = "'" + substring(name, left, widther) + "'" + ChatColor.WHITE + creatorString;
				}

				this.sender.sendMessage(color + name + location);		
			}
		}
	}
	
	/**
	 * Lob shit off that string till it fits.
	 */
	private static String substring(String name, int left, WidthCalculator widthCalculator) {
		while(widthCalculator.getWidth(name) > left && !name.isEmpty()) {
			name = name.substring(0, name.length()-1);
		}
		return name;
	}
	
	public static String whitespace(int length, int spaceWidth) {		
		StringBuilder ret = new StringBuilder();
		
		for(int i = 0; i < length; i+=spaceWidth) {
			ret.append(" ");
		}
		
		return ret.toString();
	}
	
	private static int getWidth(int number, int base) {
		int width = 1;
		while (number >= base) {
			number /= base;
			width++;
		}
		return width;
	}
	
	public static String[] getLegend() {
		List<String> result = new ArrayList<String>(8);
		result.add(ChatColor.RED + "-------------------- " + ChatColor.WHITE + "LIST LEGEND" + ChatColor.RED
		+ " -------------------");
		result.add(GenericLister.GLOBAL_OWN + "Yours and it is global");
		result.add(GenericLister.PUBLIC_OWN + "Yours and it is public.");
		result.add(GenericLister.PRIVATE_OWN + "Yours and it is private.");
		result.add(GenericLister.GLOBAL_OTHER + "Not yours and it is global");
		result.add(GenericLister.PUBLIC_OTHER + "Not yours and it is public");
		result.add(GenericLister.PRIVATE_OTHER + "Not yours, private and not invited");
		result.add(GenericLister.PRIVATE_INVITED + "Not yours, private and you are invited");
		return result.toArray(new String[0]);
	}
	
	public static ChatColor getColor(Warp warp, Player player) {
		if(warp.playerIsCreator(player.getName())) {
			switch (warp.visibility) {
			case PRIVATE :
				return GenericLister.PRIVATE_OWN;
			case PUBLIC :
				return GenericLister.PUBLIC_OWN;
			case GLOBAL :
				return GenericLister.GLOBAL_OWN;
			}
		} else {
			switch (warp.visibility) {
			case PRIVATE :
				if (warp.playerCanWarp(player)) {
					return GenericLister.PRIVATE_INVITED;
				} else {
					return GenericLister.PRIVATE_OTHER;
				}
			case PUBLIC :
				return GenericLister.PUBLIC_OTHER;
			case GLOBAL :
				return GenericLister.GLOBAL_OTHER;
			}
		}
		return GenericLister.PRIVATE_OTHER;
	}
	
	public static String getLocationString(Warp warp) {
		return getLocationString(warp.getLocation());
	}
	
	public static String getLocationString(Location location) {
		return " @(" + (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ() + ")";
	}

}
