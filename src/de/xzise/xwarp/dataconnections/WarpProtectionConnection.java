package de.xzise.xwarp.dataconnections;

import java.util.List;

import de.xzise.xwarp.WarpProtectionArea;

public interface WarpProtectionConnection extends DataConnection {

    List<WarpProtectionArea> getProtectionAreas();
    
    void addProtectionArea(WarpProtectionArea area);
    void deleteProtectionArea(WarpProtectionArea area);
    
}
