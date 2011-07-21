package de.xzise.xwarp.signwarps;

import de.xzise.xwarp.WarpDestination;

public class XWarpSign implements SignWarpDefinition {

    @Override
    public WarpDestination getDestination(String[] lines) {
        // xWarp
        if ((lines.length == 2 || lines.length == 3) && ((lines[0].equalsIgnoreCase("xWarp") || lines[0].matches("x?(W|w)arp:?")))) {
            String creator = "";
            if (lines.length == 3) {
                creator = lines[2];
            }
            return new WarpDestination(lines[1], creator);
        } else {
            return null;
        }
    }

}
