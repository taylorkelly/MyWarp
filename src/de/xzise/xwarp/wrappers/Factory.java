package de.xzise.xwarp.wrappers;

import org.bukkit.plugin.Plugin;

public interface Factory<T> {

    T create(Plugin plugin);
    
}
