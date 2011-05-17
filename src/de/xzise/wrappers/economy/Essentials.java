package de.xzise.wrappers.economy;

import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.api.Economy;

import de.xzise.XLogger;

public class Essentials implements EconomyWrapper {

    private final Plugin economy;
    private final XLogger logger;
    
    public final class EssentialsAccount implements AccountWrapper {
        
        private final String name;
        
        public EssentialsAccount(String name, XLogger logger) {
            this.name = name;
            if (!Economy.accountExist(name)) {
                if (!Economy.newAccount(name)) {
                    logger.warning("EssentialsAccount: Couldn't create a new account named \"" + name + "\"!");
                }
            }
        }

        @Override
        public boolean hasEnough(double price) {
            return Economy.hasEnough(this.name, price);
        }

        @Override
        public void add(double price) {
            Economy.add(this.name, price);
        }
        
    }
    
    public Essentials(Plugin plugin, XLogger logger) {
        this.economy = plugin;
        this.logger = logger;
    }
    
    @Override
    public AccountWrapper getAccount(String name) {
        return new EssentialsAccount(name, this.logger);
    }

    @Override
    public String format(double price) {
        return Economy.format(price);
    }

    @Override
    public Plugin getPlugin() {
        return this.economy;
    }
    
    public static class Factory implements EconomyWrapperFactory {

        @Override
        public EconomyWrapper create(Plugin plugin, XLogger logger) {
            if (plugin instanceof com.earth2me.essentials.Essentials) {
                Essentials buf = new Essentials(plugin, logger);
                try {
                    buf.format(0);
                    return buf;
                } catch (NoClassDefFoundError e) {
                    logger.info("Essentials plugin found, but without Economy API. Should be there since Essentials 2.2.13");
                    return null;
                }
            } else {
                return null;
            }
        }
        
    }

}
