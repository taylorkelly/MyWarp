package de.xzise.xwarp.list;

import de.xzise.xwarp.WarpObject;

public class NonGlobalList<T extends WarpObject> extends PersonalList<T, GlobalMap<T>> {

    @Override
    protected GlobalMap<T> createGlobalMap() {
        return new GlobalMap<T>();
    }

}
