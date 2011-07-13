package de.xzise.xwarp.list;

import java.util.List;

import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;
import de.xzise.MinecraftUtil;


public class WarpList<T extends Warp> extends PersonalList<T, WarpGlobalMap<T>> {

    @Override
    protected WarpGlobalMap<T> createGlobalMap() {
        return new WarpGlobalMap<T>();
    }
    
    /**
     * Returns the number of warps a player has created.
     * 
     * @param creator
     *            The creator of the warps. Has to be not null.
     * @param visibility
     *            The visibility of the warps. Set to null if want to show all
     *            visibilities.
     * @param world
     *            The world the warps has to be in. If null, it checks all
     *            worlds.
     * @return The number of warps the player has created (with the desired
     *         visibility).
     */
    public int getNumberOfWarps(String creator, Visibility visibility, String world) {
        int number = 0;
        if (MinecraftUtil.isSet(creator)) {
            List<T> warpObjects = this.getByCreator(creator);
            if (warpObjects != null) {
                for (T warpObject : warpObjects) {
                    if ((visibility == null || warpObject.getVisibility() == visibility) && (world == null || warpObject.getWorld().equals(world))) {
                        number++;
                    }
                }
            }
        }
        return number;
    }
    
    public void updateVisibility(T warp) {
        this.getByName(warp.getName()).updateGlobal(warp);
    }
}
