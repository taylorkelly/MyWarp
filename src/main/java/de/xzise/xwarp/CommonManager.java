package de.xzise.xwarp;

import de.xzise.xwarp.list.NonGlobalList;

public abstract class CommonManager<T extends WarpObject<?>> implements Manager<T> {

    protected final NonGlobalList<T> list;
    
    protected CommonManager(NonGlobalList<T> list) {
        this.list = list;
    }

    @Override
    public boolean isNameAvailable(T warpObject) {
        return this.isNameAvailable(warpObject.getName(), warpObject.getOwner());
    }

    @Override
    public boolean isNameAvailable(String name, String owner) {
        return this.list.getWarpObject(name, owner, null) == null;
    }

    @Override
    public T getWarpObject(String name, String owner, String playerName) {
        return this.list.getWarpObject(name, owner, playerName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] getWarpObjects() {
        return (T[]) this.list.getWarpObjects().toArray();
    }

}
