package de.xzise.xwarp.listeners;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

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
            MyWarp.logger.info("Loaded world '" + event.getWorld().getName() + "' and updated " + i + " warp" + (i == 1 ? "." : "s."));
        }
    }
}
