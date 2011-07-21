package de.xzise.xwarp.dataconnections;

import java.util.List;

import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.editors.EditorPermissions;

public interface WarpProtectionConnection extends DataConnection {

    List<WarpProtectionArea> getProtectionAreas();
    
    void addProtectionArea(WarpProtectionArea... area);
    void deleteProtectionArea(WarpProtectionArea area);
    
    void updateEditor(WarpProtectionArea warp, String name, EditorPermissions.Type type);
    
}
