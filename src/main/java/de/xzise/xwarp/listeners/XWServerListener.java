package de.xzise.xwarp.listeners;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapCommonAPI;

import de.xzise.bukkit.util.wrappers.WrapperServerListener;
import de.xzise.wrappers.Handler;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WarpManager;

public class XWServerListener extends WrapperServerListener {

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
        if (checkPlugin(event.getPlugin(), this.properties.getMarkerPlugin())) {
            this.warpManager.setMarkerAPI(((DynmapCommonAPI) event.getPlugin()).getMarkerAPI());
        }
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        super.onPluginDisable(event);
        if (checkPlugin(event.getPlugin(), this.properties.getMarkerPlugin())) {
            this.warpManager.setMarkerAPI(null);
        }
    }

    private static boolean checkPlugin(final Plugin plugin, final String markerPluginName) {
        try {
            return plugin.getDescription().getName().equalsIgnoreCase(markerPluginName) && plugin instanceof DynmapCommonAPI;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public void load() {
        super.load();
        final String markerPluginName = this.properties.getMarkerPlugin();
        for (Plugin plugin : this.manager.getPlugins()) {
            if (plugin.isEnabled() && checkPlugin(plugin, markerPluginName)) {
                this.warpManager.setMarkerAPI(((DynmapCommonAPI) plugin).getMarkerAPI());
            }
        }
    }
}
