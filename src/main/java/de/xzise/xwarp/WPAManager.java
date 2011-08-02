package de.xzise.xwarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.WarpManager.WarpObjectGetter;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.IdentificationInterface;
import de.xzise.xwarp.dataconnections.WarpProtectionConnection;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.WarpProtectionAreaPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.list.NonGlobalList;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class WPAManager extends CommonManager<WarpProtectionArea, NonGlobalList<WarpProtectionArea>> {

    private Server server;
    private WarpProtectionConnection data;

    public WPAManager(Plugin plugin, DataConnection data, PluginProperties properties) {
        super(new NonGlobalList<WarpProtectionArea>(), "warp protection area", properties);
        this.server = plugin.getServer();
        this.reload(data);
    }

    @Override
    public void reload(DataConnection data) {
        super.reload(data);
        this.data = MinecraftUtil.cast(WarpProtectionConnection.class, data);
        this.list.loadList(this.data.getProtectionAreas());
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
                this.list.deleteWarpObject(wpa);
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
            if (XWarp.permissions.permission(sender, PermissionTypes.ADMIN_CHANGE_CREATOR)) {
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
                        this.list.updateOwner(wpa, preOwner);
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
                    this.list.deleteWarpObject(wpa);
                    wpa.setName(name);
                    this.list.addWarpObject(wpa);
                    this.data.updateName(wpa, ii);
                    sender.sendMessage(ChatColor.AQUA + "You have renamed '" + wpa.getName() + "'");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to change the position from '" + wpa.getName() + "'");
            }
        }
    }

    @Override
    public void invite(WarpProtectionArea wpa, CommandSender sender, String inviteeName) {
        if (isWPAEnabled(sender)) {
            if (wpa.canModify(sender, WarpProtectionAreaPermissions.INVITE)) {
                if (wpa.hasPlayerPermission(inviteeName, WarpProtectionAreaPermissions.OVERWRITE)) {
                    sender.sendMessage(ChatColor.RED + inviteeName + " is already invited to this warp.");
                } else if (wpa.isOwn(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + inviteeName + " is the creator, of course he's the invited!");
                } else {
                    wpa.invite(inviteeName);
                    this.data.updateEditor(wpa, inviteeName, Type.PLAYER);
                    sender.sendMessage("You have invited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " to '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "'.");
                    Player match = this.server.getPlayer(inviteeName);
                    if (match != null) {
                        match.sendMessage("You've been invited to warp '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ".");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to invite players to '" + wpa.getName() + "'.");
            }
        }
    }

    @Override
    public void uninvite(WarpProtectionArea wpa, CommandSender sender, String inviteeName) {
        if (isWPAEnabled(sender)) {
            if (wpa.canModify(sender, WarpProtectionAreaPermissions.UNINVITE)) {
                if (!wpa.hasPlayerPermission(inviteeName, WarpProtectionAreaPermissions.OVERWRITE)) {
                    sender.sendMessage(ChatColor.RED + inviteeName + " is not invited to this warp.");
                } else if (wpa.isOwn(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + "You can't uninvite yourself. You're the creator!");
                } else {
                    EditorPermissions<WarpProtectionAreaPermissions> permissions = wpa.getEditorPermissions(inviteeName, Type.PLAYER);
                    if (permissions != null && permissions.remove(WarpProtectionAreaPermissions.OVERWRITE)) {
                        this.data.updateEditor(wpa, inviteeName, Type.PLAYER);
                        sender.sendMessage("You have uninvited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " from '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "'.");
                        Player match = this.server.getPlayer(inviteeName);
                        if (match != null) {
                            match.sendMessage("You've been uninvited to warp '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ". Sorry.");
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to uninvite players from '" + wpa.getName() + "'.");
            }
        }
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
    public void missing(String name, String owner, CommandSender sender) {
        if (owner == null || owner.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Protection area named '" + name + "' doesn't exist.");
        } else {
            sender.sendMessage(ChatColor.RED + "Player '" + owner + "' doesn't own a protection area named '" + name + "'.");
        }
    }

    public static class WPAGetter implements WarpObjectGetter<WarpProtectionArea> {

        private final WarpProtectionConnection connection;

        public WPAGetter(DataConnection connection) {
            this.connection = MinecraftUtil.cast(WarpProtectionConnection.class, connection);
        }

        @Override
        public List<WarpProtectionArea> get() {
            return connection == null ? new ArrayList<WarpProtectionArea>(0) : this.connection.getProtectionAreas();
        }

    }

    public void addWPA(WarpProtectionArea wpa) {
        this.list.addWarpObject(wpa);
        this.blindDataAdd(wpa);
    }

    @Override
    protected void blindDataAdd(WarpProtectionArea... areas) {
        this.data.addProtectionArea(areas);
    }

}
