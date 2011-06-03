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
            String compVersion = plugin.getDescription().getVersion();
            String[] versionElements = compVersion.split("\\.");
            if (versionElements.length > 0) {
                int majorVersion;
                try {
                    majorVersion = Integer.parseInt(versionElements[0]);
                } catch (NumberFormatException e) {
                    majorVersion = -1;
                }
                switch (majorVersion) {
                case 3:
                    return new Permissions3Wrapper((Permissions) plugin, logger);
                case 2:
                    return new PermissionsPluginWrapper((Permissions) plugin);
                default:
                    throw new InvalidWrapperException("Unknown Permissions version. (" + compVersion + ")");
                }
            } else {
                throw new InvalidWrapperException("Unknown Permissions version. (" + compVersion + ")");
            }
        }
        return null;
    }

}
