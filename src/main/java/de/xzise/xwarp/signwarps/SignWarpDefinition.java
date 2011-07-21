package de.xzise.xwarp.signwarps;

import de.xzise.xwarp.WarpDestination;

public interface SignWarpDefinition {

    WarpDestination getDestination(String[] lines);

}
