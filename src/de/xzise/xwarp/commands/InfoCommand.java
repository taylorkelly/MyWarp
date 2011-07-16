package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.metainterfaces.FixedLocation;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.editors.WarpPermissions;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;
import de.xzise.xwarp.wrappers.permission.PricePermissions;

public class InfoCommand extends WarpCommand {

    private final EconomyHandler wrapper;

    public InfoCommand(WarpManager list, Server server, EconomyHandler wrapper) {
        super(list, server, "", "info");
        this.wrapper = wrapper;
    }
    
    private String getPrice(double price, double base) {
        if (price < 0 || (price == 0 && base == 0)) {
            return "Gratis";
        } else if (price == 0 && base != 0) {
            return "Only permissions price (" + this.wrapper.format(base) + ")";
        } else {
            return this.wrapper.format(price) + " base price: " + this.wrapper.format(base);
        }
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String owner, String[] parameters) {
        if (!MyWarp.permissions.permission(sender, PermissionTypes.CMD_INFO)) {
            sender.sendMessage(ChatColor.RED + "You have no permission to gather information to warps.");
            return true;
        }
        
        Warp warp = this.list.getWarp(warpName, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            sender.sendMessage("Warp info: " + ChatColor.GREEN + warp.name);
            String world = warp.getLocationWrapper().getWorld();
            if (!warp.getLocationWrapper().isValid()) {
                sender.sendMessage(ChatColor.RED + "The location is invalid!");
            } else if (!warp.isSave()) {
                sender.sendMessage(ChatColor.RED + "The location is not save!");
            }

            sender.sendMessage("Creator: " + getPlayerLine(warp.getCreator(), world));
            sender.sendMessage("Owner: " + getPlayerLine(warp.getOwner(), world));
            String visibility = "";
            double basePrice = 0;
            switch (warp.getVisibility()) {
            case GLOBAL:
                visibility = "Global";
                basePrice = MyWarp.permissions.getDouble(sender, PricePermissions.WARP_PRICES_TO_GLOBAL);
                break;
            case PUBLIC:
                visibility = "Public";
                basePrice = MyWarp.permissions.getDouble(sender, PricePermissions.WARP_PRICES_TO_PUBLIC);
                break;
            case PRIVATE:
                visibility = "Private";
                basePrice = MyWarp.permissions.getDouble(sender, PricePermissions.WARP_PRICES_TO_PRIVATE);
                break;
            }
            if (sender instanceof Player) {
                visibility = GenericLister.getColor(warp, (Player) sender) + visibility;
            }
            sender.sendMessage("Visibility: " + visibility);
            if (this.wrapper.isActive()) {
                sender.sendMessage("Price: " + ChatColor.GREEN + this.getPrice(warp.getPrice(), basePrice));
            } else if (warp.getPrice() != 0) {
                sender.sendMessage("Price: " + ChatColor.GREEN + this.getPrice(warp.getPrice(), basePrice) + ChatColor.RED + " (Inactive)");
            }

            String[] editors = warp.getEditors();
            String editor = "";
            String invitees = "";
            if (editors.length == 0) {
                editor = "None";
            } else {
                for (int i = 0; i < editors.length; i++) {
                    String string = editors[i];
                    WarpPermissions[] pms = warp.getPlayerEditorPermissions(string).getByValue(true);
                    if (pms.length > 0) {
                        editor += ChatColor.GREEN + string + " ";
                        char[] editorPermissions = new char[pms.length];
                        for (int j = 0; j < pms.length; j++) {
                            editorPermissions[j] = pms[j].value;
                            if (pms[j] == WarpPermissions.WARP) {
                                if (!invitees.isEmpty()) {
                                    invitees += ", ";
                                }
                                invitees += string;
                            }
                        }
                        editor += new String(editorPermissions);
                    }
                    if (i < editors.length - 1) {
                        editor += ChatColor.WHITE + ", ";
                    }
                }
            }
            sender.sendMessage("Invitees: " + (invitees.isEmpty() ? "None" : invitees));
            sender.sendMessage("Editors: " + editor);

            FixedLocation location = warp.getLocation();
            sender.sendMessage("Location: World = " + ChatColor.GREEN + world + ChatColor.WHITE + ", x = " + ChatColor.GREEN + location.getBlockX() + ChatColor.WHITE + ", y = " + ChatColor.GREEN + location.getBlockY() + ChatColor.WHITE + ", z = " + ChatColor.GREEN + location.getBlockZ());
        } else {
            WarpManager.sendMissingWarp(warpName, owner, sender);
        }

        return true;
    }

    private static String getPlayerLine(String player, String world) {
        if (MinecraftUtil.isSet(player)) {
            String group = MyWarp.permissions.getGroup(world, player);

            String groupText = "";
            if (group != null) {
                groupText = ChatColor.WHITE + " (Group: " + ChatColor.GREEN + group + ChatColor.WHITE + ")";
            }

            return ChatColor.GREEN + player + groupText;
        } else {
            return "Nobody";
        }
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Show the information about the warp." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Show warp's information";
    }

}
