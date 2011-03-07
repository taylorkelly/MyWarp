package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.util.List;

import me.taylorkelly.mywarp.Warp;

public interface DataConnection {

	void load(File file);
	void create(File file);
	void free();
	
	String getFilename();
	void clear();
	
	List<Warp> getWarps();
	
	void addWarp(Warp... warp);
	void deleteWarp(Warp warp);

	void updateCreator(Warp warp);
	void updateMessage(Warp warp);
	void updateName(Warp warp);
	void updateVisibility(Warp warp);
	void updateLocation(Warp warp);
	
	void updateEditor(Warp warp, String name);
	
}
