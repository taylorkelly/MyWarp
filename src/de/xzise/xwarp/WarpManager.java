package de.xzise.xwarp;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.xzise.MinecraftUtil;
import de.xzise.metainterfaces.CommandSenderWrapper;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.metainterfaces.Nameable;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.IdentificationInterface;
import de.xzise.xwarp.dataconnections.WarpProtectionConnection;
import de.xzise.xwarp.list.NonGlobalList;
import de.xzise.xwarp.list.WarpList;
import de.xzise.xwarp.timer.CoolDown;
import de.xzise.xwarp.timer.WarmUp;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.wrappers.permission.Groups;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;
import de.xzise.xwarp.wrappers.permission.PermissionValues;

/**
 * Wraps around {@link WarpList} to provide permissions support.
 * 
 * @author Fabian Neundorf
 */
public class WarpManager {

    private WarpList<Warp> list;
    private NonGlobalList<WarpProtectionArea> protectionAreas;
    private Server server;
    private DataConnection data;
    private CoolDown coolDown;
    private WarmUp warmUp;
    private EconomyHandler economy;
    private PluginProperties properties;

    public WarpManager(Plugin plugin, EconomyHandler economy, PluginProperties properties, DataConnection data) {
        this.list = new WarpList<Warp>();
        this.protectionAreas = new NonGlobalList<WarpProtectionArea>();
        this.server = plugin.getServer();
        this.properties = properties;
        this.data = data;
        this.coolDown = new CoolDown(plugin, properties);
        this.warmUp = new WarmUp(plugin, properties, this.coolDown);
        this.economy = economy;
        this.loadFromDatabase();
    }
    
    public WarmUp getWarmUp() {
        return this.warmUp;
    }

    private void loadFromDatabase() {
        this.list.loadList(this.data.getWarps());
        if (this.data instanceof WarpProtectionConnection) {
            List<WarpProtectionArea> areas = ((WarpProtectionConnection) this.data).getProtectionAreas();
            for (WarpProtectionArea area : areas) {
                this.protectionAreas.addWarpObject(area);
            }
        } else {
            MyWarp.logger.info("No warp protection area feature available.");
        }
    }
    
    public void reload(CommandSender sender) {
        if (MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_RELOAD)) {
            this.properties.read();
            this.loadFromDatabase();
            this.economy.reloadConfig(this.properties.getEconomyPlugin(), this.properties.getEconomyBaseAccount());
            sender.sendMessage("Reload successfully!");
        } else {
            sender.sendMessage(ChatColor.RED + "You have no permission to reload.");
        }
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
    
    public void addWarp(String name, Positionable player, String newOwner, Visibility visibility) {
        Warp warp = this.list.getWarpObject(name, newOwner, null);
        Warp globalWarp = (visibility == Visibility.GLOBAL ? this.list.getWarpObject(name) : null);
        if ((warp == null && globalWarp == null) || !this.properties.isCreationUpdating()) {
            if (globalWarp != warp && Visibility.GLOBAL == visibility)
                MyWarp.logger.info("Everything okay! But inform the developer (xZise), that the global warp wasn't equals warp!");
            PermissionTypes type = Groups.CREATE_GROUP.get(visibility);
            PermissionValues limit = Groups.LIMIT_GROUP.get(visibility);
            
            CommandSender sender = CommandSenderWrapper.getCommandSender(player);
            
            if (MyWarp.permissions.permission(sender, type)) {
                String creator = "";
                String world = player.getLocation().getWorld().getName();
                if (player instanceof Nameable) {
                    creator = ((Nameable) player).getName();
                } else {
                    creator = MinecraftUtil.getPlayerName(player);
                }
    
                int warpsByCreator = this.list.getNumberOfWarps(creator, visibility, world);
                int totalWarpsByCreator = this.list.getNumberOfWarps(creator, null, world);
                int allowedMaximum = MyWarp.permissions.getInteger(sender, limit);
                int allowedTotalMaximum = MyWarp.permissions.getInteger(sender, PermissionValues.WARP_LIMIT_TOTAL);
                if (warpsByCreator >= allowedMaximum && allowedMaximum >= 0) {
                    player.sendMessage(ChatColor.RED + "You are allowed to create only " + allowedMaximum + " warps.");
                } else if (totalWarpsByCreator >= allowedTotalMaximum && allowedTotalMaximum >= 0) {
                    player.sendMessage(ChatColor.RED + "You are allowed to create only " + allowedTotalMaximum + " warps in total.");
                } else {
                    if (warp != null) {
                        sender.sendMessage(ChatColor.RED + "Warp called '" + name + "' already exists (" + warp.name + ").");
                    } else if (visibility == Visibility.GLOBAL && globalWarp != null) {
                        sender.sendMessage(ChatColor.RED + "Global warp called '" + name + "' already exists (" + globalWarp.name + ").");
                    } else {
                        List<String> inProtectionArea = new ArrayList<String>();
                        boolean skipProtectionTest = MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_IGNORE_PROTECTION_AREA);
                        if (!skipProtectionTest) {
                            for (WarpProtectionArea area : this.protectionAreas.getWarpObjects()) {
                                if (area.isWithIn(player) && creator != null && area.isAllowed(creator)) {
                                    inProtectionArea.add(area.getName());
                                }
                            }
                        }
                    
                        if (!skipProtectionTest && inProtectionArea.size() > 0) {
                            //TODO: Tell which protection areas?
                            player.sendMessage(ChatColor.RED + "Here is a warp creation protection area.");
                        } else {
                            double price = MyWarp.permissions.getDouble(sender, Groups.PRICES_CREATE_GROUP.get(visibility));
        
                            switch (this.economy.pay(sender, price)) {
                            case PAID:
                                this.printPayMessage(sender, price);
                            case UNABLE:
                                warp = new Warp(name, creator, newOwner, new LocationWrapper(player.getLocation()));
                                warp.setVisibility(visibility);
                                this.list.addWarpObject(warp);
                                this.data.addWarp(warp);
                                sender.sendMessage("Successfully created '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
                                switch (visibility) {
                                case PRIVATE:
                                    WarpManager.printPrivatizeMessage(sender, warp);
                                    break;
                                case PUBLIC:
                                    if (MyWarp.permissions.permissionOr(sender, PermissionTypes.CREATE_PRIVATE, PermissionTypes.ADMIN_PRIVATE)) {
                                        sender.sendMessage("If you'd like to privatize it, use:");
                                        sender.sendMessage(ChatColor.GREEN + "/warp private \"" + warp.name + "\" " + warp.getOwner());
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
            this.updateLocation(name, visibility == Visibility.GLOBAL ? null : newOwner, player);
        }
    }

    public void blindAdd(List<Warp> warps) {
        this.blindAdd(warps.toArray(new Warp[0]));
    }

    public void blindAdd(Warp... warps) {
        for (Warp warp : warps) {
            this.list.addWarpObject(warp);
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

    public void changeCreator(String name, String owner, CommandSender sender, String newCreator) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_CHANGE_CREATOR)) {
                if (warp.isCreator(newCreator)) {
                    sender.sendMessage(ChatColor.RED + newCreator + " is already the creator.");
                } else {
                    warp.setCreator(newCreator);
                    this.data.updateCreator(warp);
                    sender.sendMessage("You have changed the creator of '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' to " + ChatColor.GREEN + newCreator + ChatColor.WHITE + ".");
                    Player match = server.getPlayer(newCreator);
                    if (match != null) {
                        match.sendMessage("You're now creator of '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' by " + MinecraftUtil.getName(sender));
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to give '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void give(String name, String owner, CommandSender sender, String giveeName) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (playerCanModifyWarp(sender, warp, Permissions.GIVE)) {
                if (warp.isOwn(giveeName)) {
                    sender.sendMessage(ChatColor.RED + giveeName + " is already the owner.");
                } else {
                    Warp giveeWarp = this.getWarp(name, giveeName, null);
                    if (giveeWarp == null) {
                        String preOwner = warp.getOwner();
                        IdentificationInterface ii = this.data.createIdentification(warp);
                        warp.setOwner(giveeName);
                        this.list.updateOwner(warp, preOwner);
                        this.data.updateOwner(warp, ii);
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
                if (message.equalsIgnoreCase("default")) {
                    warp.setWelcomeMessage(null);
                } else {
                    warp.setWelcomeMessage(message);
                }
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
        Warp warp = this.list.getWarpObject(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.PRIVATE)) {
                warp.setVisibility(Visibility.PRIVATE);
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
        Warp warp = this.list.getWarpObject(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.PUBLIC)) {
                warp.setVisibility(Visibility.PUBLIC);
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

    public void setPrice(String name, String owner, CommandSender sender, int price) {
        Warp warp = this.list.getWarpObject(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            Permissions p;
            if (price < 0) {
                p = Permissions.FREE;
            } else {
                p = Permissions.PRICE;
            }
            if (WarpManager.playerCanModifyWarp(sender, warp, p)) {
                warp.setPrice(price);
                this.data.updatePrice(warp);
                if (price < 0) {
                    sender.sendMessage(ChatColor.AQUA + "Everybody could now warp for free to '" + warp.name + "'.");
                } else {
                    sender.sendMessage(ChatColor.AQUA + "You have set the price for '" + warp.name + "'");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to change the price of '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void globalize(String name, String owner, CommandSender sender) {
        Warp warp = this.list.getWarpObject(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (playerCanModifyWarp(sender, warp, Permissions.GLOBAL)) {
                Warp existing = this.list.getWarpObject(name);
                if (existing == null || existing.getVisibility() != Visibility.GLOBAL) {
                    warp.setVisibility(Visibility.GLOBAL);
                    this.data.updateVisibility(warp);
                    this.list.updateVisibility(warp);
                    sender.sendMessage("You have globalized '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'");
                } else if (existing.equals(warp) && existing.getVisibility() == Visibility.GLOBAL) {
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
        Warp warp = this.list.getWarpObject(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (playerCanModifyWarp(sender, warp, Permissions.INVITE)) {
                if (warp.playerIsInvited(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + inviteeName + " is already invited to this warp.");
                } else if (warp.isOwn(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + inviteeName + " is the creator, of course he's the invited!");
                } else {
                    warp.invite(inviteeName);
                    this.data.updateEditor(warp, inviteeName);
                    sender.sendMessage("You have invited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " to '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
                    if (warp.getVisibility() != Visibility.PRIVATE) {
                        sender.sendMessage(ChatColor.RED + "But '" + warp.name + "' is still public.");
                    }
                    Player match = this.server.getPlayer(inviteeName);
                    if (match != null) {
                        match.sendMessage("You've been invited to warp '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ".");
                        match.sendMessage("Use: " + ChatColor.GREEN + "/warp [to] \"" + warp.name + "\" " + warp.getOwner() + ChatColor.WHITE + " to warp to it.");
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
        Warp warp = this.list.getWarpObject(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.UNINVITE)) {
                if (!warp.playerIsInvited(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + inviteeName + " is not invited to this warp.");
                } else if (warp.isOwn(inviteeName)) {
                    sender.sendMessage(ChatColor.RED + "You can't uninvite yourself. You're the creator!");
                } else {
                    EditorPermissions permissions = warp.getPlayerEditorPermissions(inviteeName, false);
                    if (permissions != null && permissions.remove(Permissions.WARP)) {
                        this.data.updateEditor(warp, inviteeName);
                        sender.sendMessage("You have uninvited " + ChatColor.GREEN + inviteeName + ChatColor.WHITE + " from '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
                        if (warp.getVisibility() != Visibility.PRIVATE) {
                            sender.sendMessage(ChatColor.RED + "But '" + warp.name + "' is still public.");
                        }
                        Player match = this.server.getPlayer(inviteeName);
                        if (match != null) {
                            match.sendMessage("You've been uninvited to warp '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "' by " + ChatColor.GREEN + MinecraftUtil.getName(sender) + ChatColor.WHITE + ". Sorry.");
                        }
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
                    owner = warp.getOwner();
                }
                if (warp.getVisibility() == Visibility.GLOBAL && (this.getWarp(newName, null, null) != null)) {
                    sender.sendMessage(ChatColor.RED + "A global warp with this name already exists!");
                } else if (this.getWarp(newName, owner, null) != null) {
                    sender.sendMessage(ChatColor.RED + "You already have a warp with this name.");
                } else {
                    IdentificationInterface ii = this.data.createIdentification(warp);
                    this.list.deleteWarp(warp);
                    warp.rename(newName);
                    this.list.addWarpObject(warp);
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

    public void addEditor(String name, String owner, CommandSender sender, String editor, String permissions, EditorPermissions.Type type) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.ADD_EDITOR)) {
                warp.addEditor(editor, permissions, type);
                this.data.updateEditor(warp, editor);
                sender.sendMessage("You have added " + ChatColor.GREEN + editor + ChatColor.WHITE + " to '" + warp.name + ChatColor.WHITE + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to add an editor from '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }

    public void removeEditor(String name, String owner, CommandSender sender, String editor, EditorPermissions.Type type) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.REMOVE_EDITOR)) {
                warp.removeEditor(editor, type);
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
        return this.list.getWarpObject(name, owner, playerName);
    }

    public List<Warp> getWarps() {
        return this.list.getWarpObjects();
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

    public void blindAdd(Warp warp) {
        this.list.addWarpObject(warp);
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

    public void warpTo(String name, String owner, CommandSender warper, Warpable warped, boolean viaSign) {
        this.warpTo(name, owner, warper, warped, viaSign, this.properties.isForceToUsed());
    }

    public void warpTo(String name, String owner, CommandSender warper, Warpable warped, boolean viaSign, boolean forced) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(warper));
        if (warp != null) {
            if (warp.getLocationWrapper().isValid()) {
                if (warped.equals(warper) || MyWarp.permissions.permission(warper, PermissionTypes.ADMIN_WARP_OTHERS)) {
                    if (warp.playerCanWarp(warper, viaSign)) {
                        if (!forced && !warp.isSave()) {
                            warper.sendMessage(ChatColor.RED + "The selected warp is maybe not save!");
                            warper.sendMessage(ChatColor.RED + "To force warping use /warp force-to <warp> [owner].");
                        } else {
                            double price = MyWarp.permissions.getDouble(warper, Groups.PRICES_TO_GROUP.get(warp.getVisibility()));

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
                                warper.sendMessage(ChatColor.RED + "You need to wait for the cooldown of " + this.coolDown.cooldownTime(warp, warper) + " s");
                            }
                        }
                    } else {
                        warped.sendMessage(ChatColor.RED + "You do not have permission to warp to '" + warp.name + "'.");
                    }
                } else {
                    warper.sendMessage(ChatColor.RED + "You do not have permission to warp others.");
                }
            } else {
                warper.sendMessage(ChatColor.RED + "The location of the warp is invalid.");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, warped);
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
            if ((creatorTester.test(w.getCreator().toLowerCase())) &&
                (ownerTester.test(w.getOwner())) &&
                (worldTester.test(w.getLocationWrapper().getWorld().toLowerCase())) &&
                (visibilityTester.test(w.getVisibility())) &&
                (w.listWarp(sender))) {
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

    public boolean isNameAvailable(Warp warp) {
        return this.isNameAvailable(warp.name, warp.getOwner());
    }

    public boolean isNameAvailable(String name, String owner) {
        return this.list.getWarpObject(name, owner, null) == null;
    }

    public static void sendMissingWarp(String name, String owner, CommandSender sender) {
        if (owner == null || owner.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Global warp '" + name + "' doesn't exist.");
        } else {
            sender.sendMessage(ChatColor.RED + "Player '" + owner + "' doesn't own a warp named '" + name + "'.");
        }
    }

    private static void printPrivatizeMessage(CommandSender sender, Warp warp) {
        sender.sendMessage(ChatColor.WHITE + "You have privatized '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'");
        sender.sendMessage("If you'd like to invite others to it, use:");
        sender.sendMessage(ChatColor.GREEN + "/warp invite \"" + warp.name + "\" " + warp.getOwner() + " <player>");
    }

    private static boolean playerCanModifyWarp(CommandSender sender, Warp warp, Permissions permission) {
        return warp.canModify(sender, permission);
    }

    public void setListed(String name, String owner, CommandSender sender, Boolean listed) {
        Warp warp = this.getWarp(name, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            if (WarpManager.playerCanModifyWarp(sender, warp, Permissions.LIST)) {
                warp.setListed(listed);
                this.data.updateVisibility(warp);
                sender.sendMessage("You have " + (listed ? "listed" : "unlisted") + " '" + ChatColor.GREEN + warp.name + ChatColor.WHITE + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to change the listed status from '" + warp.name + "'");
            }
        } else {
            WarpManager.sendMissingWarp(name, owner, sender);
        }
    }
}
