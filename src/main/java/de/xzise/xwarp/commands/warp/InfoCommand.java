package de.xzise.xwarp.commands.warp;

import java.util.Collection;


import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.metainterfaces.FixedLocation;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.DefaultWarpObject.EditorPermissionEntry;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.commands.wpa.InfoCommand.EditorLines;
import de.xzise.xwarp.editors.WarpPermissions;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.timer.CoolDown;
import de.xzise.xwarp.timer.WarmUp;
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
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        if (!XWarp.permissions.permission(sender, PermissionTypes.CMD_INFO)) {
            sender.sendMessage(ChatColor.RED + "You have no permission to gather information to warps.");
            return true;
        }

        sender.sendMessage("Warp info: " + ChatColor.GREEN + warp.getName());
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
            basePrice = XWarp.permissions.getDouble(sender, PricePermissions.WARP_PRICES_TO_GLOBAL);
            break;
        case PUBLIC:
            visibility = "Public";
            basePrice = XWarp.permissions.getDouble(sender, PricePermissions.WARP_PRICES_TO_PUBLIC);
            break;
        case PRIVATE:
            visibility = "Private";
            basePrice = XWarp.permissions.getDouble(sender, PricePermissions.WARP_PRICES_TO_PRIVATE);
            break;
        }
        if (sender instanceof Player) {
            visibility = GenericLister.getColor(warp, (Player) sender) + visibility;
        }
        sender.sendMessage("Visibility: " + visibility + " (" + (warp.isListed() ? "Listed" : "Unlisted") + ")");
        if (this.wrapper.isActive()) {
            sender.sendMessage("Price: " + ChatColor.GREEN + this.getPrice(warp.getPrice(), basePrice));
        } else if (warp.getPrice() != 0) {
            sender.sendMessage("Price: " + ChatColor.GREEN + this.getPrice(warp.getPrice(), basePrice) + ChatColor.RED + " (Inactive)");
        }

        sender.sendMessage("Cooldown: " + CoolDown.getCooldownTime(warp, sender) + " sec   Warmup: " + WarmUp.getWarmupTime(warp, sender) + " sec");

        Collection<EditorPermissionEntry<WarpPermissions>> allEditorPermissions = warp.getEditorPermissionsList();
        EditorLines lines = de.xzise.xwarp.commands.wpa.InfoCommand.getEditorLines(allEditorPermissions, WarpPermissions.WARP);
        sender.sendMessage("Invitees: " + (lines.invitees.isEmpty() ? "None" : lines.invitees));
        sender.sendMessage("Editors: " + lines.editors);

        FixedLocation location = warp.getLocation();
        sender.sendMessage("Location: World = " + ChatColor.GREEN + world + ChatColor.WHITE + ", x = " + ChatColor.GREEN + location.getBlockX() + ChatColor.WHITE + ", y = " + ChatColor.GREEN + location.getBlockY() + ChatColor.WHITE + ", z = " + ChatColor.GREEN + location.getBlockZ());

        return true;
    }

    public static String getPlayerLine(String player, String world) {
        if (MinecraftUtil.isSet(player)) {
            String[] groups = XWarp.permissions.getGroup(world, player);

            String groupText = "";
            if (groups != null && groups.length > 0) {
                groupText = ChatColor.WHITE + " (Group: " + ChatColor.GREEN + groups[0] + ChatColor.WHITE + ")";
            }

            return ChatColor.GREEN + player + groupText;
        } else {
            return "Nobody";
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Show the information about the warp." };
    }

    @Override
    public String getSmallHelpText() {
        return "Show warp's information";
    }

}
