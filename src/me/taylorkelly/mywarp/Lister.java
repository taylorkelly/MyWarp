package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class Lister {
	private WarpList warpList;
	private Player player;
	
	private int maxPages;
	private int page;
	
	private static final int WARPS_PER_PAGE = 8;
	ArrayList<Warp> sortedWarps;

	public Lister(WarpList warpList) {
		this.warpList = warpList;
	}

	public void addPlayer(Player player) {
		this.player = player;
	}

	public void setPage(int page) {
		this.page = page;
		int start = (page-1)*WARPS_PER_PAGE;
		sortedWarps = warpList.getSortedWarps(player, start, WARPS_PER_PAGE);
	    maxPages = (int)Math.ceil(warpList.getMaxWarps(player)/(double)WARPS_PER_PAGE);
	}

	public void list() {
		String intro = "------------------- Page " + page + "/" + maxPages + " -------------------";
		player.sendMessage(ChatColor.YELLOW + intro);
		for(Warp warp: sortedWarps) {
			String name = warp.name;
			String creator = (warp.creator.equalsIgnoreCase(player.getName()))?"you":warp.creator;
			int x = (int) Math.round(warp.x);
			int y = warp.y;
			int z = (int) Math.round(warp.z);
			String color;
			if(warp.playerIsCreator(player.getName())) {
				color = ChatColor.AQUA.toString();
			} else if(warp.publicAll) {
				color = ChatColor.GREEN.toString();
			} else {
				color = ChatColor.RED.toString();
			}
			
		
			String location = " @(" + x + ", " + y + ", " + z + ")";
			String creatorString = (warp.publicAll?"(+)":"(-)") + " by " + creator;
			
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

	public int getMaxPages(Player player) {
        return (int)Math.ceil(warpList.getMaxWarps(player)/(double)WARPS_PER_PAGE);
	}
	
	public String whitespace(int length) {
		int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");
		
		StringBuilder ret = new StringBuilder();
		
		for(int i = 0; i < length; i+=spaceWidth) {
			ret.append(" ");
		}
		
		return ret.toString();
	}
}
