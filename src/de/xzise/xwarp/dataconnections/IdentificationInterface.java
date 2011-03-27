package de.xzise.xwarp.dataconnections;

import me.taylorkelly.mywarp.Warp;

public interface IdentificationInterface {

    /**
     * Determines if the given warp is identificated by this identification.
     * @param warp The tested warp.
     * @return If the warp is meant by this identification.
     */
    boolean isIdentificated(Warp warp);
    
}
