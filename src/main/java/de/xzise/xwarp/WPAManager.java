package de.xzise.xwarp;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.IdentificationInterface;
import de.xzise.xwarp.dataconnections.WarpProtectionConnection;
import de.xzise.xwarp.editors.WarpProtectionAreaPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.list.NonGlobalList;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class WPAManager implements Manager<WarpProtectionArea> {

    private NonGlobalList<WarpProtectionArea> protectionAreas;
    private Server server;
    private WarpProtectionConnection data;
    
    public WPAManager(Plugin plugin, DataConnection data) {
        this.protectionAreas = new NonGlobalList<WarpProtectionArea>();
        this.server = plugin.getServer();
        this.data = saveCast(WarpProtectionConnection.class, data);
    }
    
    public static <T> T saveCast(Class<T> castClass, Object o) {
        try {
            return castClass.cast(o);
        } catch (ClassCastException cce) {
            return null;
        }
    }
    
    @Override
    public void reload() {
        this.protectionAreas.loadList(this.data.getProtectionAreas());
    }
    
    private boolean isWPAEnabled(CommandSender sender) {
        if (this.data != null) {
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Warp protection areas are not enabled on this server.");
            return false;
        }
    }

    @Override
    public void delete(WarpProtectionArea wpa, CommandSender sender) {
        if (isWPAEnabled(sender)) {
            if (wpa.canModify(sender, WarpProtectionAreaPermissions.DELETE)) {
                this.protectionAreas.deleteWarpObject(wpa);
                this.data.deleteProtectionArea(wpa);
                sender.sendMessage("You have deleted '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to delete '" + wpa.getName() + "'");
            }
        }
    }

    @Override
    public void setCreator(WarpProtectionArea wpa, CommandSender sender, String creator) {
        if (isWPAEnabled(sender)) {
            if (MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_CHANGE_CREATOR)) {
                if (wpa.isCreator(creator)) {
                    sender.sendMessage(ChatColor.RED + creator + " is already the creator.");
                } else {
                    wpa.setCreator(creator);
                    this.data.updateCreator(wpa);
                    sender.sendMessage("You have changed the creator of '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "' to " + ChatColor.GREEN + creator + ChatColor.WHITE + ".");
                    Player match = server.getPlayer(creator);
                    if (match != null) {
                        match.sendMessage("You're now creator of '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "' by " + MinecraftUtil.getName(sender));
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to give '" + wpa.getName() + "'");
            }
        }
    }

    @Override
    public void setOwner(WarpProtectionArea wpa, CommandSender sender, String owner) {
        if (isWPAEnabled(sender)) {
            if (wpa.canModify(sender, WarpProtectionAreaPermissions.GIVE)) {
                if (wpa.isOwn(owner)) {
                    sender.sendMessage(ChatColor.RED + owner + " is already the owner.");
                } else {
                    WarpProtectionArea giveeWarp = this.getWarpObject(wpa.getName(), owner, null);
                    if (giveeWarp == null) {
                        String preOwner = wpa.getOwner();
                        IdentificationInterface<WarpProtectionArea> ii = this.data.createWarpProtectionAreaIdentification(wpa);
                        wpa.setOwner(owner);
                        this.protectionAreas.updateOwner(wpa, preOwner);
                        this.data.updateOwner(wpa, ii);
                        sender.sendMessage("You have given '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "' to " + ChatColor.GREEN + owner + ChatColor.WHITE + ".");
                        Player match = this.server.getPlayer(owner);
                        if (match != null) {
                            match.sendMessage("You've been given '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "' by " + MinecraftUtil.getName(sender));
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "The new owner already has a warp named " + giveeWarp.getName());
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to give '" + wpa.getName() + "'");
            }
        }
    }

    @Override
    public void setName(WarpProtectionArea wpa, CommandSender sender, String name) {
        if (isWPAEnabled(sender)) {
            if (wpa.canModify(sender, WarpProtectionAreaPermissions.RENAME)) {
                String owner = wpa.getOwner();
                if (this.getWarpObject(name, owner, null) != null) {
                    sender.sendMessage(ChatColor.RED + "You already have a warp with this name.");
                } else {
                    IdentificationInterface<WarpProtectionArea> ii = this.data.createWarpProtectionAreaIdentification(wpa);
                    this.protectionAreas.deleteWarpObject(wpa);
                    wpa.setName(name);
                    this.protectionAreas.addWarpObject(wpa);
                    this.data.updateName(wpa, ii);
                    sender.sendMessage(ChatColor.AQUA + "You have renamed '" + wpa.getName() + "'");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to change the position from '" + wpa.getName() + "'");
            }
        }
    }

    @Override
    public void invite(WarpProtectionArea warpObject, CommandSender sender, String inviteeName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uninvite(WarpProtectionArea warpObject, CommandSender sender, String inviteeName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addEditor(WarpProtectionArea wpa, CommandSender sender, String editor, Type type, String permissions) {
        if (isWPAEnabled(sender)) {
            if (wpa.canModify(sender, WarpProtectionAreaPermissions.ADD_EDITOR)) {
                wpa.addEditor(editor, type, WarpProtectionAreaPermissions.parseString(permissions));
                this.data.updateEditor(wpa, editor, type);
                sender.sendMessage("You have added " + ChatColor.GREEN + editor + ChatColor.WHITE + " to '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to add an editor from '" + wpa.getName() + "'");
            }
        }
    }

    @Override
    public void removeEditor(WarpProtectionArea wpa, CommandSender sender, String editor, Type type) {
        if (isWPAEnabled(sender)) {
            if (wpa.canModify(sender, WarpProtectionAreaPermissions.REMOVE_EDITOR)) {
                wpa.removeEditor(editor, type);
                this.data.updateEditor(wpa, editor, type);
                sender.sendMessage("You have removed " + ChatColor.GREEN + editor + ChatColor.WHITE + " from '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to remove an editor from '" + wpa.getName() + "'");
            }
        }
    }

    @Override
    public boolean isNameAvailable(WarpProtectionArea wpa) {
        return this.isNameAvailable(wpa.getName(), wpa.getOwner());
    }

    @Override
    public boolean isNameAvailable(String name, String owner) {
        return this.protectionAreas.getWarpObject(name, owner, null) == null;
    }

    @Override
    public WarpProtectionArea getWarpObject(String name, String owner, String playerName) {
        return this.protectionAreas.getWarpObject(name, owner, playerName);
    }

    @Override
    public void missing(String name, String owner, CommandSender sender) {
        if (owner == null || owner.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Protection area named '" + name + "' doesn't exist.");
        } else {
            sender.sendMessage(ChatColor.RED + "Player '" + owner + "' doesn't own a protection area named '" + name + "'.");
        }
    }

    @Override
    public WarpProtectionArea[] getWarpObjects() {
        return this.protectionAreas.getWarpObjects().toArray(new WarpProtectionArea[0]);
    }

}
