package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class RemoveEditorCommand extends WarpCommand {

    public RemoveEditorCommand(WarpManager list, Server server) {
        super(list, server, "editor", "remove-editor");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
        this.list.removeEditor(warpName, creator, sender, this.getPlayer(parameters[0]));
        return true;
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Remove the editor to the warps editors list." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Removes the " + this.getParameterText(true, false, 0);
    }

    protected boolean listHelp(CommandSender sender) {
        return true;
    }

}
