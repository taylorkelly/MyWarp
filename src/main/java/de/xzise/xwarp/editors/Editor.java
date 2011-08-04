package de.xzise.xwarp.editors;

import de.xzise.wrappers.permissions.Permission;

public interface Editor {

    String getName();
    
    char getValue();

    int getId();
    
    Permission<Boolean> getDefault();
    
    Permission<Boolean> getAdmin();

}
