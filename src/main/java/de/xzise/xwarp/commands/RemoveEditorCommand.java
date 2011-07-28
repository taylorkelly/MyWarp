package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.editors.EditorPermissions;

public class RemoveEditorCommand<W extends WarpObject<?>, M extends Manager<W>> extends EditorCommand<W, M> {

    public RemoveEditorCommand(M list, Server server, String label) {
        super(list, server, label, "editor", "remove-editor");
    }

    public static <W extends WarpObject<?>, M extends Manager<W>> RemoveEditorCommand<W, M> create(M manager, Server server, String label) {
        return new RemoveEditorCommand<W, M>(manager, server, label);
    }
    
    @Override
    protected boolean executeEditorEdit(W warpObject, CommandSender sender, String editor, EditorPermissions.Type type, String[] parameters) {
        if (parameters.length == 1) {
            this.manager.removeEditor(warpObject, sender, editor, type);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Remove the editor to the warps editors list." };
    }

    @Override
    public String getSmallHelpText() {
        return "Removes the " + this.getParameterText(true, false, 0);
    }

    public boolean listHelp(CommandSender sender) {
        return true;
    }

}
