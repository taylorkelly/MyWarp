package de.xzise.xwarp.dataconnections;

import java.util.List;

import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.editors.EditorPermissions;

public interface WarpProtectionConnection extends DataConnection {

    IdentificationInterface<WarpProtectionArea> createWarpProtectionAreaIdentification(WarpProtectionArea area);
    
    List<WarpProtectionArea> getProtectionAreas();
    
    void addProtectionArea(WarpProtectionArea... areas);
    void deleteProtectionArea(WarpProtectionArea area);
    
    void updateEditor(WarpProtectionArea area, String name, EditorPermissions.Type type);
    void updateCreator(WarpProtectionArea area);
    void updateOwner(WarpProtectionArea area, IdentificationInterface<WarpProtectionArea> identification);
    void updateName(WarpProtectionArea area, IdentificationInterface<WarpProtectionArea> identification);

    void updateWorld(WarpProtectionArea area);
}
