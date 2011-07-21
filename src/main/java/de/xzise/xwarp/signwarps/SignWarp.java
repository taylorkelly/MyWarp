package de.xzise.xwarp.signwarps;

import java.util.ArrayList;
import java.util.List;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.xzise.xwarp.WarpDestination;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.warpable.WarperFactory;

public class SignWarp {

    private static final SignWarpDefinition[] SIGN_WARP_DEFINITIONS = new SignWarpDefinition[] { new XWarpSign(), new SingleLineSign(), new MyWarpSign(), };

    private Sign sign;

    public SignWarp(Sign sign) {
        this.sign = sign;
    }

    public WarpDestination getDestination(Player player) {
        return SignWarp.getDestination(SignWarp.getFilledLines(this.sign), player);
    }

    public boolean warp(WarpManager list, Player player) {
        WarpDestination destination = this.getDestination(player);

        if (destination != null) {
            list.warpTo(destination.name, destination.owner, player, WarperFactory.getWarpable(player), true);
            return true;
        } else {
            return false;
        }
    }
    
    private static String replaceName(String text, String value, String... placeHolders) {
        for (String placeHolder : placeHolders) {
            text = text.replace("{" + placeHolder + "}", value);
        }
        return text;
    }

    public static WarpDestination getDestination(String[] lines, Player player) {
        for (SignWarpDefinition destinationElement : SIGN_WARP_DEFINITIONS) {
            WarpDestination destination = destinationElement.getDestination(lines);
            if (destination != null) {
                String name = destination.name;
                name = replaceName(name, player.getName(), "Name", "N");
                name = replaceName(name, player.getDisplayName(), "DName", "DN");
                String[] groups = MyWarp.permissions.getGroup(player.getWorld().getName(), player.getName());
                if (groups.length > 0) {
                    name = replaceName(name, groups[0], "Group", "G");
                }
                ItemStack stack = player.getItemInHand();
                name = replaceName(name, String.valueOf(stack == null ? Material.AIR.getId() : stack.getTypeId()), "Hand", "M");
                
                return new WarpDestination(name, destination.owner);
            }
        }

        return null;
    }

    private static String[] getFilledLines(Sign sign) {
        return SignWarp.getFilledLines(sign.getLines());
    }

    public static String[] getFilledLines(String[] lines) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < lines.length; i++) {
            if (!lines[i].trim().isEmpty()) {
                result.add(lines[i]);
            }
        }
        return result.toArray(new String[0]);
    }
}
