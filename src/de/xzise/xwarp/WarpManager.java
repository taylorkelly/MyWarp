package de.xzise.xwarp;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.IdentificationInterface;
import de.xzise.xwarp.timer.CoolDown;
import de.xzise.xwarp.timer.WarmUp;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.warpable.WarperFactory;

/**
 * Wraps around {@link WarpList} to provide permissions support.
 * 
 * @author Fabian Neundorf
 */
public class WarpManager {

    private WarpList list;
    private Server server;
    private DataConnection data;
    private CoolDown coolDown;
    private WarmUp warmUp;

    public WarpManager(Plugin plugin, PluginProperties properties, DataConnection data) {
        this.list = new WarpList();
        this.server = plugin.getServer();
        this.data = data;
        this.loadFromDatabase();
        this.coolDown = new CoolDown(plugin, properties);
        this.warmUp = new WarmUp(plugin, properties, this.coolDown);
    }

    private void loadFromDatabase() {
        this.list.loadList(this.data.getWarps());
    }

    public void loadFromDatabase(CommandSender sender) {
        if (MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_RELOAD)) {
            this.loadFromDatabase();
        } else {
            sender.sendMessage(ChatColor.RED + "You have no permission to reload.");
        }
    }

    public void addWarp(String name, Positionable player, String newOwner, Visibility visibility) {
        PermissionTypes type;
        switch (visibility) {
        case PRIVATE:
            type = PermissionTypes.CREATE_PRIVATE;
            break;
        case PUBLIC:
            type = PermissionTypes.CREATE_PUBLIC;
            break;
        case GLOBAL:
            type = PermissionTypes.CREATE_GLOBAL;
            break;
        default:
            return;
        }
        if (MyWarp.permissions.permission(player, type)) {
            Warp warp = this.list.getWarp(name, newOwner, null);
            Warp globalWarp = (visibility == Visibility.GLOBAL ? this.list.getWarp(name) : null);
            if (warp != null) {
                player.sendMessage(ChatColor.RED + "Warp called '" + name + "' already exists (" + warp.name + ").");
            } else if (visibility == Visibility.GLOBAL && globalWarp != null) {
                player.sendMessage(ChatColor.RED + "Global warp called '" + name + "' already exists (" + globalWarp.name + ").");
            } else {
                warp = new Warp(name, newOwner, player.getLocation());
                warp.visibility = visibility;
                this.list.addWarp(warp);
                this.data.addWarp(warp);
                player.sendMessage("Successfully created '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
                switch (visibility) {
                case PRIVATE:
                    WarpManager.printPrivatizeMessage(player, warp);
                    break;
                case PUBLIC:
                    if (MyWarp.permissions.permissionOr(player, PermissionTypes.CREATE_PRIVATE, PermissionTypes.ADMIN_PRIVATE)) {
                        player.sendMessage("If you'd like to privatize it, use:");
                        player.sendMessage(ChatColor.GREEN + "/warp private \"" + warp.name + "\" " + warp.creator);
                    }
                    break;
                case GLOBAL:
                    player.sendMessage("This warp is now global available.");
                    break;
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You have no permission to add a warp.");
        }
    }

    public void blindAdd(List<Warp> warps) {
        this.blindAdd(warps.toArray(new Warp[0]));
    }

    public void blindAdd(Warp... warps) {
        for (Warp warp : warps) {
            this.list.addWarp(warp);
        }
        // if (this.getWarp(warp.name) == null) {
        // this.global.put(warp.name.toLowerCase(), warp);
        // } else if (warp.visibility == Visibility.GLOBAL) {
        // throw new
        // IllegalArgumentException("A global warp could not override an existing one.");
        // }
        // if (!putIntoPersonal(personal, warp)) {
        // throw new
        // IllegalArgumentException("A personal warp could not override an existing one.");
        // }
    }

    public void deleteWarp(String name, String owner, CommandSender sender) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (playerCanModifyWarp(sender, warp, Permissions.DELETE)) {
                this.list.deleteWarp(warp);
                this.data.deleteWarp(warp);
                sender.sendMessage("You have deleted '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to delete '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void give(String name, String owner, CommandSender sender, String giveeName) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (playerCanModifyWarp(sender, warp, Permissions.GIVE)) {
                if (warp.playerIsCreator(giveeName)) {
                    sender.sendMessage(ChatColor.RED + giveeName + " is already the owner.");
                } else {
                    Warp giveeWarp = this.getWarp(name, giveeName, null);
                    if (giveeWarp == null) {
                        IdentificationInterface ii = this.data.createIdentification(warp);
                        warp.setCreator(giveeName);
                        this.data.updateCreator(warp, ii);
                        sender.sendMessage("You have given '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' to " + ChatColor.GREEN + giveeName + ChatColor.WHITE + ".");
                        Player match = server.getPlayer(giveeName);
                        if (match != null) {
                            match.sendMessage("You've been given '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' by " + MinecraftUtil.getName(sender));
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "The new owner already has a warp named " + giveeWarp.name);
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to give '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }
    
    public void setMessage(String name, String owner, CommandSender sender, String message) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (playerCanModifyWarp(sender, warp, Permissions.MESSAGE)) {
                warp.welcomeMessage = message;
                this.data.updateMessage(warp);
                sender.sendMessage("You have successfully changed the welcome message.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to set the message of '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void privatize(String name, String owner, CommandSender sender) {
        Warp warp = this.list.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.PRIVATE)) {
                warp.visibility = Visibility.PRIVATE;
                this.list.updateVisibility(warp);
                this.data.updateVisibility(warp);
                WarpManager.printPrivatizeMessage(sender, warp);
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to privatize '" + name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void publicize(String name, String owner, CommandSender sender) {
        Warp warp = this.list.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.PUBLIC)) {
                warp.visibility = Visibility.PUBLIC;
                this.list.updateVisibility(warp);
                this.data.updateVisibility(warp);
                sender.sendMessage(ChatColor.AQUA + "You have publicized '" + warp.name + "'");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to publicize '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void globalize(String name, String owner, CommandSender sender) {
        Warp warp = this.list.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (playerCanModifyWarp(sender, warp, Permissions.GLOBAL)) {
                Warp existing = this.list.getWarp(name);
                if (existing == null || existing.visibility != Visibility.GLOBAL) {
                    warp.visibility = Visibility.GLOBAL;
                    this.data.updateVisibility(warp);
                    this.list.updateVisibility(warp);
                    sender.sendMessage("You have globalized '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'");
                } else if (existing.equals(warp) && existing.visibility == Visibility.GLOBAL) {
                    sender.sendMessage(ChatColor.RED + "This warp is already globalized.");
                } else {
                    sender.sendMessage(ChatColor.RED + "One global warp with this name already exists.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to globalize '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void invite(String name, String owner, CommandSender sender, String inviteeName) {
        Warp warp = this.list.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (playerCanModifyWarp(sender, warp, Permissions.INVITE)) {
                if (warp.playerIsInvited(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + inviteeName + " is already invited to this warp.");
                } else if (warp.playerIsCreator(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + inviteeName + " is the creator, of course he's the invited!");
                } else {
                    warp.invite(inviteeName);
                    this.data.updateEditor(warp, inviteeName);
                    sender.sendMessage("You have invited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " to '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
                    if (warp.visibility != Visibility.PRIVATE) {
                        sender.sendMessage(ChatColor.RED + "But '" + warp.name + "' is still public.");
                    }
                    Player match = this.server.getPlayer(inviteeName);
                    if (match != null) {
                        match.sendMessage("You've been invited to warp '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ".");
                        match.sendMessage("Use: " + ChatColor.GREEN + "/warp [to] \"" + warp.name + "\" " + warp.creator + ChatColor.WHITE + " to warp to it.");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to invite players to '" + name + "'.");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void uninvite(String name, String owner, CommandSender sender, String inviteeName) {
        Warp warp = this.list.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.UNINVITE)) {
                if (!warp.playerIsInvited(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + inviteeName + " is not invited to this warp.");
                } else if (warp.playerIsCreator(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + "You can't uninvite yourself. You're the creator!");
                } else {
                    warp.addEditor(inviteeName, "w");
                    this.data.updateEditor(warp, inviteeName);
                    sender.sendMessage("You have uninvited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " from '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
                    if (warp.visibility != Visibility.PRIVATE) {
                        sender.sendMessage(ChatColor.RED + "But '" + warp.name + "' is still public.");
                    }
                    Player match = this.server.getPlayer(inviteeName);
                    if (match != null) {
                        match.sendMessage("You've been uninvited to warp '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ". Sorry.");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to uninvite players from '" + warp.name + "'.");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void rename(String name, String owner, CommandSender sender, String newName) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (playerCanModifyWarp(sender, warp, Permissions.RENAME)) {
                // Creator has to exists!
                if (owner == null || owner.isEmpty()) {
                    owner = warp.creator;
                }
                if (warp.visibility == Visibility.GLOBAL && (this.getWarp(newName, null, null) != null)) {
                    sender.sendMessage(ChatColor.RED + "A global warp with this name already exists!");
                } else if (this.getWarp(newName, owner, null) != null) {
                    sender.sendMessage(ChatColor.RED + "You already have a warp with this name.");
                } else {
                    IdentificationInterface ii = this.data.createIdentification(warp);
                    this.list.deleteWarp(warp);
                    warp.rename(newName);
                    this.list.addWarp(warp);
                    this.data.updateName(warp, ii);
                    sender.sendMessage(ChatColor.AQUA + "You have renamed '" + warp.name + "'");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to change the position from '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void addEditor(String name, String owner, CommandSender sender, String editor, String permissions) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.ADD_EDITOR)) {
                warp.addEditor(editor, permissions);
                this.data.updateEditor(warp, editor);
                sender.sendMessage("You have added " + ChatColor.GREEN + editor + ChatColor.WHITE + " to '" + warp.name + ChatColor.WHITE + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to add an editor from '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void removeEditor(String name, String owner, CommandSender sender, String editor) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.REMOVE_EDITOR)) {
                warp.removeEditor(editor);
                this.data.updateEditor(warp, editor);
                sender.sendMessage("You have removed " + ChatColor.GREEN + editor + ChatColor.WHITE + " from '" + warp.name + ChatColor.WHITE + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to remove an editor from '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public Warp getWarp(String name, String owner, String playerName) {
        return this.list.getWarp(name, owner, playerName);
    }

    public List<Warp> getWarps() {
        return this.list.getWarps();
    }

    public MatchList getMatches(String name, CommandSender sender) {
        ArrayList<Warp> exactMatches = new ArrayList<Warp>();
        ArrayList<Warp> matches = new ArrayList<Warp>();
        List<Warp> all = this.getWarps();

        final Collator collator = Collator.getInstance();
        collator.setStrength(Collator.SECONDARY);
        Collections.sort(all, Warp.WARP_NAME_COMPARATOR);

        for (int i = 0; i < all.size(); i++) {
            Warp warp = all.get(i);
            if (warp.listWarp(sender)) {
                if (warp.name.equalsIgnoreCase(name)) {
                    exactMatches.add(warp);
                } else if (warp.name.toLowerCase().contains(name.toLowerCase())) {
                    matches.add(warp);
                }
            }
        }
        return new MatchList(exactMatches, matches);
    }

    public List<Warp> getSortedWarps(CommandSender sender, String creator, int start, int size) {
        List<Warp> ret = new ArrayList<Warp>(size);
        List<Warp> names;
        if (creator == null || creator.isEmpty()) {
            names = this.getWarps();
        } else {
            names = this.list.getWarps(creator);
        }

        final Collator collator = Collator.getInstance();
        collator.setStrength(Collator.SECONDARY);
        Collections.sort(names, Warp.WARP_NAME_COMPARATOR);

        int index = 0;
        int currentCount = 0;
        while (index < names.size() && ret.size() < size) {
            Warp warp = names.get(index);
            if (warp.listWarp(sender)) {
                if (currentCount >= start) {
                    ret.add(warp);
                } else {
                    currentCount++;
                }
            }
            index++;
        }
        return ret;
    }

    public void blindAdd(Warp warp) {
        this.list.addWarp(warp);
        // if (this.getWarp(warp.name) == null) {
        // this.global.put(warp.name.toLowerCase(), warp);
        // } else if (warp.visibility == Visibility.GLOBAL) {
        // throw new
        // IllegalArgumentException("A global warp could not override an existing one.");
        // }
        // if (!putIntoPersonal(personal, warp)) {
        // throw new
        // IllegalArgumentException("A personal warp could not override an existing one.");
        // }
    }

    public void updateLocation(String name, String owner, Positionable player) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(player));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(player, warp, Permissions.UPDATE)) {
                warp.setLocation(player);
                this.data.updateLocation(warp);
                player.sendMessage("You have updated '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to change the position from '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, player);
        }
    }

    public void warpTo(String name, String creator, CommandSender warper, Warpable warped, boolean viaSign) {
        this.warpTo(name, creator, warper, warped, viaSign, false);
    }

    public void warpTo(String name, String owner, CommandSender warper, Warpable warped, boolean viaSign, boolean worldForce) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(warper));
        if (warp != null) {
            if (warped.equals(warper) || MyWarp.permissions.permission(warper, PermissionTypes.ADMIN_WARP_OTHERS)) {
                if (warp.playerCanWarp(warper, viaSign)) {
                    Positionable warpedPos = WarperFactory.getPositionable(warped);
                    if (!worldForce && warpedPos != null && warp.getLocation().getWorld() != warpedPos.getLocation().getWorld()) {
                        warper.sendMessage(ChatColor.RED + "The selected warp is in another world.");
                        warper.sendMessage(ChatColor.RED + "To force warping use /warp force-to <warp> [owner].");
                    } else {
                        if (this.coolDown.playerHasCooled(warper)) {
                            this.warmUp.addPlayer(warper, warped, warp);
                        } else {
                            warper.sendMessage(ChatColor.RED + "You need to wait for the cooldown of " + this.coolDown.cooldownTime(warp.visibility, warper) + " s");
                        }
                    }
                } else {
                    warped.sendMessage(ChatColor.RED + "You do not have permission to warp to '" + warp.name + "'.");
                }
            } else {
                warper.sendMessage(ChatColor.RED + "You do not have permission to warp others.");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, warped);
        }
    }

    public int getSize(CommandSender sender, String creator) {
        return this.list.getSize(sender, creator);
    }

    public boolean isNameAvailable(Warp warp) {
        return this.isNameAvailable(warp.name, warp.creator);
    }

    public boolean isNameAvailable(String name, String owner) {
        return this.list.getWarp(name, owner, null) == null;
    }

    public static void sendMissingWarp(String name, String owner, CommandSender sender) {
        if (owner == null || owner.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Global warp '" + name + "' doesn't exist.");
        } else {
            sender.sendMessage(ChatColor.RED + "Player '" + owner + "' don't owns a warp named '" + name + "'.");
        }
    }

    private static void printPrivatizeMessage(CommandSender sender, Warp warp) {
        sender.sendMessage(ChatColor.WHITE + "You have privatized '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'");
        sender.sendMessage("If you'd like to invite others to it, use:");
        sender.sendMessage(ChatColor.GREEN + "/warp invite \"" + warp.name + "\" " + warp.creator + " <player>");
    }

    private static boolean playerCanModifyWarp(CommandSender sender, Warp warp, Permissions permission) {
        Player player = WarperFactory.getPlayer(sender);
        boolean canModify = false;
        if (player != null) {
            canModify = warp.playerCanModify(player, permission);
        }
        
        if (permission.defaultPermission != null) {
            return ((canModify && MyWarp.permissions.permission(sender, permission.defaultPermission)) || MyWarp.permissions.permission(sender, permission.adminPermission));
        } else {
            return (canModify || MyWarp.permissions.permission(sender, permission.adminPermission));
        }
    }
}
