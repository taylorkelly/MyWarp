package de.xzise.xwarp.signwarps;

import me.taylorkelly.mywarp.WarpDestination;

public interface SignWarpDefinition {
	
	WarpDestination getDestination(String[] lines);

}
