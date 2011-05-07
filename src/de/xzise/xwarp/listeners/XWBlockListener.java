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
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;
import de.xzise.xwarp.signwarps.SignWarp;

public class XWBlockListener extends BlockListener {

    private WarpManager list;

    public XWBlockListener(WarpManager list) {
        this.list = list;
    }

    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Sign && !event.isCancelled()) {
            WarpDestination destination = SignWarp.getDestination(SignWarp.getFilledLines(event.getLines()));
            if (destination != null) {
                if (MyWarp.permissions.permissionOr(event.getPlayer(), PermissionTypes.CREATE_SIGN_PRIVATE, PermissionTypes.CREATE_SIGN_PUBLIC, PermissionTypes.CREATE_SIGN_GLOBAL)) {
                    String line = "Warp sign found: ";
                    Warp warp = this.list.getWarp(destination.name, destination.creator, null);
                    if (warp == null) {
                        String creator = "";
                        if (MinecraftUtil.isSet(destination.creator)) {
                            creator = " by " + ChatColor.GREEN + destination.creator;
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
