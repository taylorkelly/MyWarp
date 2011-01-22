package me.taylorkelly.mywarp;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class MWPlayerListener extends PlayerListener {
    private WarpList warpList;
    
    public MWPlayerListener(WarpList warpList) {
        this.warpList = warpList;
    }

    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if(warpList.waitingForWelcome(player)) {
            warpList.setWelcomeMessage(player, event.getMessage());
            warpList.notWaiting(player);
            event.setCancelled(true);
        }
    }
}
