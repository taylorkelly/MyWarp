package de.xzise.xwarp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import de.xzise.MinecraftUtil;
import de.xzise.metainterfaces.CommandSenderWrapper;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.metainterfaces.Nameable;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.Warp.Visibility;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.HModConnection;
import de.xzise.xwarp.dataconnections.IdentificationInterface;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.editors.WarpPermissions;
import de.xzise.xwarp.list.WarpList;
import de.xzise.xwarp.timer.CoolDown;
import de.xzise.xwarp.timer.WarmUp;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.wrappers.permission.Groups;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;
import de.xzise.xwarp.wrappers.permission.PermissionValues;
import de.xzise.xwarp.wrappers.permission.WPAPermissions;

/**
 * Wraps around {@link WarpList} to provide permissions support.
 * 
 * @author Fabian Neundorf
 */
public class WarpManager extends CommonManager<Warp, WarpList<Warp>> {

    private Server server;
    private DataConnection data;
    private final CoolDown coolDown;
    private final WarmUp warmUp;
    private final PluginProperties properties;
    private final File dataDirectory;
    private final MarkerManager manager;
    private EconomyHandler economy;
    private Manager<WarpProtectionArea> wpaManager;
    
    private static int markerSetId = 0;

    public WarpManager(Plugin plugin, EconomyHandler economy, PluginProperties properties, DataConnection data) {
        super(new WarpList<Warp>(), "warp", properties);
        this.server = plugin.getServer();
        this.coolDown = new CoolDown(plugin, properties);
        this.warmUp = new WarmUp(plugin, properties, this.coolDown);
        this.economy = economy;
        this.properties = properties;
        this.dataDirectory = plugin.getDataFolder();
        this.manager = new MarkerManager(properties);
        this.reload(data);
    }

    public WarmUp getWarmUp() {
        return this.warmUp;
    }

    public void setWPAManager(Manager<WarpProtectionArea> manager) {
        this.wpaManager = manager;
    }

    @Override
    public void reload(DataConnection data) {
        super.reload(data);
        this.data = data;
        this.list.loadList(this.data.getWarps());
    }

    /**
     * Returns the number of warps a player has created.
     * 
     * @param creator
     *            The creator of the warps. Has to be not null.
     * @param visibility
     *            The visibility of the warps. Set to null if want to show all
     *            visibilites.
     * @param world
     *            The world the warps has to be in. If null, it checks all
     *            worlds.
     * @return The number of warps the player has created (with the desired
     *         visibility).
     * @see {@link WarpList#getNumberOfWarps(String, Visibility, String)}
     */
    public int getAmountOfWarps(String creator, Visibility visibility, String world) {
        return this.list.getNumberOfWarps(creator, visibility, world);
    }

    private void printPayMessage(CommandSender payee, double amount) {
        if (amount > -0.0000001 && amount < 0.0000001) {
            if (this.properties.showFreePriceMessage()) {
                String freePriceMessage = "Yeah. This warp was " + ChatColor.GREEN + "free" + ChatColor.WHITE;
                // Little easteregg: Print with a 1 % change the (as beer) text
                if (Math.random() < 0.01) {
                    freePriceMessage += " (as beer)";
                }
                payee.sendMessage(freePriceMessage + "!");
            }
        } else if (amount > 0) {
            payee.sendMessage(ChatColor.WHITE + "You have paid " + ChatColor.GREEN + this.economy.format(amount) + ChatColor.WHITE + ".");
        } else {
            payee.sendMessage("Woooo! You got " + ChatColor.GREEN + this.economy.format(-amount) + ChatColor.WHITE + "!");
        }
    }

    private boolean isInProtectionArea(Positionable sender) {
        return this.isInProtectionArea(sender, sender instanceof Nameable ? ((Nameable) sender).getName() : MinecraftUtil.getPlayerName(sender));
    }

    private boolean isInProtectionArea(Positionable sender, String creator) {
        List<String> inProtectionArea = new ArrayList<String>();
        boolean skipProtectionTest = XWarp.permissions.permission(CommandSenderWrapper.getCommandSender(sender), WPAPermissions.ADMIN_IGNORE_PROTECTION_AREA);

        if (!skipProtectionTest && this.wpaManager != null) {
            for (WarpProtectionArea area : this.wpaManager.getWarpObjects()) {
                if (area.isWithIn(sender) && creator != null && !area.isAllowed(creator)) {
                    inProtectionArea.add(area.getName());
                }
            }
        }

        if (!skipProtectionTest && inProtectionArea.size() > 0) {
            switch (inProtectionArea.size()) {
            case 1:
                sender.sendMessage(ChatColor.RED + "Here is the warp protection area '" + inProtectionArea.get(0) + "'.");
                break;
            case 2:
            case 3:
            case 4:
                sender.sendMessage(ChatColor.RED + "Here are following warp protection areas:");
                for (String areaName : inProtectionArea) {
                    sender.sendMessage(ChatColor.RED + "- '" + areaName + "'");
                }
                break;
            default :
                sender.sendMessage(ChatColor.RED + "Here are " + inProtectionArea.size() + " warp protection areas.");
                break;
            }
            return true;
        } else {
            return false;
        }
    }

    public void addWarp(String name, Positionable player, String newOwner, Visibility visibility) {
        Warp warp = this.list.getWarpObject(name, newOwner, null);
        Warp globalWarp = (visibility == Visibility.GLOBAL ? this.list.getWarpObject(name) : null);
        if ((warp == null && globalWarp == null) || !this.properties.isCreationUpdating()) {
            if (globalWarp != warp && Visibility.GLOBAL == visibility)
                XWarp.logger.info("Everything okay! But inform the developer (xZise), that the global warp wasn't equals warp!");
            PermissionTypes type = Groups.CREATE_GROUP.get(visibility);
            PermissionValues limit = Groups.LIMIT_GROUP.get(visibility);

            CommandSender sender = CommandSenderWrapper.getCommandSender(player);

            if (XWarp.permissions.permission(sender, type)) {
                final String creator;
                String world = player.getLocation().getWorld().getName();
                if (player instanceof Nameable) {
                    creator = ((Nameable) player).getName();
                } else {
                    creator = MinecraftUtil.getPlayerName(player);
                }

                int warpsByCreator = this.list.getNumberOfWarps(creator, visibility, world);
                int totalWarpsByCreator = this.list.getNumberOfWarps(creator, null, world);
                int allowedMaximum = XWarp.permissions.getInteger(sender, limit);
                int allowedTotalMaximum = XWarp.permissions.getInteger(sender, PermissionValues.WARP_LIMIT_TOTAL);
                if (warpsByCreator >= allowedMaximum && allowedMaximum >= 0) {
                    player.sendMessage(ChatColor.RED + "You are allowed to create only " + allowedMaximum + " warps.");
                } else if (totalWarpsByCreator >= allowedTotalMaximum && allowedTotalMaximum >= 0) {
                    player.sendMessage(ChatColor.RED + "You are allowed to create only " + allowedTotalMaximum + " warps in total.");
                } else {
                    if (warp != null) {
                        sender.sendMessage(ChatColor.RED + "Warp called '" + name + "' already exists (" + warp.getName() + ").");
                    } else if (visibility == Visibility.GLOBAL && globalWarp != null) {
                        sender.sendMessage(ChatColor.RED + "Global warp called '" + name + "' already exists (" + globalWarp.getName() + ").");
                    } else {
                        if (!isInProtectionArea(player, creator)) {
                            double price = XWarp.permissions.getDouble(sender, Groups.PRICES_CREATE_GROUP.get(visibility));

                            switch (this.economy.pay(sender, price)) {
                            case PAID:
                                this.printPayMessage(sender, price);
                            case UNABLE:
                                warp = new Warp(name, creator, newOwner, new LocationWrapper(player.getLocation()));
                                warp.setVisibility(visibility);
                                warp.setMarkerManager(this.manager);
                                this.list.addWarpObject(warp);
                                this.data.addWarp(warp);
                                sender.sendMessage("Successfully created '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "'.");
                                switch (visibility) {
                                case PRIVATE:
                                    WarpManager.printPrivatizeMessage(sender, warp);
                                    break;
                                case PUBLIC:
                                    if (XWarp.permissions.permissionOr(sender, PermissionTypes.CREATE_PRIVATE, PermissionTypes.ADMIN_PRIVATE)) {
                                        sender.sendMessage("If you'd like to privatize it, use:");
                                        sender.sendMessage(ChatColor.GREEN + "/warp private \"" + warp.getName() + "\" " + warp.getOwner());
                                    }
                                    break;
                                case GLOBAL:
                                    sender.sendMessage("This warp is now global available.");
                                    break;
                                }
                                break;
                            case NOT_ENOUGH:
                                sender.sendMessage(ChatColor.RED + "You have not enough money to pay this creation.");
                                break;
                            }
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "You have no permission to add a warp.");
            }
        } else {
            this.updateLocation(warp, player);
        }
    }

    public void deleteWarp(String name, String owner, CommandSender sender) {
        Warp warp = this.getWarpObject(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (warp.canModify(sender, WarpPermissions.DELETE)) {
                this.list.deleteWarpObject(warp);
                this.data.deleteWarp(warp);
                sender.sendMessage("You have deleted '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to delete '" + warp.getName() + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    @Override
    public void delete(Warp warp, CommandSender sender) {
        if (warp.canModify(sender, WarpPermissions.DELETE)) {
            this.list.deleteWarpObject(warp);
            this.data.deleteWarp(warp);
            sender.sendMessage("You have deleted '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "'.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to delete '" + warp.getName() + "'");
        }
    }

    @Override
    public void setCreator(Warp warp, CommandSender sender, String newCreator) {
        if (XWarp.permissions.permission(sender, PermissionTypes.ADMIN_CHANGE_CREATOR)) {
            if (warp.isCreator(newCreator)) {
                sender.sendMessage(ChatColor.RED + newCreator + " is already the creator.");
            } else {
                warp.setCreator(newCreator);
                this.data.updateCreator(warp);
                sender.sendMessage("You have changed the creator of '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "' to " + ChatColor.GREEN + newCreator + ChatColor.WHITE + ".");
                Player match = server.getPlayer(newCreator);
                if (match != null) {
                    match.sendMessage("You're now creator of '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "' by " + MinecraftUtil.getName(sender));
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to give '" + warp.getName() + "'");
        }
    }

    @Override
    public void setOwner(Warp warp, CommandSender sender, String owner) {
        if (warp.canModify(sender, WarpPermissions.GIVE)) {
            if (warp.isOwn(owner)) {
                sender.sendMessage(ChatColor.RED + owner + " is already the owner.");
            } else {
                Warp giveeWarp = this.getWarpObject(warp.getName(), owner, null);
                if (giveeWarp == null) {
                    String preOwner = warp.getOwner();
                    IdentificationInterface<Warp> ii = this.data.createWarpIdentification(warp);
                    warp.setOwner(owner);
                    this.list.updateOwner(warp, preOwner);
                    this.data.updateOwner(warp, ii);
                    sender.sendMessage("You have given '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "' to " + ChatColor.GREEN + owner + ChatColor.WHITE + ".");
                    Player match = this.server.getPlayer(owner);
                    if (match != null) {
                        match.sendMessage("You've been given '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "' by " + MinecraftUtil.getName(sender));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "The new owner already has a warp named " + giveeWarp.getName());
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to give '" + warp.getName() + "'");
        }
    }

    public void setMessage(Warp warp, CommandSender sender, String message) {
        if (warp.canModify(sender, WarpPermissions.MESSAGE)) {
            warp.setWelcomeMessage(message);
            this.data.updateMessage(warp);
            sender.sendMessage("You have successfully changed the welcome message.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to set the message of '" + warp.getName() + "'");
        }
    }

    private boolean changeVisibility(CommandSender sender, Warp warp, Visibility visibility) {
        int warpsByCreator = this.list.getNumberOfWarps(warp.getCreator(), visibility, warp.getWorld());
        int totalWarpsByCreator = this.list.getNumberOfWarps(warp.getCreator(), null, warp.getWorld());
        int allowedMaximum = XWarp.permissions.getInteger(sender, Groups.LIMIT_GROUP.get(visibility));
        int allowedTotalMaximum = XWarp.permissions.getInteger(sender, PermissionValues.WARP_LIMIT_TOTAL);
        if (warpsByCreator >= allowedMaximum && allowedMaximum >= 0) {
            sender.sendMessage(ChatColor.RED + "The creator is allowed to create only " + allowedMaximum + " warps.");
        } else if (totalWarpsByCreator >= allowedTotalMaximum && allowedTotalMaximum >= 0) {
            sender.sendMessage(ChatColor.RED + "The creator is allowed to create only " + allowedTotalMaximum + " warps in total.");
        } else {
            double price = XWarp.permissions.getDouble(sender, Groups.PRICES_CREATE_GROUP.get(visibility));

            switch (this.economy.pay(sender, price)) {
            case PAID:
                this.printPayMessage(sender, price);
            case UNABLE:
                warp.setVisibility(visibility);
                this.list.updateVisibility(warp);
                this.data.updateVisibility(warp);
                return true;
            case NOT_ENOUGH:
                sender.sendMessage(ChatColor.RED + "You have not enough money to pay the change.");
                break;
            }
        }

        return false;
    }

    public void privatize(Warp warp, CommandSender sender) {
        if (warp.canModify(sender, WarpPermissions.PRIVATE)) {
            if (this.changeVisibility(sender, warp, Visibility.PRIVATE)) {
                WarpManager.printPrivatizeMessage(sender, warp);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to privatize '" + warp.getName() + "'");
        }
    }

    public void publicize(Warp warp, CommandSender sender) {
        if (warp.canModify(sender, WarpPermissions.PUBLIC)) {
            if (this.changeVisibility(sender, warp, Visibility.PUBLIC)) {
                sender.sendMessage(ChatColor.AQUA + "You have publicized '" + warp.getName() + "'");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to publicize '" + warp.getName() + "'");
        }
    }

    public void globalize(Warp warp, CommandSender sender) {
        if (warp.canModify(sender, WarpPermissions.GLOBAL)) {
            Warp existing = this.list.getWarpObject(warp.getName());
            if (existing == null || existing.getVisibility() != Visibility.GLOBAL) {
                if (this.changeVisibility(sender, warp, Visibility.GLOBAL)) {
                    sender.sendMessage("You have globalized '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "'");
                }
            } else if (existing.equals(warp) && existing.getVisibility() == Visibility.GLOBAL) {
                sender.sendMessage(ChatColor.RED + "This warp is already globalized.");
            } else {
                sender.sendMessage(ChatColor.RED + "One global warp with this name already exists.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to globalize '" + warp.getName() + "'");
        }
    }

    public void setPrice(Warp warp, CommandSender sender, double price) {
        WarpPermissions p;
        if (price < 0) {
            p = WarpPermissions.FREE;
        } else {
            p = WarpPermissions.PRICE;
        }
        if (warp.canModify(sender, p)) {
            warp.setPrice(price);
            this.data.updatePrice(warp);
            if (price < 0) {
                sender.sendMessage(ChatColor.AQUA + "Everybody could now warp for free to '" + warp.getName() + "'.");
            } else {
                sender.sendMessage(ChatColor.AQUA + "You have set the price for '" + warp.getName() + "'");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to change the price of '" + warp.getName() + "'");
        }
    }

    @Override
    public void invite(Warp warp, CommandSender sender, String inviteeName) {
        if (warp.canModify(sender, WarpPermissions.INVITE)) {
            if (warp.hasPlayerPermission(inviteeName, WarpPermissions.WARP)) {
                sender.sendMessage(ChatColor.RED + inviteeName + " is already invited to this warp.");
            } else if (warp.isOwn(inviteeName)) {
                sender.sendMessage(ChatColor.RED + inviteeName + " is the creator, of course he's the invited!");
            } else {
                warp.invite(inviteeName);
                this.data.updateEditor(warp, inviteeName, Type.PLAYER);
                sender.sendMessage("You have invited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " to '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "'.");
                if (warp.getVisibility() != Visibility.PRIVATE) {
                    sender.sendMessage(ChatColor.RED + "But '" + warp.getName() + "' is still public.");
                }
                Player match = this.server.getPlayer(inviteeName);
                if (match != null) {
                    match.sendMessage("You've been invited to warp '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ".");
                    match.sendMessage("Use: " + ChatColor.GREEN + "/warp [to] \"" + warp.getName() + "\" " + warp.getOwner() + ChatColor.WHITE + " to warp to it.");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to invite players to '" + warp.getName() + "'.");
        }
    }

    @Override
    public void uninvite(Warp warp, CommandSender sender, String inviteeName) {
        if (warp.canModify(sender, WarpPermissions.UNINVITE)) {
            if (!warp.hasPlayerPermission(inviteeName, WarpPermissions.WARP)) {
                sender.sendMessage(ChatColor.RED + inviteeName + " is not invited to this warp.");
            } else if (warp.isOwn(inviteeName)) {
                sender.sendMessage(ChatColor.RED + "You can't uninvite yourself. You're the creator!");
            } else {
                EditorPermissions<WarpPermissions> permissions = warp.getEditorPermissions(inviteeName, Type.PLAYER);
                if (permissions != null && permissions.remove(WarpPermissions.WARP)) {
                    this.data.updateEditor(warp, inviteeName, Type.PLAYER);
                    sender.sendMessage("You have uninvited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " from '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "'.");
                    if (warp.getVisibility() != Visibility.PRIVATE) {
                        sender.sendMessage(ChatColor.RED + "But '" + warp.getName() + "' is still public.");
                    }
                    Player match = this.server.getPlayer(inviteeName);
                    if (match != null) {
                        match.sendMessage("You've been uninvited to warp '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ". Sorry.");
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to uninvite players from '" + warp.getName() + "'.");
        }
    }

    @Override
    public void setName(Warp warp, CommandSender sender, String newName) {
        if (warp.canModify(sender, WarpPermissions.RENAME)) {
            String owner = warp.getOwner();
            if (warp.getVisibility() == Visibility.GLOBAL && (this.getWarpObject(newName, null, null) != null)) {
                sender.sendMessage(ChatColor.RED + "A global warp with this name already exists!");
            } else if (this.getWarpObject(newName, owner, null) != null) {
                sender.sendMessage(ChatColor.RED + "You already have a warp with this name.");
            } else {
                IdentificationInterface<Warp> ii = this.data.createWarpIdentification(warp);
                this.list.deleteWarpObject(warp);
                warp.setName(newName);
                this.list.addWarpObject(warp);
                this.data.updateName(warp, ii);
                sender.sendMessage(ChatColor.AQUA + "You have renamed '" + warp.getName() + "'");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to change the position from '" + warp.getName() + "'");
        }
    }

    @Override
    public void addEditor(Warp warp, CommandSender sender, String editor, EditorPermissions.Type type, String permissions) {
        if (warp.canModify(sender, WarpPermissions.ADD_EDITOR)) {
            warp.addEditor(editor, permissions, type);
            this.data.updateEditor(warp, editor, type);
            sender.sendMessage("You have added " + ChatColor.GREEN + editor + ChatColor.WHITE + " to '" + warp.getName() + ChatColor.WHITE + "'.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to add an editor from '" + warp.getName() + "'");
        }
    }

    @Override
    public void removeEditor(Warp warp, CommandSender sender, String editor, EditorPermissions.Type type) {
        if (warp.canModify(sender, WarpPermissions.REMOVE_EDITOR)) {
            warp.removeEditor(editor, type);
            this.data.updateEditor(warp, editor, type);
            sender.sendMessage("You have removed " + ChatColor.GREEN + editor + ChatColor.WHITE + " from '" + warp.getName() + ChatColor.WHITE + "'.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to remove an editor from '" + warp.getName() + "'");
        }
    }

    public MatchList getMatches(String name, CommandSender sender) {
        ArrayList<Warp> exactMatches = new ArrayList<Warp>();
        ArrayList<Warp> matches = new ArrayList<Warp>();
        List<Warp> all = Lists.newArrayList(this.getWarpObjects());

        for (int i = 0; i < all.size(); i++) {
            Warp warp = all.get(i);
            if (warp.isListed(sender)) {
                if (warp.getName().equalsIgnoreCase(name)) {
                    exactMatches.add(warp);
                } else if (warp.getName().toLowerCase().contains(name.toLowerCase())) {
                    matches.add(warp);
                }
            }
        }
        
        Collections.sort(exactMatches, Warp.WARP_NAME_COMPARATOR);
        Collections.sort(matches, Warp.WARP_NAME_COMPARATOR);
        return new MatchList(exactMatches, matches);
    }

    public void blindAdd(Warp warp) {
        this.list.addWarpObject(warp);
        warp.setMarkerManager(this.manager);
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

    public void updateLocation(Warp warp, Positionable player) {
        if (warp.canModify(player, WarpPermissions.UPDATE)) {
            if (!this.isInProtectionArea(player)) {
                warp.setLocation(player);
                this.data.updateLocation(warp);
                player.sendMessage("You have updated '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "'.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to change the position from '" + warp.getName() + "'");
        }
    }

    public void warpTo(String name, String owner, CommandSender warper, Warpable warped, boolean viaSign) {
        Warp warp = this.getWarpObject(name, owner, MinecraftUtil.getPlayerName(warper));
        if (warp != null) {
            this.warpTo(warp, warper, warped, viaSign, this.properties.isForceToUsed());
        } else {
            WarpManager.sendMissingWarp(name, owner, warped);
        }
    }

    public void warpTo(Warp warp, CommandSender warper, Warpable warped, boolean viaSign, boolean forced) {
        if (warp.getLocationWrapper().isValid()) {
            if (warped.equals(warper) || XWarp.permissions.permission(warper, PermissionTypes.ADMIN_WARP_OTHERS)) {
                if (warp.playerCanWarp(warper, viaSign)) {
                    if (!forced && !warp.isSave()) {
                        warper.sendMessage(ChatColor.RED + "The selected warp is maybe not save!");
                        warper.sendMessage(ChatColor.RED + "To force warping use /warp force-to <warp> [owner].");
                    } else {
                        double price = XWarp.permissions.getDouble(warper, Groups.PRICES_TO_GROUP.get(warp.getVisibility()));

                        if (this.coolDown.playerHasCooled(warper)) {
                            if (warp.isFree()) {
                                this.printPayMessage(warper, 0);
                                this.warmUp.addPlayer(warper, warped, warp);
                            } else {
                                switch (this.economy.pay(warper, warp.getOwner(), warp.getPrice(), price)) {
                                case PAID:
                                    double totalPrice = warp.getPrice() + price;
                                    this.printPayMessage(warper, totalPrice);
                                case UNABLE:
                                    this.warmUp.addPlayer(warper, warped, warp);
                                    break;
                                case NOT_ENOUGH:
                                    warper.sendMessage(ChatColor.RED + "You have not enough money to pay this warp.");
                                    break;
                                }
                            }
                        } else {
                            warper.sendMessage(ChatColor.RED + "You need to wait for the cooldown of " + CoolDown.getCooldownTime(warp, warper) + " s");
                        }
                    }
                } else {
                    warped.sendMessage(ChatColor.RED + "You do not have permission to warp to '" + warp.getName() + "'.");
                }
            } else {
                warper.sendMessage(ChatColor.RED + "You do not have permission to warp others.");
            }
        } else {
            warper.sendMessage(ChatColor.RED + "The location of the warp is invalid.");
        }
    }

    private interface Tester<T> {
        boolean test(T t);
    }

    private static class SimpleTester<T> implements Tester<T> {

        private final boolean t;

        public SimpleTester(boolean value) {
            this.t = value;
        }

        @Override
        public boolean test(T o) {
            return t;
        }

    }

    private static final class CollectionContainCheck<T> implements Tester<T> {

        private final Collection<T> collection;

        public CollectionContainCheck(Collection<T> collection) {
            this.collection = collection;
        }

        @Override
        public boolean test(T t) {
            return collection.contains(t);
        }

    }

    public static <T> Tester<T> getTester(Collection<T> collection) {
        if (MinecraftUtil.isSet(collection)) {
            return new CollectionContainCheck<T>(collection);
        } else {
            return new SimpleTester<T>(true);
        }
    }

    public List<Warp> getWarps(CommandSender sender, Set<String> creators, Set<String> owners, Set<String> worlds, Set<Visibility> visibilites) {
        List<Warp> allWarps = new ArrayList<Warp>();

        if (MinecraftUtil.isSet(owners)) {
            for (String owner : owners) {
                allWarps.addAll(this.list.getWarps(owner));
            }
        } else {
            allWarps.addAll(this.list.getWarpObjects());
        }

        ArrayList<Warp> validWarps = new ArrayList<Warp>(allWarps.size());

        Tester<String> creatorTester = getTester(creators);
        Tester<String> ownerTester = getTester(owners);
        Tester<String> worldTester = getTester(worlds);
        Tester<Visibility> visibilityTester = getTester(visibilites);

        for (int i = allWarps.size() - 1; i >= 0; i--) {
            Warp w = allWarps.get(i);
            if ((creatorTester.test(w.getCreator().toLowerCase())) && (ownerTester.test(w.getOwner().toLowerCase())) && (worldTester.test(w.getLocationWrapper().getWorld().toLowerCase())) && (visibilityTester.test(w.getVisibility())) && (w.isListed(sender))) {
                validWarps.add(w);
            }
        }

        if (validWarps.size() > 0) {
            // Removes everything which was to much
            validWarps.trimToSize();
            Collections.sort(validWarps, Warp.WARP_NAME_COMPARATOR);
        }

        return validWarps;
    }

    public int getSize(CommandSender sender, String creator) {
        return this.list.getSize(sender, creator);
    }

    @Override
    public void missing(String name, String owner, CommandSender sender) {
        sendMissingWarp(name, owner, sender);
    }

    public static void sendMissingWarp(String name, String owner, CommandSender sender) {
        if (owner == null || owner.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Global warp '" + name + "' doesn't exist.");
        } else {
            sender.sendMessage(ChatColor.RED + "Player '" + owner + "' doesn't own a warp named '" + name + "'.");
        }
    }

    private static void printPrivatizeMessage(CommandSender sender, Warp warp) {
        sender.sendMessage(ChatColor.WHITE + "You have privatized '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "'");
        sender.sendMessage("If you'd like to invite others to it, use:");
        sender.sendMessage(ChatColor.GREEN + "/warp invite \"" + warp.getName() + "\" " + warp.getOwner() + " <player>");
    }

    public void setListed(Warp warp, CommandSender sender, Boolean listed) {
        if (warp.canModify(sender, WarpPermissions.LIST)) {
            warp.setListed(listed);
            this.data.updateVisibility(warp);
            sender.sendMessage("You have " + (listed ? "listed" : "unlisted") + " '" + ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "'.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to change the listed status from '" + warp.getName() + "'");
        }
    }

    public static interface WarpObjectGetter<W extends WarpObject<?>> {
        List<W> get();
    }
    
    public static class WarpGetter implements WarpObjectGetter<Warp> {

        private final DataConnection connection;
        private final String owner;
        
        public WarpGetter(DataConnection connection, String owner) {
            this.connection = connection;
            this.owner = owner;
        }
        
        @Override
        public List<Warp> get() {
            if (this.connection instanceof HModConnection) {
                return ((HModConnection) this.connection).getWarps(this.owner);
            } else {
                return this.connection.getWarps();
            }
        }
        
    }

    @Override
    protected void blindDataAdd(Warp... warps) {
        this.data.addWarp(warps);
    }

    @Override
    public int setWorld(World world) {
        int result = 0;
        for (Warp warp : this.getWarpObjects()) {
            if (warp.getLocationWrapper().setWorld(world)) {
                result++;
            }
        }
        return result;
    }

    @Override
    public int unsetWorld(World world) {
        int result = 0;
        for (Warp warp : this.getWarpObjects()) {
            if (warp.getLocationWrapper().unsetWorld(world)) {
                result++;
            }
        }
        return result;
    }

    public void setCooldown(Warp warp, CommandSender sender, int time) {
        if (warp.canModify(sender, WarpPermissions.COOLDOWN)) {
            warp.setCoolDown(time);
            this.data.updateCoolDown(warp);
            sender.sendMessage("You have successfully changed the cooldown.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to set the cooldown of '" + warp.getName() + "'");
        }
    }

    public void setWarmup(Warp warp, CommandSender sender, int time) {
        if (warp.canModify(sender, WarpPermissions.WARMUP)) {
            warp.setWarmUp(time);
            this.data.updateWarmUp(warp);
            sender.sendMessage("You have successfully changed the warmup.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to set the warmup of '" + warp.getName() + "'");
        }
    }

    private void updateMarkerAPI() {
        for (Warp warp : this.getWarpObjects()) {
            warp.setMarkerManager(this.manager);
        }
    }
    
    public void setMarkerAPI(MarkerAPI api) {
        if (api == null) {
            this.manager.setMarkerSet(null);
            this.manager.setMarkerIcon(null);
            this.updateMarkerAPI();
        } else {
            try {
                InputStream is = new FileInputStream(new File(this.dataDirectory, this.properties.getMarkerPNG()));
                MarkerIcon icon = api.getMarkerIcon("xwarp.warp.icon");
                if (icon == null) {
                    icon = api.createMarkerIcon("xwarp.warp.icon", "Warps Icon", is);
                }
                if (icon != null) {
                    this.manager.setMarkerIcon(icon);
                    this.manager.setMarkerSet(api.createMarkerSet("xwarp.warp.set" + markerSetId++, "Warps", ImmutableSet.of(icon), false));
                    this.updateMarkerAPI();
                } else {
                    XWarp.logger.severe("Marker icon isn't set.");
                }
            } catch (FileNotFoundException e) {
                XWarp.logger.severe("Unable to load marker file.", e);
            }
        }
    }
}
