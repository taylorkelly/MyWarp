package me.taylorkelly.mywarp;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRightClickEvent;

public class MWBlockListener extends BlockListener {
    private WarpList list;
    
    
    public MWBlockListener(WarpList list) {
        this.list = list;
    }
    
    public void onBlockRightClick(BlockRightClickEvent event) {
        Block block = event.getBlock();
        if(block.getState() instanceof Sign && SignWarp.isSignWarp((Sign) block.getState())) {
            SignWarp.warpSign((Sign) block.getState(), list, event.getPlayer());
        }
    }
}
