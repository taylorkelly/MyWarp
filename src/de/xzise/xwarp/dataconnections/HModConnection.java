package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import de.xzise.MinecraftUtil;
import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.EditorPermissions;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;

public class HModConnection implements DataConnection {

    private File file;
    private Server server;
    private static final char SEPARATOR = ':';
    // Name, X, Y, Z, Yaw, Pitch
    private static final int GEN_1_LENGTH = 6;
    // Gen1 + World, Owner, Visibility, Message
    /** Minimum length of second generation map. */
    private static final int GEN_2_LENGTH = GEN_1_LENGTH + 4;
    // Gen2 + Creator
    private static final int GEN_3_LENGTH = GEN_2_LENGTH + 1;
    // Gen3 + Price
    private static final int GEN_4_LENGTH = GEN_3_LENGTH + 1;

    public HModConnection(Server server) {
        this.server = server;
    }

    @Override
    public boolean load(File file) {
        this.file = file;
        if (!this.file.exists()) {
            try {
                return this.file.createNewFile();
            } catch (IOException e) {
                MyWarp.logger.severe("Unable to load the hmod connection", e);
                return false;
            }
        } else {
            return this.file.canWrite();
        }
    }

    @Override
    public void free() {
    }

    @Override
    public List<Warp> getWarps() {
        // List<HModWarpElement> elements = this.g
        List<Warp> result = new ArrayList<Warp>(this.getWarps(null));
        return result;
    }

    public List<Warp> getWarps(String owner) {
        List<Warp> result = new ArrayList<Warp>();
        Scanner scanner;
        try {
            List<String> lines = new ArrayList<String>();
            Integer version = null;
            int lineNum = 0;
            scanner = new Scanner(this.file);
            try {
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    lineNum++;
                    if (line.length() > 0 && !line.startsWith("#")) {
                        if (line.matches("!version\\s*\\d+\\s*")) {
                            int tempVersion = 0;
                            try {
                                tempVersion = Integer.parseInt(line.substring(8).trim());
                            } catch (NumberFormatException nfe) {
                                MyWarp.logger.severe("Version tag is invalid number.");
                            }
                            if (version == null) {
                                version = tempVersion;
                            } else if (version != tempVersion) {
                                MyWarp.logger.severe("Different version tags found (line: " + lineNum + "), choose first found: " + version);
                            }
                        } else {
                            lines.add(line);
                        }
                    }
                }
            } finally {
                scanner.close();
            }

            switch (version) {
            case 0:
                return getWarpsVersion0(lines, this.server.getWorlds().get(0));
            case 1:
                return getWarpsVersion1(lines, this.server);
            case 2:
                return getWarpsVersion2(lines, this.server);
            case 3:
                return getWarpsVersion3(lines, this.server);
            default:
                if (version == null) {
                    return getWarpsVersion0(lines, this.server.getWorlds().get(0));
                }
                MyWarp.logger.severe("Unknown version tag: " + version);
                break;
            }
        } catch (FileNotFoundException e) {
            MyWarp.logger.info("hmod file not found!");
        }
        return result;
    }

    private static List<Warp> getWarpsVersion0(List<String> lines, World def) {
        List<Warp> warps = new ArrayList<Warp>();
        for (String line : lines) {
            String[] segments = MinecraftUtil.parseLine(line, SEPARATOR);
            if (segments.length == GEN_1_LENGTH) {
                Warp warp = null;
                boolean valid = true;
                try {
                    String name = segments[0];

                    double x = Double.parseDouble(segments[1]);
                    double y = Double.parseDouble(segments[2]);
                    double z = Double.parseDouble(segments[3]);
                    double yaw = Double.parseDouble(segments[4]);
                    double pitch = Double.parseDouble(segments[5]);

                    yaw = (yaw < 0) ? (360 + (yaw % 360)) : (yaw % 360);

                    LocationWrapper location = new LocationWrapper(new Location(def, x, y, z, (float) yaw, (float) pitch));
                    warp = new Warp(name, "", "", location);
                    warp.visibility = Visibility.GLOBAL;
                } catch (NumberFormatException nfe) {
                    valid = false;
                    MyWarp.logger.warning("Unable to parse a location value (" + nfe.getMessage() + ")");
                } catch (Exception e) {
                    MyWarp.logger.severe("Catched an unhandled exception", e);
                    valid = false;
                }
                if (valid && warp != null) {
                    warps.add(warp);
                }
            } else {
                MyWarp.logger.warning("Invalid warp line found");
            }
        }
        return warps;
    }

    private static List<Warp> getWarpsVersion1(List<String> lines, Server server) {
        List<Warp> warps = new ArrayList<Warp>();
        for (String line : lines) {
            String[] segments = MinecraftUtil.parseLine(line, SEPARATOR);
            if (segments.length >= GEN_2_LENGTH && segments.length % 2 == 0) {
                Warp warp = null;
                boolean valid = true;
                try {
                    String name = segments[0];

                    double x = Double.parseDouble(segments[1]);
                    double y = Double.parseDouble(segments[2]);
                    double z = Double.parseDouble(segments[3]);
                    float yaw = Float.parseFloat(segments[4]);
                    float pitch = Float.parseFloat(segments[5]);

                    yaw = (yaw < 0) ? (360 + (yaw % 360)) : (yaw % 360);

                    World world = server.getWorld(segments[6]);
                    String warpOwner = segments[7];
                    FixedLocation location = new FixedLocation(world, x, y, z, yaw, pitch);
                    LocationWrapper wrapper = new LocationWrapper(location, segments[6]);
                    warp = new Warp(name, warpOwner, warpOwner, wrapper);
                    warp.setMessage(segments[9]);
                    for (int i = GEN_2_LENGTH; i < segments.length; i += 2) {
                        warp.addEditor(segments[i], segments[i + 1]);
                    }

                    Visibility v = Visibility.parseLevel(Integer.parseInt(segments[8]));
                    if (v != null) {
                        warp.visibility = v;
                    } else {
                        MyWarp.logger.warning("Illegal visibilty found (" + warp.name + " by " + warp.getOwner() + ")");
                        valid = false;
                    }
                } catch (NumberFormatException nfe) {
                    valid = false;
                    MyWarp.logger.warning("Unable to parse a location value (" + nfe.getMessage() + ")");
                } catch (Exception e) {
                    MyWarp.logger.severe("Catched an unhandled exception", e);
                    valid = false;
                }
                if (valid && warp != null) {
                    warps.add(warp);
                }
            } else {
                MyWarp.logger.warning("Invalid warp line found");
            }
        }
        return warps;
    }

    private static List<Warp> getWarpsVersion2(List<String> lines, Server server) {
        List<Warp> warps = new ArrayList<Warp>();
        for (String line : lines) {
            String[] segments = MinecraftUtil.parseLine(line, SEPARATOR);
            if (segments.length >= GEN_3_LENGTH && segments.length % 2 == 1) {
                Warp warp = null;
                boolean valid = true;
                try {
                    String name = segments[0];

                    double x = Double.parseDouble(segments[1]);
                    double y = Double.parseDouble(segments[2]);
                    double z = Double.parseDouble(segments[3]);
                    float yaw = Float.parseFloat(segments[4]);
                    float pitch = Float.parseFloat(segments[5]);

                    yaw = (yaw < 0) ? (360 + (yaw % 360)) : (yaw % 360);

                    World world = server.getWorld(segments[6]);
                    String warpOwner = segments[7];
                    FixedLocation location = new FixedLocation(world, x, y, z, yaw, pitch);
                    LocationWrapper wrapper = new LocationWrapper(location, segments[6]);
                    warp = new Warp(name, segments[segments.length - 1], warpOwner, wrapper);
                    warp.setMessage(segments[9]);
                    for (int i = GEN_2_LENGTH; i < segments.length - 1; i += 2) {
                        warp.addEditor(segments[i], segments[i + 1]);
                    }

                    Visibility v = Visibility.parseLevel(Integer.parseInt(segments[8]));
                    if (v != null) {
                        warp.visibility = v;
                    } else {
                        MyWarp.logger.warning("Illegal visibilty found (" + warp.name + " by " + warp.getOwner() + ")");
                        valid = false;
                    }
                } catch (NumberFormatException nfe) {
                    valid = false;
                    MyWarp.logger.warning("Unable to parse a location value (" + nfe.getMessage() + ")");
                } catch (Exception e) {
                    MyWarp.logger.severe("Catched an unhandled exception", e);
                    valid = false;
                }
                if (valid && warp != null) {
                    warps.add(warp);
                }
            } else {
                MyWarp.logger.warning("Invalid warp line found");
            }
        }
        return warps;
    }

    private static List<Warp> getWarpsVersion3(List<String> lines, Server server) {
        List<Warp> warps = new ArrayList<Warp>();
        for (String line : lines) {
            String[] segments = MinecraftUtil.parseLine(line, SEPARATOR);
            if (segments.length >= GEN_4_LENGTH && segments.length % 2 == 0) {
                Warp warp = null;
                boolean valid = true;
                try {
                    String name = segments[0];

                    double x = Double.parseDouble(segments[1]);
                    double y = Double.parseDouble(segments[2]);
                    double z = Double.parseDouble(segments[3]);
                    float yaw = Float.parseFloat(segments[4]);
                    float pitch = Float.parseFloat(segments[5]);

                    yaw = (yaw < 0) ? (360 + (yaw % 360)) : (yaw % 360);

                    World world = server.getWorld(segments[6]);
                    String warpOwner = segments[7];
                    String creator = segments[10];
                    FixedLocation location = new FixedLocation(world, x, y, z, yaw, pitch);
                    LocationWrapper wrapper = new LocationWrapper(location, segments[6]);
                    warp = new Warp(name, creator, warpOwner, wrapper);
                    warp.setMessage(segments[9]);
                    warp.setPrice(Integer.parseInt(segments[11]));
                    for (int i = GEN_4_LENGTH; i < segments.length - 1; i += 2) {
                        warp.addEditor(segments[i], segments[i + 1]);
                    }

                    Visibility v = Visibility.parseLevel(Integer.parseInt(segments[8]));
                    if (v != null) {
                        warp.visibility = v;
                    } else {
                        MyWarp.logger.warning("Illegal visibility found (" + warp.name + " by " + warp.getOwner() + ")");
                        valid = false;
                    }
                } catch (NumberFormatException nfe) {
                    valid = false;
                    MyWarp.logger.warning("Unable to parse a value (" + nfe.getMessage() + ")");
                } catch (Exception e) {
                    MyWarp.logger.severe("Catched an unhandled exception", e);
                    valid = false;
                }
                if (valid && warp != null) {
                    warps.add(warp);
                }
            } else {
                MyWarp.logger.warning("Invalid warp line found:");
                MyWarp.logger.warning(line);
            }
        }
        return warps;
    }

    private void writeWarps(List<Warp> warps) {
        try {
            FileWriter writer = new FileWriter(this.file);
            try {
                writer.write("!version 3\n");
                for (Warp warp : warps) {
                    writeWarp(warp, writer, 3);
                }
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            MyWarp.logger.severe("Unable to write the file", e);
        }
    }

    private static void writeWarp(Warp warp, Writer writer, int version) throws IOException {
        try {
            StringBuilder warpLine = new StringBuilder();
            LocationWrapper l = warp.getLocationWrapper();
            FixedLocation location = l.getLocation();
            warpLine.append(makeParsable(warp.name) + SEPARATOR);
            warpLine.append(makeParsable(location.x) + SEPARATOR);
            warpLine.append(makeParsable(location.y) + SEPARATOR);
            warpLine.append(makeParsable(location.z) + SEPARATOR);
            warpLine.append(makeParsable(location.yaw) + SEPARATOR);
            warpLine.append(makeParsable(location.pitch) + SEPARATOR);
            if (version >= 1) {
                warpLine.append(makeParsable(l.getWorld()) + SEPARATOR);
                warpLine.append(makeParsable(warp.getOwner()) + SEPARATOR);
                warpLine.append(makeParsable(warp.visibility.level) + SEPARATOR);
                warpLine.append(makeParsable(warp.welcomeMessage) + SEPARATOR);
                if (version >= 3) {
                    warpLine.append(makeParsable(warp.getCreator()) + SEPARATOR);
                    warpLine.append(makeParsable(warp.getPrice()) + SEPARATOR);
                }
                for (String editor : warp.getEditors()) {
                    EditorPermissions ep = warp.getEditorPermissions(editor);
                    if (ep != null) {
                        warpLine.append(makeParsable(editor) + SEPARATOR);
                        warpLine.append(makeParsable(ep.getPermissionString()) + SEPARATOR);
                    }
                }
                if (version == 2) {
                    warpLine.append(makeParsable(warp.getCreator()) + SEPARATOR);
                }
            }
            writer.append(warpLine + "\n");
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            MyWarp.logger.severe("Unable to write warp: '" + warp.name + "' by " + warp.getOwner(), e);
        }
    }

    private static String makeParsable(int input) {
        return Integer.toString(input);
    }

    private static String makeParsable(double input) {
        return Double.toString(input);
    }

    private static String makeParsable(String input) {
        // Length output
        int length = 0;
        // Maximum length = twice input length (to escape at least each
        // character)
        char[] output = new char[input.length() * 2];
        char[] in = input.toCharArray();
        for (int i = 0; i < in.length; i++) {
            char c = in[i];
            switch (c) {
            case ':':
            case '\\':
            case '"':
            case '#':
            case '!':
                output[length++] = '\\';
            }
            output[length++] = c;
        }
        return new String(Arrays.copyOf(output, length));
    }

    @Override
    public void addWarp(Warp... warps) {
        if (warps.length > 0) {
            try {
                FileWriter writer = new FileWriter(this.file, true);
                try {
                    // TODO: Determine version
                    int version = 3;
                    for (Warp warp : warps) {
                        writeWarp(warp, writer, version);
                    }
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                MyWarp.logger.severe("Unable to write the file", e);
            }
        }
    }

    @Override
    public void deleteWarp(Warp warp) {
        // First read all, then delete the selected and then write all
        List<Warp> warps = this.getWarps();
        warps.remove(warp);
        this.writeWarps(warps);
    }

    private static Warp getWarp(List<Warp> warps, IdentificationInterface identification) {
        for (Warp warp : warps) {
            if (identification.isIdentificated(warp)) {
                return warp;
            }
        }
        return null;
    }

    @Override
    public void updateCreator(Warp warp) {
        List<Warp> warps = this.getWarps();
        Warp updated = warps.get(warps.indexOf(warp));
        updated.setCreator(warp.getCreator());
        this.writeWarps(warps);
    }

    @Override
    public void updateOwner(Warp warp, IdentificationInterface identification) {
        List<Warp> warps = this.getWarps();
        Warp updated = getWarp(warps, identification);
        updated.setOwner(warp.getOwner());
        this.writeWarps(warps);
    }

    @Override
    public void updateName(Warp warp, IdentificationInterface identification) {
        List<Warp> warps = this.getWarps();
        Warp updated = getWarp(warps, identification);
        updated.name = warp.name;
        this.writeWarps(warps);
    }

    @Override
    public void updateLocation(Warp warp) {
        List<Warp> warps = this.getWarps();
        Warp updated = warps.get(warps.indexOf(warp));
        updated.setLocation(warp.getLocation());
        this.writeWarps(warps);
    }

    @Override
    public void updateMessage(Warp warp) {
        List<Warp> warps = this.getWarps();
        Warp updated = warps.get(warps.indexOf(warp));
        updated.setMessage(warp.welcomeMessage);
        this.writeWarps(warps);
    }

    @Override
    public void updateVisibility(Warp warp) {
        List<Warp> warps = this.getWarps();
        Warp updated = warps.get(warps.indexOf(warp));
        updated.visibility = warp.visibility;
        this.writeWarps(warps);
    }

    @Override
    public void updateEditor(Warp warp, String name) {
        List<Warp> warps = this.getWarps();
        warps.set(warps.indexOf(warp), warp);
        this.writeWarps(warps);
    }

    @Override
    public void updatePrice(Warp warp) {
        List<Warp> warps = this.getWarps();
        Warp updated = warps.get(warps.indexOf(warp));
        updated.setPrice(warp.getPrice());
        this.writeWarps(warps);
    }

    @Override
    public boolean create(File file) {
        if (this.load(file)) {
            this.clear();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getFilename() {
        return "hmod.txt";
    }

    @Override
    public void clear() {
        this.writeWarps(new ArrayList<Warp>(0));
    }

    private final class NameIdentification implements IdentificationInterface {

        private final String name;
        private final String owner;

        public NameIdentification(Warp warp) {
            this(warp.name, warp.getOwner());
        }

        public NameIdentification(String name, String owner) {
            this.name = name;
            this.owner = owner;
        }

        @Override
        public boolean isIdentificated(Warp warp) {
            return warp.name.equalsIgnoreCase(this.name) && warp.getOwner().equals(this.owner);
        }

    }

    @Override
    public IdentificationInterface createIdentification(Warp warp) {
        return new NameIdentification(warp);
    }
}
