package de.xzise.xwarp.commands.wpa;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.DefaultWarpObject.EditorPermissionEntry;
import de.xzise.xwarp.editors.Editor;
import de.xzise.xwarp.editors.WarpPermissions;
import de.xzise.xwarp.editors.WarpProtectionAreaPermissions;
import de.xzise.xwarp.wrappers.permission.WPAPermissions;

public class InfoCommand extends WPACommand {

    public InfoCommand(Manager<WarpProtectionArea> manager, Server server) {
        super(manager, server, new String[0], "info");
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Show the information about the warp protection area." };
    }

    @Override
    public String getSmallHelpText() {
        return "Show warp protection area's information";
    }

    @Override
    protected boolean executeEdit(WarpProtectionArea warp, CommandSender sender, String[] parameters) {
        if (!XWarp.permissions.permission(sender, WPAPermissions.CMD_INFO)) {
            sender.sendMessage(ChatColor.RED + "You have no permission to gather information to warps.");
            return true;
        }

        sender.sendMessage("Warp protection area info: " + ChatColor.GREEN + warp.getName());
        String world = warp.getWorld();
        if (!warp.isValid()) {
            sender.sendMessage(ChatColor.RED + "The location is invalid!");
        }

        sender.sendMessage("Creator: " + de.xzise.xwarp.commands.warp.InfoCommand.getPlayerLine(warp.getCreator(), world));
        sender.sendMessage("Owner: " + de.xzise.xwarp.commands.warp.InfoCommand.getPlayerLine(warp.getOwner(), world));

        Collection<EditorPermissionEntry<WarpProtectionAreaPermissions>> allEditorPermissions = warp.getEditorPermissionsList();
        EditorLines editorLines = getEditorLines(allEditorPermissions);
        sender.sendMessage("Invitees: " + (editorLines.invitees.isEmpty() ? "None" : editorLines.invitees));
        sender.sendMessage("Editors: " + editorLines.editors);

//        FixedLocation location = warp.getLocation();
//        sender.sendMessage("Location: World = " + ChatColor.GREEN + world + ChatColor.WHITE + ", x = " + ChatColor.GREEN + location.getBlockX() + ChatColor.WHITE + ", y = " + ChatColor.GREEN + location.getBlockY() + ChatColor.WHITE + ", z = " + ChatColor.GREEN + location.getBlockZ());

        return true;
    }

    public static class EditorLines {
        public final String invitees;
        public final String editors;
        
        public EditorLines(String invitees, String editors) {
            this.invitees = invitees;
            this.editors = editors;
        }
    }
    
    public static <T extends Enum<T> & Editor> EditorLines getEditorLines(Collection<EditorPermissionEntry<T>> allPermissions) {
        String editor = "";
        String invitees = "";
        if (allPermissions.size() == 0) {
            editor = "None";
        } else {
            for (EditorPermissionEntry<T> editorPermissionEntry : allPermissions) {
                T[] permissions = editorPermissionEntry.editorPermissions.getByValue(true);
                if (permissions.length > 0) {
                    if (!editor.isEmpty()) {
                        editor += ChatColor.WHITE + ", ";
                    }
                    editor += ChatColor.GREEN + editorPermissionEntry.name + " ";
                    char[] editorPermissions = new char[permissions.length];
                    for (int j = 0; j < permissions.length; j++) {
                        editorPermissions[j] = permissions[j].getValue();
                        if (permissions[j] == WarpPermissions.WARP) {
                            if (!invitees.isEmpty()) {
                                invitees += ", ";
                            }
                            invitees += editorPermissionEntry.name;
                        }
                    }
                    editor += new String(editorPermissions);
                }
            }
        }
        return new EditorLines(invitees, editor);
    }
}
