package de.xzise.xwarp.commands;

import org.bukkit.Server;

import de.xzise.MinecraftUtil;
import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.xwarp.Manager;

/**
 * Command like list/create etc.
 * 
 * @author Fabian Neundorf.
 */
public abstract class DefaultSubCommand<M extends Manager<?>> extends CommonHelpableSubCommand {

    protected final M manager;
    protected final Server server;

    /**
     * Creates a subcommand.
     * 
     * @param list
     *            The list to all warps.
     * @param server
     *            The server instance.
     * @param commands
     *            The commands.
     * @throws IllegalArgumentException
     *             If commands is empty.
     */
    protected DefaultSubCommand(M manager, Server server, String... commands) {
        super(commands);
        this.manager = manager;
        this.server = server;
    }

    protected String getPlayer(String name) {
        return MinecraftUtil.expandName(name, this.server);
    }
}
