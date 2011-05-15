package de.xzise.xwarp.wrappers.permission;

import java.util.EnumMap;
import java.util.Map;

import de.xzise.wrappers.permissions.VisibilityPermission;

import me.taylorkelly.mywarp.Warp.Visibility;

public class Group<T extends VisibilityPermission> {

    private final Map<Visibility, T> groups = new EnumMap<Visibility, T>(Visibility.class);
    
    public Group(T... permissions) {
        if (permissions.length != Visibility.values().length) {
            throw new IllegalArgumentException("For each visibility has to be one permission.");
        }
        for (T permission : permissions) {
            if (this.groups.put(permission.getVisibility(), permission) != null) {
                throw new IllegalArgumentException("Don't use two permissions fro the same visibility.");
            }
        }
    }
    
    public T get(Visibility visibility) {
        return this.groups.get(visibility);
    }

}