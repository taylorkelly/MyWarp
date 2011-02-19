package de.xzise.xwarp.signwarps;

import me.taylorkelly.mywarp.WarpDestination;

public class MyWarpSign implements SignWarpDefinition {

	@Override
	public WarpDestination getDestination(String[] lines) {
		if (lines.length == 2 && lines[0].contains("MyWarp")) {
			return new WarpDestination(lines[1], null);
		} else {
			return null;
		}
	}

}
