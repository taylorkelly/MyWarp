package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignWarp {
    
    /**
     * Precondition: Only call if isSignWarp() returned true
     */
    public static void warpSign(Sign sign, WarpList list, Player player) {
        ArrayList<String> lines = new ArrayList<String>();
        for(int i = 0; i < 4; i++){
            if(!sign.getLine(i).trim().equals("")) {
                lines.add(sign.getLine(i).trim());
            }
        }
        String name = lines.get(1);
        list.warpTo(name, player);
    }

    public static boolean isSignWarp(Sign sign) {
        ArrayList<String> lines = new ArrayList<String>();
        for(int i = 0; i < 4; i++){
            if(!sign.getLine(i).trim().equals("")) {
                lines.add(sign.getLine(i).trim());
            }
        }
        if(lines.size() == 2 && lines.get(0).equalsIgnoreCase("MyWarp")) {
            return true;
        } else {
            return false;
        }
    }
}
