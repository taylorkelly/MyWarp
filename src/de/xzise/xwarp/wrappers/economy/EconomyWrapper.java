package de.xzise.xwarp.wrappers.economy;

import org.bukkit.plugin.Plugin;

public interface EconomyWrapper {

    AccountWrapper getAccount(String name);
    String format(int price);
    
    Plugin getPlugin();
    
}
