package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.EditorPermissions;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.EditorPermissions.Type;

public class RemoveEditorCommand extends WarpCommand {

    public RemoveEditorCommand(WarpManager list, Server server) {
        super(list, server, "editor", "remove-editor");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
        String editor = parameters[0];
        EditorPermissions.Type type;
        if (editor.startsWith("g:")) {
            type = Type.GROUP;
        } else {
            type = Type.GROUP;
            if (!editor.startsWith("o:")) {
                editor = MinecraftUtil.expandName(editor, this.server);
            }
        }
        this.list.removeEditor(warpName, creator, sender, editor, type);
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
