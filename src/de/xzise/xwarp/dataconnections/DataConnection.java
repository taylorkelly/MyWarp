package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.util.List;

import de.xzise.xwarp.Permissions;

import me.taylorkelly.mywarp.Warp;

public interface DataConnection {
	
	/**
	 * Loads the data from the given file.
	 * @param file initialize loading the data.
	 * @return TODO
	 */
	boolean load(File file);
	/**
	 * Loads the data from the default file.
	 * @param directory Defines the directory.
	 * @return TODO
	 * @see {@link #load(File)}.
	 */
	boolean loadDefault(File directory);
	void free();
	
	List<Warp> getWarps();
	
	void addWarp(Warp... warp);
	void deleteWarp(Warp warp);

	void updateCreator(Warp warp);
	void updateMessage(Warp warp);
	void updateName(Warp warp);
	void updateVisibility(Warp warp);
	void updateLocation(Warp warp);	
	void updateEditor(Warp warp, String name);

	boolean isUpdateAvailable(Permissions permission);
	
}
