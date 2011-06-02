package de.xzise.wrappers.permissions;

import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;

import de.xzise.XLogger;
import de.xzise.wrappers.Factory;
import de.xzise.wrappers.InvalidWrapperException;

public class PermissionPluginWrapperFactory implements Factory<PermissionsWrapper> {

    @Override
    public PermissionsWrapper create(Plugin plugin, XLogger logger) throws InvalidWrapperException {
        if (plugin instanceof Permissions) {
            String[] version = plugin.getDescription().getVersion().split("\\.");
            if (version.length > 0) {
                int majorVersion = -1;
                try {
                    majorVersion = Integer.parseInt(version[0]);
                } catch (NumberFormatException e) {
                    majorVersion = -1;
                }
                if (majorVersion > 3) {
                    throw new InvalidWrapperException("Unknown Permissions version. (" + majorVersion + ")");
                }
                switch (majorVersion) {
                case 3:
                    return new Permissions3Wrapper((Permissions) plugin, logger);
                case 2:
                    return new PermissionsPluginWrapper((Permissions) plugin);
                default:
                    return null;
                }
            }
        }
        return null;
    }

}
