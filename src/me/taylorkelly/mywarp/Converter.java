package me.taylorkelly.mywarp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class Converter {

	public static List<Warp> convert(CommandSender sender, Server server, String owner) {
		List<Warp> result = new ArrayList<Warp>();
		File file = new File("warps.txt");
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			int size = 0;
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.equals(""))
					continue;
				String[] pieces = line.split(":");
				String name = pieces[0];
				double x = Double.parseDouble(pieces[1]);
				double y = Double.parseDouble(pieces[2]);
				double z = Double.parseDouble(pieces[3]);
				double yaw = Double.parseDouble(pieces[4]);
				double pitch = Double.parseDouble(pieces[5]);
	
				yaw = (yaw < 0) ? (360 + (yaw % 360)) : (yaw % 360);
	
				World world = server.getWorlds().get(0);
				Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);
				result.add(new Warp(name, owner, location));
			}
			file.delete();
			sender.sendMessage("Successfully loaded " + ChatColor.GREEN + size + ChatColor.WHITE + " warps.");
		} catch (FileNotFoundException e) {
			sender.sendMessage(ChatColor.RED + "Problem loading file.");
			MyWarp.logger.severe("File not found", e);
		}
		return result;
	}

}
