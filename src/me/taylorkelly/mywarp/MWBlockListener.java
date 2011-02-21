package me.taylorkelly.mywarp;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.SignChangeEvent;

import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class MWBlockListener extends BlockListener {
	
    private WarpManager list;
    
    public MWBlockListener(WarpManager list) {
        this.list = list;
    }
    
    public void onBlockRightClick(BlockRightClickEvent event) {
        Block block = event.getBlock();
        if(block.getState() instanceof Sign && MyWarp.permissions.permissionOr(event.getPlayer(), PermissionTypes.SIGN_WARP_GLOBAL, PermissionTypes.SIGN_WARP_INVITED, PermissionTypes.SIGN_WARP_OTHER, PermissionTypes.SIGN_WARP_OWN)) {
        	SignWarp signWarp = new SignWarp((Sign) block.getState());
        	signWarp.warp(this.list, event.getPlayer());
        }
    }
    
    public void onBlockCanBuild(BlockCanBuildEvent event) {
    	Block block = event.getBlock();
        if(block.getState() instanceof Sign /*&& MyWarp.permissions.permission(event.getPlayer(), PermissionTypes.SIGN_WARP)*/) {
        	SignWarp signWarp = new SignWarp((Sign) block.getState());
        	if (signWarp.getDestination() != null) {
        		MyWarp.logger.info("cb");
        		event.setBuildable(false);
        	}
        	MyWarp.logger.info("cb pre");
        } else {
        	MyWarp.logger.info("cb !sign");
        }
    }
    
    public void onSignChange(SignChangeEvent event) {
    	Block block = event.getBlock();
    	if (block.getState() instanceof Sign && MyWarp.permissions.permissionOr(event.getPlayer(), PermissionTypes.SIGN_WARP_GLOBAL, PermissionTypes.SIGN_WARP_INVITED, PermissionTypes.SIGN_WARP_OTHER, PermissionTypes.SIGN_WARP_OWN)) {    		
    		WarpDestination destination = SignWarp.getDestination(SignWarp.getFilledLines(event.getLines()));
    		if (destination != null) {
    			event.getPlayer().sendMessage("Warp sign found.");
    			Warp warp = this.list.getWarp(destination.name, destination.creator, null);
    			if (warp == null) {
	    			String creator = "";
	    			if (!destination.creator.isEmpty()) {
	    				creator = " by " + ChatColor.GREEN + destination.creator;
	    			} else {
	    				creator = " (global)";
	    			}
	    			event.getPlayer().sendMessage(ChatColor.GREEN + destination.name + ChatColor.WHITE + creator);
    				event.getPlayer().sendMessage(ChatColor.RED + "This warp doesn't exists!");
    			} else {
    				event.getPlayer().sendMessage(ChatColor.GREEN + warp.name + ChatColor.WHITE + " by " + ChatColor.GREEN + warp.creator);
    			}
    		}
    	}
    }
}
