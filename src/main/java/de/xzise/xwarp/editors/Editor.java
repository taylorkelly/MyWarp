package de.xzise.xwarp.editors;

import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public interface Editor {

    String getName();
    
    char getValue();

    int getId();
    
    PermissionTypes getDefault();
    
    PermissionTypes getAdmin();

}
