package de.xzise.xwarp.commands;

import java.util.Arrays;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;

public abstract class EditorCommand<W extends WarpObject<?>, M extends Manager<W>> extends ManagerCommand<W, M> {

    protected EditorCommand(M manager, Server server, String label, String[] parameters, String... commands) {
        super(manager, server, label, parameters, commands);
    }

    protected EditorCommand(M manager, Server server, String label, String parameterText, String... commands) {
        super(manager, server, label, parameterText, commands);
    }
    
    @Override
    public final boolean executeEdit(W warpObject, CommandSender sender, String[] parameters) {
        String editor = parameters[0];
        EditorPermissions.Type type;
        if (editor.startsWith("p:")) {
            type = Type.PERMISSIONS;
        } else if (editor.startsWith("g:")) {
            type = Type.GROUP;
        } else {
            type = Type.PLAYER;
            if (!editor.startsWith("o:")) {
                editor = MinecraftUtil.expandName(editor, this.server);
            }
        }
        
        return this.executeEditorEdit(warpObject, sender, editor, type, Arrays.copyOfRange(parameters, 1, parameters.length));
    }
    
    protected abstract boolean executeEditorEdit(W warpObject, CommandSender sender, String editor, EditorPermissions.Type type, String[] parameters);

}
