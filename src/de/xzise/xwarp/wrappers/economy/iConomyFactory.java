package de.xzise.xwarp.wrappers.economy;

import org.bukkit.plugin.Plugin;

public class iConomyFactory implements EconomyWrapperFactory {

    @Override
    public EconomyWrapper create(Plugin plugin) {
        try {
            if (plugin instanceof com.iConomy.iConomy) {
                // Try newer
                try {
                    return new iConomy5(plugin);
                } catch (NoClassDefFoundError ncdfe) {
                    // Try v4
                    return new iConomy4(plugin);
                }
            } else if (plugin instanceof com.nijiko.coelho.iConomy.iConomy) {
                return new iConomy4(plugin);
            }
        } catch (NoClassDefFoundError e) {
            return null;
        }
        return null;
    }

}
