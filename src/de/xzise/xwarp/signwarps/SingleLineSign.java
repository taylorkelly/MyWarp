package de.xzise.xwarp.signwarps;

import me.taylorkelly.mywarp.WarpDestination;

public class SingleLineSign implements SignWarpDefinition {

	@Override
	public WarpDestination getDestination(String[] lines) {
		// Single Line
		// line < 0 → No line, line ≥ lines.length → More than one line
		// All other cases: One line & valid!
		int line = -1;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].matches("(W|w)arp:?\\s+.+")) {
				if (line < 0) {
					line = i;
				} else {
					// Invalid
					line = lines.length;
					break;
				}
			}
		}
		
		// If only one valid line found
		if (line >= 0 && line < lines.length) {
			// Extract name:
			String command = lines[line];
			String name = "";
			boolean spaceReach = false;
			for (int i = 0; i < command.length(); i++) {
				if (command.charAt(i) == ' ') {
					spaceReach = true;
				} else if (spaceReach) {
					name = command.substring(i);
					break;
				}
			}
			if (name.isEmpty()) {
				throw new IllegalArgumentException("Empty warp name");
			}
			return new WarpDestination(name, "");
		} else {
			return null;
		}
	}

}
