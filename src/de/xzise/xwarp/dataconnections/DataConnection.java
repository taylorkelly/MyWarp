package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.util.List;

import me.taylorkelly.mywarp.Warp;

public interface DataConnection {

    boolean load(File file);

    boolean create(File file);

    void free();

    String getFilename();

    void clear();

    /**
     * This method should be called to create an identification for a warp,
     * before any changes to this warp will be performed.
     * 
     * @param warp
     *            The identification for the warp will be created.
     * @return An identification.
     */
    IdentificationInterface createIdentification(Warp warp);

    List<Warp> getWarps();

    void addWarp(Warp... warp);

    void deleteWarp(Warp warp);

    void updateCreator(Warp warp);

    void updateOwner(Warp warp, IdentificationInterface identification);

    void updateName(Warp warp, IdentificationInterface identification);

    void updateMessage(Warp warp);

    void updateVisibility(Warp warp);

    void updateLocation(Warp warp);

    void updateEditor(Warp warp, String name);

    void updatePrice(Warp warp);
}
