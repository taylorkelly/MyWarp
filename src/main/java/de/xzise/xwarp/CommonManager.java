package de.xzise.xwarp;

import de.xzise.xwarp.list.PersonalList;

public abstract class CommonManager<T extends WarpObject<?>, L extends PersonalList<T, ?>> implements Manager<T> {

    protected final L list;
    protected final PluginProperties properties;
    
    protected CommonManager(L list, PluginProperties properties) {
        this.properties = properties;
        this.list = list;
        this.list.setIgnoreCase(!properties.isCaseSensitive());
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

    @Override
    public void reload() {
        this.list.setIgnoreCase(!properties.isCaseSensitive());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] getWarpObjects() {
        return (T[]) this.list.getWarpObjects().toArray();
    }

}
