package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.editors.EditorPermissions;

public class AddEditorCommand<W extends WarpObject<?>, M extends Manager<W>> extends EditorCommand<W, M> {

    public AddEditorCommand(M list, Server server, String label) {
        super(list, server, label, new String[] { "editor", "permissions" }, "add-editor");
    }

    public static <W extends WarpObject<?>, M extends Manager<W>> AddEditorCommand<W, M> create(M manager, Server server, String label) {
        return new AddEditorCommand<W, M>(manager, server, label);
    }
    
    @Override
    protected boolean executeEditorEdit(W warpObject, CommandSender sender, String editor, EditorPermissions.Type type, String[] parameters) {
        if (parameters.length == 1) {
            this.manager.addEditor(warpObject, sender, editor, type, parameters[0]);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Adds the editor to the warps editors list.", "The permissions define the allowed commands.", "Update (l), Rename (r), Uninvite (u), Invite (i), Private (0), Public (1), Global (2), Give (g), Delete (d), Warp (w).", "* allows all commands, s sets lruiw, all after a slash removes the permission", "Example: */d allows everthing except delete." };
    }

    @Override
    public String getSmallHelpText() {
        return "Adds the " + this.getParameterText(true, false, 0);
    }

    public boolean listHelp(CommandSender sender) {
        return true;
    }

}
