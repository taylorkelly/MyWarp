package de.xzise.xwarp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import de.xzise.wrappers.Handler;

public class BPUServerListener implements Listener {

    private final Handler<?>[] handlers;

    public BPUServerListener(Handler<?>... handlers) {
        this.handlers = handlers;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        for (Handler<?> handler : this.handlers) {
            handler.load(event.getPlugin());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginDisable(PluginDisableEvent event) {
        for (Handler<?> handler : this.handlers) {
            if (handler.unload(event.getPlugin())) {
                handler.load();
            }
        }
    }
}
