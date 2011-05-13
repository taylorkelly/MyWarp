package de.xzise.wrappers;

import org.bukkit.plugin.Plugin;

import de.xzise.XLogger;


public interface Factory<T> {

    T create(Plugin plugin, XLogger logger);
    
}
