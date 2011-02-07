package me.taylorkelly.mywarp;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Converter {

	public static void convert(Player player, Server server, WarpList lister) {
		File file = new File("warps.txt");
		PreparedStatement ps = null;
		try {
			Connection conn = ConnectionManager.getConnection();
			ps = conn
					.prepareStatement("INSERT INTO warpTable (id, name, creator, world, x, y, z, yaw, pitch, publicLevel, permissions, welcomeMessage) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

			Scanner scanner = new Scanner(file);
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

				World world = server.getWorlds()[0];
				Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);
				Warp warp = new Warp(name, player.getName(), location);
				lister.blindAdd(warp);

				ps.setInt(1, warp.index);
				ps.setString(2, warp.name);
				ps.setString(3, warp.creator);
				ps.setInt(4, warp.world);
				ps.setDouble(5, warp.x);
				ps.setDouble(6, warp.y);
				ps.setDouble(7, warp.z);
				ps.setInt(8, warp.yaw);
				ps.setInt(9, warp.pitch);
				ps.setInt(10, warp.visibility.level);
				ps.setString(11, warp.permissionsString());
				ps.setString(12, warp.welcomeMessage);
				ps.addBatch();
				size++;
			}
			ps.executeBatch();
			conn.commit();
			file.delete();
			player.sendMessage("Successfully imported " + size + " warps.");
		} catch (FileNotFoundException e) {
			player.sendMessage(ChatColor.RED + "Error: 'warps.txt' doesn't exist.");
		} catch (SQLException e) {
			player.sendMessage(ChatColor.RED + "Error: SQLite Exception");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex) {
				player.sendMessage(ChatColor.RED + "Error: SQLite Exception (on close)");
			}
		}
	}

}
