package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.EconomyWrapper;
import de.xzise.xwarp.Permissions;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.lister.GenericLister;

public class InfoCommand extends WarpCommand {

    private final EconomyWrapper wrapper;

    public InfoCommand(WarpManager list, Server server, EconomyWrapper wrapper) {
        super(list, server, "", "info");
        this.wrapper = wrapper;
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String owner, String[] parameters) {
        Warp warp = this.list.getWarp(warpName, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            sender.sendMessage("Warp info: " + ChatColor.GREEN + warp.name);
            String world;
            if (warp.isValid()) {
                world = warp.getLocation().getWorld().getName();
            } else {
                sender.sendMessage(ChatColor.RED + "The location is invalid!");
                world = this.server.getWorlds().get(0).getName();
            }

            sender.sendMessage("Creator: " + getPlayerLine(warp.getCreator(), world));
            sender.sendMessage("Owner: " + getPlayerLine(warp.getOwner(), world));
            String visibility = "";
            switch (warp.visibility) {
            case GLOBAL:
                visibility = "Global";
                break;
            case PUBLIC:
                visibility = "Public";
                break;
            case PRIVATE:
                visibility = "Private";
                break;
            }
            if (sender instanceof Player) {
                visibility = GenericLister.getColor(warp, (Player) sender) + visibility;
            }
            sender.sendMessage("Visibility: " + visibility);
            if (this.wrapper.isActive()) {
                sender.sendMessage("Price: " + ChatColor.GREEN + this.wrapper.format(warp.getPrice()));
            } else {
                sender.sendMessage("Price: " + ChatColor.GREEN + warp.getPrice() + ChatColor.RED + " (INACTIVE)");
            }

            String[] editors = warp.getEditors();
            String editor = "";
            String invitees = "";
            if (editors.length == 0) {
                editor = "None";
            } else {
                for (int i = 0; i < editors.length; i++) {
                    String string = editors[i];
                    Permissions[] pms = warp.getEditorPermissions(string).getByValue(true);
                    if (pms.length > 0) {
                        editor += ChatColor.GREEN + string + " ";
                        char[] editorPermissions = new char[pms.length];
                        for (int j = 0; j < pms.length; j++) {
                            editorPermissions[j] = pms[j].value;
                            if (pms[j] == Permissions.WARP) {
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

            if (warp.isValid()) {
                Location location = warp.getLocation();
                sender.sendMessage("Location: World = " + ChatColor.GREEN + location.getWorld().getName() + ChatColor.WHITE + ", x = " + ChatColor.GREEN + location.getBlockX() + ChatColor.WHITE + ", y = " + ChatColor.GREEN + location.getBlockY() + ChatColor.WHITE + ", z = " + ChatColor.GREEN + location.getBlockZ());
            }
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
