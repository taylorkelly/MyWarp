package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Lister {
	private WarpList warpList;
	private Player player;
	
	private int maxPages;
	private int page;
	
	private static final int WARPS_PER_PAGE = 8;
	ArrayList<Warp> sortedWarps;
	
	public static final ChatColor PUBLIC_OWN = ChatColor.AQUA;
	public static final ChatColor PRIVATE_OWN = ChatColor.BLUE;
	public static final ChatColor PUBLIC_OTHER = ChatColor.GREEN;
	public static final ChatColor PRIVATE_OTHER = ChatColor.RED;
	public static final ChatColor PRIVATE_INVITED = ChatColor.YELLOW;

	public Lister(WarpList warpList) {
		this.warpList = warpList;
		this.maxPages = (int)Math.ceil(warpList.getSize()/(double)WARPS_PER_PAGE);
	}

	public void addPlayer(Player player) {
		this.player = player;
		this.maxPages = (int) Math.ceil(this.warpList.getSize(player) / (double) WARPS_PER_PAGE);
	}

	public void setPage(int page) {
		this.page = page;
		int start = (page-1)*WARPS_PER_PAGE;
		this.sortedWarps = warpList.getSortedWarps(player, start, WARPS_PER_PAGE);
	}

	private int getWidth(int number, int base) {
		int width = 1;
		while (number >= base) {
			number /= base;
			width++;
		}
		return width;
	}
	
	public void list() {
		// Generate header with the same length every time
		String intro = "";
		int width = 20 - this.getWidth(page, 10);
		while (width > 0) {
			intro += "-";
			width--;
		}
		intro += " Page " + page + "/" + maxPages + " ";
		width = 20 - this.getWidth(maxPages, 10);
		while (width > 0) {
			intro += "-";
			width--;
		}
		
		player.sendMessage(ChatColor.YELLOW + intro);
		for(Warp warp: sortedWarps) {
			String name = warp.name;
			String creator = (warp.creator.equalsIgnoreCase(player.getName()))?"you":warp.creator;
			int x = (int) Math.round(warp.x);
			int y = (int) Math.round(warp.y);
			int z = (int) Math.round(warp.z);
			ChatColor color;
			if(warp.playerIsCreator(player.getName())) {
				if (warp.publicAll) {
					color = PUBLIC_OWN;
				} else {
					color = PRIVATE_OWN;
				}
			} else if(warp.publicAll) {
				color = PUBLIC_OTHER;
			} else if (warp.playerCanWarp(player)){
				color = PRIVATE_INVITED;
			} else {
				color = PRIVATE_OTHER;
			}
			
		
			String location = " @(" + x + ", " + y + ", " + z + ")";
			String creatorString = " by " + creator;
			
			//Find remaining length left
			int left = MinecraftFontWidthCalculator.getStringWidth(intro) - MinecraftFontWidthCalculator.getStringWidth("''" + creatorString + location);
			
			int nameLength = MinecraftFontWidthCalculator.getStringWidth(name);
			if(left > nameLength) {
				name = "'" + name + "'" + ChatColor.WHITE + creatorString  + whitespace(left - nameLength);
			} else if (left < nameLength) {
				name = "'" + substring(name, left) + "'" + ChatColor.WHITE + creatorString;
			}

			player.sendMessage(color + name + location);
		}
	}

	/**
	 * Lob shit off that string till it fits.
	 */
	private String substring(String name, int left) {
		while(MinecraftFontWidthCalculator.getStringWidth(name) > left) {
			name = name.substring(0, name.length()-1);
		}
		return name;
	}

	public int getMaxPages() {
		return this.maxPages;
	}
	
	public String whitespace(int length) {
		int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");
		
		StringBuilder ret = new StringBuilder();
		
		for(int i = 0; i < length; i+=spaceWidth) {
			ret.append(" ");
		}
		
		return ret.toString();
	}
	
	public static String[] getLegend() {
		String[] result = new String[6];
		result[0] = ChatColor.RED + "-------------------- " + ChatColor.WHITE + "LIST LEGEND" + ChatColor.RED
		+ " -------------------";
		result[1] = PUBLIC_OWN + "Yours and it is public.";
		result[2] = PRIVATE_OWN + "Yours and it is private.";
		result[3] = PUBLIC_OTHER + "Not yours and it is public";
		result[4] = PRIVATE_OTHER + "Not yours, private and not invited";
		result[5] = PRIVATE_INVITED + "Not yours, private and you are invited";
		return result;
	}
}
