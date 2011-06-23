package de.xzise.xwarp.listeners;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.WarpDestination;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.signwarps.SignWarp;
import de.xzise.xwarp.wrappers.permission.Groups;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class XWBlockListener extends BlockListener {

    private WarpManager list;

    public XWBlockListener(WarpManager list) {
        this.list = list;
    }

    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Sign && !event.isCancelled() && event.getPlayer() != null) {
            WarpDestination destination = SignWarp.getDestination(SignWarp.getFilledLines(event.getLines()), event.getPlayer());
            if (destination != null) {
                Warp warp = this.list.getWarp(destination.name, destination.owner, null);
                PermissionTypes type = null;
                if (warp == null) {
                    type = PermissionTypes.SIGN_CREATE_UNKNOWN;
                } else {
                    type = Groups.SIGN_CREATE_GROUP.get(warp.visibility);
                }
                if (MyWarp.permissions.permission(event.getPlayer(), type)) {
                    String line = "Warp sign found: ";
                    if (warp == null) {
                        String creator = "";
                        if (MinecraftUtil.isSet(destination.owner)) {
                            creator = " by " + ChatColor.GREEN + destination.owner;
                        } else {
                            creator = " (global)";
                        }
                        line += ChatColor.GREEN + destination.name + ChatColor.WHITE + creator;
                    } else {
                        line += ChatColor.GREEN + warp.name + ChatColor.WHITE + " by " + ChatColor.GREEN + warp.getOwner();
                    }

                    event.getPlayer().sendMessage(line);
                    if (warp == null) {
                        event.getPlayer().sendMessage(ChatColor.RED + "This warp doesn't exists!");
                    }
                } else {
                    // Return sign
                    event.getPlayer().sendMessage(ChatColor.RED + "You have no permission to create a warp sign.");
                    
                    event.setCancelled(true);
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SIGN, 1));
                    block.setTypeId(0);
                }
            }
        }
    }
}
