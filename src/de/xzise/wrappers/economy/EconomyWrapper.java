package de.xzise.wrappers.economy;

import org.bukkit.plugin.Plugin;

import de.xzise.wrappers.Wrapper;

public interface EconomyWrapper extends Wrapper {

    AccountWrapper getAccount(String name);
    String format(double price);
    
    Plugin getPlugin();
    
}
