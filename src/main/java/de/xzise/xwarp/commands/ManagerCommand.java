package de.xzise.xwarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
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
    private final int length;
    private final String[] parametersText;

    protected ManagerCommand(M manager, Server server, String label, String[] parameters, String... commands) {
        super(manager, server, commands);
        this.parametersText = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            this.parametersText[i] = "<" + parameters[i] + ">";
        }
        this.length = this.parametersText.length + 1;
        
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

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length != this.length + 1 && parameters.length != this.length + 2) {
            return false;
        }
        String owner = "";
        int parameterStartIndex = 2;
        if (parameters.length == this.length + 2) {
            owner = this.getPlayer(parameters[2]);
            parameterStartIndex++;
        }
        String[] parameters2 = new String[this.parametersText.length];
        for (int i = 0; i < parameters2.length; i++) {
            parameters2[i] = parameters[i + parameterStartIndex];
        }
        
        W wo = this.manager.getWarpObject(parameters[1], owner, MinecraftUtil.getPlayerName(sender));
        if (wo != null) {
            return this.executeEdit(wo, sender, parameters2);
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
