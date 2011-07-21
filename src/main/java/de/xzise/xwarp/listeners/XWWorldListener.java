package de.xzise.xwarp.listeners;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;

public class XWWorldListener extends WorldListener {

    private final WarpManager manager;

    public XWWorldListener(WarpManager manager) {
        this.manager = manager;
    }

    @Override
    public void onWorldLoad(WorldLoadEvent event) {
        int i = 0;
        for (Warp warp : this.manager.getWarps()) {
            if (warp.getLocationWrapper().setWorld(event.getWorld())) {
                i++;
            }
        }
        if (i > 0) {
            MyWarp.logger.info("Because world '" + event.getWorld().getName() + "' was loaded " + i + " warp" + (i == 1 ? "" : "s") + " get valid.");
        }
    }
    
    @Override
    public void onWorldUnload(WorldUnloadEvent event) {
        int i = 0;
        for (Warp warp : this.manager.getWarps()) {
            if (warp.getLocationWrapper().unsetWorld(event.getWorld())) {
                i++;
            }
        }
        if (i > 0) {
            MyWarp.logger.info("Because world '" + event.getWorld().getName() + "' was unloaded " + i + " warp" + (i == 1 ? "" : "s") + " get invalid.");
        }
    }
}
