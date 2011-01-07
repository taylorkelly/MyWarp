package me.taylorkelly.mywarp;

import org.bukkit.Color;
import org.bukkit.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;

public class WMPlayerListener extends PlayerListener{
	private boolean warning;
	private Plugin plugin;
	private WarpList warpList;
	
	public WMPlayerListener(Plugin plugin, WarpList warpList) {
		warning = false;
		this.plugin = plugin;
		this.warpList = warpList;
	}
	
	public void onPlayerCommand(PlayerChatEvent event) {
		String[] split = event.getMessage().split(" ");
		Player player = event.getPlayer();

		// TODO permissions
		if (split[0].equalsIgnoreCase("/warp")) {
			event.setCancelled(true);
			/**
			 * /warp convert
			 */
			if (split.length == 2 && split[1].equalsIgnoreCase("convert")) {
				if(!warning) {
					player.sendMessage(Color.RED + "Warning: " + Color.WHITE + "Only use a copy of warps.txt.");
					player.sendMessage("This will delete the warps.txt it uses");
					player.sendMessage("Use " + Color.RED + "'/warp convert'" + Color.WHITE + " again to confirm.");
					warning = true;
				} else {
					Converter.convert(player, plugin.getServer(), warpList);
					warning = false;
				}
			/**
			 * /warp create <name>
			 */
			} else if (split.length > 2 && split[1].equalsIgnoreCase("create")) {
				String name = "";
				for(int i = 2; i < split.length; i++) {
					name += split[i];
					if(i+1 < split.length) name += " ";
				}
				
				warpList.addWarp(name, player);
			/**
			 * /warp delete <name>
			 */
			} else if (split.length > 2 && split[1].equalsIgnoreCase("delete")) {
				String name = "";
				for(int i = 2; i < split.length; i++) {
					name += split[i];
					if(i+1 < split.length) name += " ";
				}
				
				warpList.deleteWarp(name, player);
			/**
			 * /warp private <name>
			 */
			} else if (split.length > 2 && split[1].equalsIgnoreCase("private")) {
				String name = "";
				for(int i = 2; i < split.length; i++) {
					name += split[i];
					if(i+1 < split.length) name += " ";
				}
				
				warpList.privatize(name, player);
			/**
			 * /warp public <name>
			 */
			} else if (split.length > 2 && split[1].equalsIgnoreCase("public")) {
				String name = "";
				for(int i = 2; i < split.length; i++) {
					name += split[i];
					if(i+1 < split.length) name += " ";
				}
				
				warpList.publicize(name, player);
			/**
			 * /warp invite <player> <name>
			 */
			} else if (split.length > 3 && split[1].equalsIgnoreCase("invite")) {
				Player invitee = plugin.getServer().getPlayer(split[2]);
				// TODO Change to matchPlayer
				String inviteeName = (invitee == null) ? split[2] : invitee.getName();

				String name = "";
				for(int i = 3; i < split.length; i++) {
					name += split[i];
					if(i+1 < split.length) name += " ";
				}
				
				warpList.invite(name, player, inviteeName);
			/**
			 * /warp uninvite <player> <name>
			 */
			} else if (split.length > 3 && split[1].equalsIgnoreCase("uninvite")) {
				Player invitee = plugin.getServer().getPlayer(split[2]);
				// TODO Change to matchPlayer
				String inviteeName = (invitee == null) ? split[2] : invitee.getName();

				String name = "";
				for(int i = 3; i < split.length; i++) {
					name += split[i];
					if(i+1 < split.length) name += " ";
				}
				
				warpList.uninvite(name, player, inviteeName);
			/**
			 * /warp <name>
			 */
			} else if (split.length > 1) {
				// TODO ChunkLoading
				String name = "";
				for(int i = 1; i < split.length; i++) {
					name += split[i];
					if(i+1 < split.length) name += " ";
				}
				
				warpList.warpTo(name, player);
			} else {
				//TODO help?
				player.sendMessage(Color.RED + "Invalid /warp command");
			}
		}
	}
}
