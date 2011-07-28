package de.xzise.xwarp.commands.wpa;

import org.bukkit.Server;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.commands.ManagerCommand;

public abstract class WPACommand extends ManagerCommand<WarpProtectionArea, Manager<WarpProtectionArea>> {

    protected WPACommand(Manager<WarpProtectionArea> manager, Server server, String[] parameters, String... commands) {
        super(manager, server, "wpa", parameters, commands);
    }

    protected WPACommand(Manager<WarpProtectionArea> manager, Server server, String parameterText, String... commands) {
        super(manager, server, "wpa", parameterText, commands);
    }
}