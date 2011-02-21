package de.xzise.xwarp.dataconnections;

import java.util.List;

import me.taylorkelly.mywarp.Warp;

public interface DataConnection {
	
	void free();
	
	List<Warp> getWarps();
	
	void addWarp(Warp... warp);
	void deleteWarp(Warp warp);

	void updateCreator(Warp warp);
	void updateMessage(Warp warp);
	void updateName(Warp warp);
	void updatePermissions(Warp warp);
	void updateVisibility(Warp warp);
	void updateLocation(Warp warp);

}
