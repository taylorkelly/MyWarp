package de.xzise.xwarp.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.DefaultArrays;
import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;

/**
 * Default command structure with a warp definition. The command structure is:
 * <blockquote>
 * <code>/&lt;cmd&gt; &lt;command&gt; &lt;warpname&gt; [creator] &lt;parameter&gt;</code>
 * </blockquote> The parameter could be disabled.
 * 
 * @author Fabian Neundorf
 */
public abstract class ManagerCommand<W extends WarpObject<?>, M extends Manager<W>> extends DefaultSubCommand<M> {
    
    private final String cmd;
    private final int minLength;
    private final int maxLength;
    private final String[] parametersText;

    protected ManagerCommand(M manager, Server server, String label, String[] parameters, String... commands) {
        this(manager, server, label, parameters, false, commands);
    }

    private ManagerCommand(M manager, Server server, String label, String[] parameters, boolean optional, String... commands) {
        super(manager, server, commands);
        final char paramStart = optional ? '[' : '<';
        final char paramEnd = optional ? ']' : '>';

        this.parametersText = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            this.parametersText[i] = paramStart + parameters[i] + paramEnd;
        }

        // |{cmd name owner}| = 3
        this.maxLength = this.parametersText.length + 3;
        if (optional) {
            this.minLength = 2;
        } else {
            this.minLength = this.maxLength - 1;
        }
        
        StringBuilder commandLine = new StringBuilder(label);
        commandLine.append(" ");
        commandLine.append(this.commands[0]);
        commandLine.append(" <name> [owner]");
        for (int i = 0; i < this.parametersText.length; i++) {
            commandLine.append(" " + this.parametersText[i]);
        }
        this.cmd = commandLine.toString();
    }

    protected ManagerCommand(M manager, Server server, String label, String parameterText, String... commands) {
        this(manager, server, label, MinecraftUtil.isSet(parameterText) ? new String[] { parameterText } : new String[0], commands);
    }

    protected ManagerCommand(M manager, Server server, String label, String parameterText, boolean optional, String... commands) {
        this(manager, server, label, MinecraftUtil.isSet(parameterText) ? new String[] { parameterText } : new String[0], optional, commands);
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length != this.minLength && parameters.length != this.maxLength) {
            return false;
        }
        String owner = "";
        int parameterStartIndex = 2;
        if (parameters.length == this.maxLength) {
            owner = this.getPlayer(parameters[2]);
            parameterStartIndex++;
        }
        final String[] cmdParameters;
        if (parameters.length > parameterStartIndex) {
            cmdParameters = Arrays.copyOfRange(parameters, parameterStartIndex, parameters.length - 1);
        } else {
            cmdParameters = DefaultArrays.EMPTY_STRING_ARRAY;
        }

        W warpObject = this.manager.getWarpObject(parameters[1], owner, MinecraftUtil.getPlayerName(sender));
        if (warpObject != null) {
            return this.executeEdit(warpObject, sender, cmdParameters);
        } else {
            this.manager.missing(parameters[1], owner, sender);
            return true;
        }
    }

    protected abstract boolean executeEdit(W warpObject, CommandSender sender, String[] parameters);

    protected String getParameterText(boolean colorBeginning, boolean colorEnding, int index) {
        return (colorBeginning ? ChatColor.GREEN : "") + this.parametersText[index] + (colorEnding ? ChatColor.WHITE : "");
    }

    @Override
    public String getCommand() {
        return this.cmd;
    }
}
