package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.util.List;

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

	boolean updateCreator(Warp warp);
	boolean updateMessage(Warp warp);
	boolean updateName(Warp warp);
	boolean updatePermissions(Warp warp);
	boolean updateVisibility(Warp warp);
	boolean updateLocation(Warp warp);

}
