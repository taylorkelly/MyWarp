package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.taylorkelly.mywarp.Warp;

/**
 * This connection does nothing.
 * 
 * @author Fabian Neundorf
 */
public class NullConnection implements DataConnection {

	@Override
	public void free() {
		
	}

	@Override
	public List<Warp> getWarps() {
		return new ArrayList<Warp>(0);
	}

	@Override
	public void addWarp(Warp... warp) {
		
	}

	@Override
	public void deleteWarp(Warp warp) {
		
	}

	@Override
	public void updateCreator(Warp warp) {
		
	}

	@Override
	public void updateMessage(Warp warp) {
		
	}

	@Override
	public void updateName(Warp warp) {

	}

	@Override
	public void updateVisibility(Warp warp) {

	}

	@Override
	public void updateLocation(Warp warp) {

	}

	@Override
	public void updateEditor(Warp warp, String name) {
		
	}

	@Override
	public void load(File file) {
		
	}

	@Override
	public String getFilename() {
		return "null";
	}

	@Override
	public void clear() {
		
	}

	@Override
	public void create(File file) {
		
	}

}
