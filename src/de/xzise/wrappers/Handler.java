package de.xzise.wrappers;

import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import de.xzise.XLogger;

public class Handler<W extends Wrapper> {

    private final Map<String, ? extends Factory<W>> factories;
    private final PluginManager pluginManager;
    protected final XLogger logger;
    private final String type;
    private final W nullary;
    private String pluginName;
    private W wrapper;

    public Handler(Map<String, ? extends Factory<W>> factories, PluginManager pluginManager, String type, String plugin, XLogger logger) {
        this(factories, null, pluginManager, type, plugin, logger);
    }

    public Handler(Map<String, ? extends Factory<W>> factories, W nullaryWrapper, PluginManager pluginManager, String type, String plugin, XLogger logger) {
        this.factories = factories;
        this.pluginManager = pluginManager;
        this.logger = logger;
        this.type = type;
        this.nullary = nullaryWrapper;
        this.pluginName = plugin;
    }

    public void setPluginName(String name) {
        this.pluginName = name;
    }

    public boolean isActive() {
        return this.wrapper != null && this.wrapper != this.nullary;
    }

    public String getWrapperName() {
        if (this.isActive()) {
            return this.wrapper.getPlugin().getDescription().getFullName();
        } else if (this.pluginManager == null) {
            return "Deactivated";
        } else {
            return "Not linked (yet)";
        }
    }
    
    public W getWrapper() {
        return this.wrapper == null ? this.nullary : this.wrapper;
    }

    public void load() {
        this.wrapper = null;
        for (String string : this.factories.keySet()) {
            this.load(this.pluginManager.getPlugin(string));
            if (this.wrapper != null) {
                return;
            }
        }
        if (this.wrapper == null) {
            if (this.pluginName == null) {
                this.logger.info("Loaded no " + this.type + " system, because it is deactivated.");
            } else {
                this.logger.info("No " + this.type + " system found until here. A " + this.type + " plugin will be maybe activated later.");
            }
        }
    }

    protected void loaded() {
    }

    protected void setWrapper(W wrapper) {
        this.wrapper = wrapper;
    }

    protected boolean customLoad(Plugin plugin) {
        return false;
    }

    public void load(Plugin plugin) {
        if (plugin != null && this.wrapper == null && this.pluginName != null) {
            PluginDescriptionFile pdf = plugin.getDescription();
            if (this.pluginName.isEmpty() || (pdf.getName().equalsIgnoreCase(this.pluginName))) {
                boolean loaded = this.customLoad(plugin);

                if (!loaded) {
                    Factory<W> factory = factories.get(pdf.getName());
                    if (factory != null) {
                        if (plugin.isEnabled()) {
                            try {
                                this.wrapper = factory.create(plugin, this.logger);
                                loaded = true;
                            } catch (InvalidWrapperException e) {
                                this.logger.warning("Error while loading the plugin " + pdf.getFullName() + " into " + this.type + " system.");
                                this.logger.warning("Error message: " + e.getMessage());
                                this.wrapper = null;
                            } catch (Exception e) {
                                this.logger.warning("Unspecified error while loading the plugin " + pdf.getFullName() + " into " + this.type + " system.");
                                this.logger.warning("Error message: '" + e.getMessage() + "' of '" + e.getClass().getSimpleName() + "'");
                                this.wrapper = null;
                            }
                        } else {
                            this.logger.warning("Skiped disabled " + this.type + " system: " + pdf.getFullName());
                        }
                    }
                }

                if (loaded) {
                    if (this.wrapper == null) {
                        this.logger.warning("Invalid " + this.type + " system found: " + pdf.getFullName());
                    } else {
                        this.loaded();
                        this.logger.info("Linked with " + this.type + " system: " + pdf.getFullName());
                    }
                }
            }
        }
    }

    public boolean unload(Plugin plugin) {
        if (this.wrapper != null && plugin == this.wrapper.getPlugin()) {
            this.wrapper = null;
            this.logger.info("Deactivated " + this.type + " system.");
            return true;
        } else {
            return false;
        }
    }

}
