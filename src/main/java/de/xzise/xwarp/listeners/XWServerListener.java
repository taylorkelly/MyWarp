package de.xzise.xwarp.listeners;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapPlugin;

import de.xzise.wrappers.Handler;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.XWarp;

public class XWServerListener extends BPUServerListener {

    private final PluginProperties properties;
    private final WarpManager warpManager;
    private final PluginManager manager;

    public XWServerListener(PluginProperties properties, PluginManager manager, WarpManager warpManager, Handler<?>... handlers) {
        super(handlers);
        this.warpManager = warpManager;
        this.properties = properties;
        this.manager = manager;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        super.onPluginEnable(event);
        if (this.properties.isMarkerEnabled()) {
            try {
                if ("dynmap".equals(event.getPlugin().getDescription().getName())) {
                    this.load(event.getPlugin());
                }
            } catch (NoSuchMethodError e) {
                XWarp.logger.info("No dynmap marker API found.");
            }
        }
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        super.onPluginDisable(event);
        if (this.warpManager != null && "dynmap".equals(event.getPlugin().getDescription().getName())) {
            this.warpManager.setMarkerAPI(null);
        }
    }

    public void load() {
        Plugin p = this.manager.getPlugin("dynmap");
        if (p != null && p.isEnabled()) {
            this.load(p);
        }
    }

    public void load(Plugin plugin) {
        DynmapPlugin p = (DynmapPlugin) plugin;
        this.warpManager.setMarkerAPI(p.getMarkerAPI());
    }
}
