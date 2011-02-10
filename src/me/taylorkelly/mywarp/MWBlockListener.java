package me.taylorkelly.mywarp;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRightClickEvent;

import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class MWBlockListener extends BlockListener {
	
    private WarpList list;
    
    public MWBlockListener(WarpList list) {
        this.list = list;
    }
    
    public void onBlockRightClick(BlockRightClickEvent event) {
        Block block = event.getBlock();
        if(block.getState() instanceof Sign && MyWarp.permissions.permission(event.getPlayer(), PermissionTypes.SIGN_WARP)) {
        	SignWarp signWarp = new SignWarp((Sign) block.getState());
        	signWarp.warp(this.list, event.getPlayer());
        }
    }
}
