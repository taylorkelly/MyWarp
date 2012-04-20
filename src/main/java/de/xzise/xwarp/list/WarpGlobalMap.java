package de.xzise.xwarp.list;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.Warp.Visibility;

class WarpGlobalMap<T extends Warp> extends GlobalMap<T> {

    private T global = null;

    @Override
    public void put(T warpObject) {
        if (warpObject.getVisibility() == Visibility.GLOBAL) {
            this.global = warpObject;
        }
        super.put(warpObject);
    }

    @Override
    public void delete(T warp) {
        if (warp.equals(this.global)) {
            this.global = null;
        }
        super.delete(warp);
    }

    @Override
    public T getWarpObject(String playerName) {
        if (this.global != null) {
            return this.global;
        }

        return super.getWarpObject(playerName);
    }

    public void updateGlobal(T warpObject) {
        if (this.global == null && warpObject.getVisibility() == Visibility.GLOBAL) {
            this.global = warpObject;
        } else if (warpObject.equals(this.global) && warpObject.getVisibility() != Visibility.GLOBAL) {
            this.global = null;
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.global = null;
    }

    @Override
    public boolean isAmbiguous() {
        return !(this.global != null || !super.isAmbiguous());
    }
}