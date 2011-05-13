package de.xzise.wrappers.economy;

import org.bukkit.plugin.Plugin;

import de.xzise.XLogger;

public class iConomyFactory implements EconomyWrapperFactory {

    @Override
    public EconomyWrapper create(Plugin plugin, XLogger logger) {
        try {
            if (plugin instanceof com.iConomy.iConomy) {
                return new iConomy5(plugin);
            } else {
                return null;
            }
        } catch (NoClassDefFoundError e) {
            logger.info("The plugin \"" + plugin.getDescription().getFullName() + "\" is not iConomy 5 compatible.");
        }
        
        try {
            if (plugin instanceof com.nijiko.coelho.iConomy.iConomy) {
                return new iConomy4(plugin);
            } else {
                return null;
            }
        } catch (NoClassDefFoundError e) {
            logger.info("The plugin \"" + plugin.getDescription().getFullName() + "\" is not iConomy 4 compatible.");
        }
        return null;
    }

}
