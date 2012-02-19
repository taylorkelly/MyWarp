package de.xzise.xwarp.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.PluginManager;

import com.google.common.collect.ImmutableSet;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.wrappers.permission.WorldPermission;

public class XWWorldListener implements Listener {

    private final ImmutableSet<Manager<?>> managers;
    private final PluginManager pm;

    public XWWorldListener(PluginManager pm, Manager<?>... managers) {
        this.managers = ImmutableSet.copyOf(managers);
        this.pm = pm;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        int validCount = 0;
        World world = event.getWorld();
        for (Manager<?> manager : this.managers) {
            validCount += manager.setWorld(world);
        }
        if (validCount > 0) {
            XWarp.logger.info("Because world '" + event.getWorld().getName() + "' was loaded " + validCount + " warp object" + (validCount == 1 ? "" : "s") + " get valid.");
        }
        WorldPermission.register(world.getName(), this.pm);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        if (!event.isCancelled()) {
            int invalidCount = 0;
            World world = event.getWorld();
            for (Manager<?> manager : this.managers) {
                invalidCount += manager.unsetWorld(world);
            }
            if (invalidCount > 0) {
                XWarp.logger.info("Because world '" + event.getWorld().getName() + "' was unloaded " + invalidCount + " warp object" + (invalidCount == 1 ? "" : "s") + " get invalid.");
            }
            WorldPermission.unregister(world.getName(), this.pm);
        }
    }
}
