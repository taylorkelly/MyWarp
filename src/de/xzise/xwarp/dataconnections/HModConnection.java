package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.Warp;

public class HModConnection implements DataConnection {

	private File file;
	private Server server;
	private static final char SEPARATOR = ':';
	
	public HModConnection(Server server) {
		this.server = server;
	}

	@Override
	public boolean load(File file) {
		this.file = file;
		return this.file.canWrite();
	}

	@Override
	public boolean loadDefault(File directory) {
		return this.load(new File(directory, "hmod.txt"));
	}
	
	@Override
	public void free() {}

	@Override
	public List<Warp> getWarps() {
		return this.getWarps(null);
	}
	
	public List<Warp> getWarps(String owner) {
		List<Warp> result = new ArrayList<Warp>();
		Scanner scanner;
		try {
			scanner = new Scanner(this.file);
			int size = 0;
			int invalidSize = 0;
			World defaultWorld = this.server.getWorlds().get(0);
			while (scanner.hasNext()) {
				String[] pieces = WMPlayerListener.parseLine(scanner.nextLine(), ':');
				if ((pieces.length == 8) || (pieces.length == 6 && owner != null && !owner.isEmpty())) {
					String name = pieces[0];
					
					double x = Double.parseDouble(pieces[1]);
					double y = Double.parseDouble(pieces[2]);
					double z = Double.parseDouble(pieces[3]);
					double yaw = Double.parseDouble(pieces[4]);
					double pitch = Double.parseDouble(pieces[5]);
		
					yaw = (yaw < 0) ? (360 + (yaw % 360)) : (yaw % 360);
		
					World world = defaultWorld;
					String warpOwner = owner;
					// hmod gen 2
					if (pieces.length == 8) {
						world = this.server.getWorld(pieces[6]);
						warpOwner = pieces[7];
					}
					Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);
					Warp warp = new Warp(name, warpOwner, location);
					result.add(warp);
					size++;
					if (!warp.isValid()) {
						invalidSize++;
					}
				} else {
					MyWarp.logger.warning("Invalid line found");
				}
			}
			MyWarp.logger.info(size + " warps loaded");
			if (invalidSize > 0) {
				MyWarp.logger.warning(invalidSize + " invalid warps found.");
			}
		} catch (FileNotFoundException e) {
			MyWarp.logger.info("hmod file not found!");
		}
		return result;
	}

	private void writeWarps(List<Warp> warps) {
		try {
			FileWriter writer = new FileWriter(this.file);
			for (Warp warp : warps) {
				writeWarp(warp, writer);
			}
		} catch (IOException e) {
			MyWarp.logger.severe("Unable to write the file", e);
		}		
	}
	
	private static void writeWarp(Warp warp, Writer writer) throws IOException {
		StringBuilder warpLine = new StringBuilder();
		Location l = warp.getLocation();
		warpLine.append(makeParsable(warp.name) + SEPARATOR);
		warpLine.append(makeParsable(l.getX()) + SEPARATOR);
		warpLine.append(makeParsable(l.getY()) + SEPARATOR);
		warpLine.append(makeParsable(l.getZ()) + SEPARATOR);
		warpLine.append(makeParsable(l.getYaw()) + SEPARATOR);
		warpLine.append(makeParsable(l.getPitch()) + SEPARATOR);
		warpLine.append(makeParsable(l.getWorld().getName()) + SEPARATOR);
		warpLine.append(makeParsable(warp.creator) + SEPARATOR);
		writer.append(warpLine);
	}
	
	private static String makeParsable(double input) {
		return Double.toString(input);
	}
	
	private static String makeParsable(String input) {
		// Length output
		int length = 0;
		// Maximum length = twice input length (to escape at least each character)
		char[] output = new char[input.length() * 2];
		char[] in = input.toCharArray();
		for (int i = 0; i < in.length; i++) {
			char c = in[i];
			switch (c) {
			case ':' :
			case '\\' :
			case '"' :
				output[length++] = '\\';
			}
			output[length++] = c;
		}
		return new String(Arrays.copyOf(output, length));
	}
	
	@Override
	public void addWarp(Warp... warps) {
		if (warps.length > 0) {
			try {
				FileWriter writer = new FileWriter(this.file, true);
				for (Warp warp : warps) {
					writeWarp(warp, writer);
				}
			} catch (IOException e) {
				MyWarp.logger.severe("Unable to write the file", e);
			}
		}		
	}

	@Override
	public void deleteWarp(Warp warp) {
		// First read all, then delete the selected and then write all
		List<Warp> warps = this.getWarps();
		warps.remove(warp);
		this.writeWarps(warps);
	}

	@Override
	public boolean updateCreator(Warp warp) {
		List<Warp> warps = this.getWarps();
		Warp updated = warps.get(warps.indexOf(warp));
		updated.creator = warp.creator;
		this.writeWarps(warps);
		return true;
	}

	@Override
	public boolean updateName(Warp warp) {
		List<Warp> warps = this.getWarps();
		Warp updated = warps.get(warps.indexOf(warp));
		updated.name = warp.name;
		this.writeWarps(warps);
		return true;
	}

	@Override
	public boolean updateLocation(Warp warp) {
		List<Warp> warps = this.getWarps();
		Warp updated = warps.get(warps.indexOf(warp));
		updated.setLocation(warp.getLocation());
		this.writeWarps(warps);
		return true;
	}

	@Override
	// Not supported in hmod
	public boolean updateMessage(Warp warp) {
		return false;
	}

	@Override
	// Not supported in hmod
	public boolean updatePermissions(Warp warp) {
		return false;
	}

	@Override
	// Not supported in hmod
	public boolean updateVisibility(Warp warp) {
		return false;
	}
}
