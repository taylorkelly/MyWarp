package de.xzise.xwarp.commands.warp;

import org.bukkit.Server;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.commands.ManagerCommand;
import de.xzise.xwarp.commands.WarpCommandMap;

/**
 * Default command structure with a warp definition. The command structure is:
 * <blockquote>
 * <code>/warp &lt;command&gt; &lt;warpname&gt; [creator] &lt;parameter&gt;</code>
 * </blockquote> The parameter could be disabled.
 * 
 * @author Fabian Neundorf
 */
public abstract class WarpCommand extends ManagerCommand<Warp, WarpManager> {
    protected WarpCommand(WarpManager manager, Server server, String[] parameters, String... commands) {
        super(manager, server, WarpCommandMap.LABEL, parameters, commands);
    }

    protected WarpCommand(WarpManager manager, Server server, String parameterText, String... commands) {
        super(manager, server, WarpCommandMap.LABEL, parameterText, commands);
    }

    protected WarpCommand(WarpManager manager, Server server, String parameterText, boolean optional, String... commands) {
        super(manager, server, WarpCommandMap.LABEL, parameterText, optional, commands);
    }
}
