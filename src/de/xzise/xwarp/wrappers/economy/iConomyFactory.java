package de.xzise.xwarp.wrappers.economy;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.plugin.Plugin;

public class iConomyFactory implements EconomyWrapperFactory {

    @Override
    public EconomyWrapper create(Plugin plugin) {
        try {
            if (plugin instanceof com.iConomy.iConomy) {
                return new iConomy5(plugin);
            } else {
                return null;
            }
        } catch (NoClassDefFoundError e) {
            MyWarp.logger.info("The plugin \"" + plugin.getDescription().getFullName() + "\" is not iConomy 5 compatible.");
        }
        
        try {
            if (plugin instanceof com.nijiko.coelho.iConomy.iConomy) {
                return new iConomy4(plugin);
            } else {
                return null;
            }
        } catch (NoClassDefFoundError e) {
            MyWarp.logger.info("The plugin \"" + plugin.getDescription().getFullName() + "\" is not iConomy 4 compatible.");
        }
        return null;
    }

}
