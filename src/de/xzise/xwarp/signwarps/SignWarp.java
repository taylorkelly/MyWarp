package de.xzise.xwarp.signwarps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import de.xzise.xwarp.WarpDestination;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.warpable.WarperFactory;

public class SignWarp {

    private static final SignWarpDefinition[] SIGN_WARP_DEFINITIONS = new SignWarpDefinition[] { new XWarpSign(), new SingleLineSign(), new MyWarpSign(), };

    private Sign sign;

    public SignWarp(Sign sign) {
        this.sign = sign;
    }

    public WarpDestination getDestination() {
        return SignWarp.getDestination(SignWarp.getFilledLines(this.sign));
    }

    public boolean warp(WarpManager list, Player player) {
        WarpDestination destination = this.getDestination();

        if (destination != null) {
            list.warpTo(destination.name, destination.creator, player, WarperFactory.getWarpable(player), true);
            return true;
        } else {
            return false;
        }
    }

    public static WarpDestination getDestination(String[] lines) {
        for (SignWarpDefinition destinationElement : SIGN_WARP_DEFINITIONS) {
            WarpDestination destination = destinationElement.getDestination(lines);
            if (destination != null) {
                return destination;
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
