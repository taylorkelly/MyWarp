package de.xzise.xwarp.listeners;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import de.xzise.wrappers.Handler;

public class BPUServerListener extends ServerListener {

    private final Handler<?>[] handlers;

    public BPUServerListener(Handler<?>... handlers) {
        this.handlers = handlers;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        for (Handler<?> handler : this.handlers) {
            handler.load(event.getPlugin());
        }
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        for (Handler<?> handler : this.handlers) {
            if (handler.unload(event.getPlugin())) {
                handler.load();
            }
        }
    }
}
