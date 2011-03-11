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

import de.xzise.xwarp.EditorPermissions;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.WMPlayerListener;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;

public class HModConnection implements DataConnection {

    private File file;
    private Server server;
    private static final char SEPARATOR = ':';
    // Name, X, Y, Z, Yaw, Pitch
    private static final int GEN_1_LENGTH = 6;
    // Gen1 + World, Creator, Visibility, Message
    /** Minimum length of second generation map. */
    private static final int GEN_2_LENGTH = GEN_1_LENGTH + 4;

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
            scanner = new Scanner(this.file);
            int size = 0;
            int invalidSize = 0;
            boolean valid;
            int version = 0;
            World defaultWorld = this.server.getWorlds().get(0);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("!version")) {
                    if (result.size() > 0) {
                        MyWarp.logger.warning("Found version tag after adding warps.");
                    }
                    if (line.length() > 8) {
                        try {
                            version = Integer.parseInt(line.substring(8).trim());
                        } catch (NumberFormatException nfe) {
                            MyWarp.logger.severe("Version tag is invalid number.");
                        }
                    } else {
                        MyWarp.logger.severe("Invalid version tag.");
                    }
                } else if (!line.startsWith("##")) {
                    String[] pieces = WMPlayerListener.parseLine(line, ':');
                    if ((pieces.length >= GEN_2_LENGTH && pieces.length % 2 == 0 && version > 0) || (pieces.length == GEN_1_LENGTH && owner != null && !owner.isEmpty())) {
                        Warp warp = null;
                        valid = true;
                        try {
                            String name = pieces[0];

                            double x = Double.parseDouble(pieces[1]);
                            double y = Double.parseDouble(pieces[2]);
                            double z = Double.parseDouble(pieces[3]);
                            double yaw = Double.parseDouble(pieces[4]);
                            double pitch = Double.parseDouble(pieces[5]);

                            yaw = (yaw < 0) ? (360 + (yaw % 360)) : (yaw % 360);

                            World world = defaultWorld;
                            String warpOwner = owner;
                            // hmod gen 2
                            if (pieces.length >= GEN_2_LENGTH && pieces.length % 2 == 0) {
                                world = this.server.getWorld(pieces[6]);
                                warpOwner = pieces[7];
                            }
                            Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);
                            warp = new Warp(name, warpOwner, location);
                            // hmod gen 2
                            if (pieces.length >= GEN_2_LENGTH && pieces.length % 2 == 0) {
                                Visibility v = Visibility.parseLevel(Integer.parseInt(pieces[8]));
                                if (v != null) {
                                    warp.visibility = v;
                                    warp.setMessage(pieces[9]);
                                    for (int i = GEN_2_LENGTH; i < pieces.length; i += 2) {
                                        warp.addEditor(pieces[i], pieces[2]);
                                    }
                                } else {
                                    MyWarp.logger.warning("Illegal visibilty found (" + warp.name + " by " + warp.creator + ")");
                                    valid = false;
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            valid = false;
                            MyWarp.logger.warning("Unable to parse a location value (" + nfe.getMessage() + ")");
                        } catch (Exception e) {
                            MyWarp.logger.severe("Catched an unhandled exception", e);
                            valid = false;
                        }
                        if (valid && warp != null) {
                            result.add(warp);
                            size++;
                            if (!warp.isValid()) {
                                invalidSize++;
                            }
                        }
                    } else {
                        MyWarp.logger.warning("Invalid line found");
                    }
                }
            }
            MyWarp.logger.info(size + " warps loaded");
            if (invalidSize > 0) {
                MyWarp.logger.warning(invalidSize + " invalid warps found.");
            }
        } catch (FileNotFoundException e) {
            MyWarp.logger.info("hmod file not found!");
        }
        return result;
    }

    private void writeWarps(List<Warp> warps) {
        try {
            FileWriter writer = new FileWriter(this.file);
            writer.write("!version 1\n");
            for (Warp warp : warps) {
                writeWarp(warp, writer);
            }
            writer.close();
        } catch (IOException e) {
            MyWarp.logger.severe("Unable to write the file", e);
        }
    }

    private static void writeWarp(Warp warp, Writer writer) throws IOException {
        StringBuilder warpLine = new StringBuilder();
        Location l = warp.getLocation();
        warpLine.append(makeParsable(warp.name) + SEPARATOR);
        warpLine.append(makeParsable(l.getX()) + SEPARATOR);
        warpLine.append(makeParsable(l.getY()) + SEPARATOR);
        warpLine.append(makeParsable(l.getZ()) + SEPARATOR);
        warpLine.append(makeParsable(l.getYaw()) + SEPARATOR);
        warpLine.append(makeParsable(l.getPitch()) + SEPARATOR);
        warpLine.append(makeParsable(l.getWorld().getName()) + SEPARATOR);
        warpLine.append(makeParsable(warp.creator) + SEPARATOR);
        warpLine.append(makeParsable(warp.visibility.level) + SEPARATOR);
        warpLine.append(makeParsable(warp.welcomeMessage) + SEPARATOR);
        for (String editor : warp.getEditors()) {
            EditorPermissions ep = warp.getEditorPermissions(editor);
            if (ep != null) {
                warpLine.append(makeParsable(editor) + SEPARATOR);
                warpLine.append(makeParsable(ep.getPermissionString()) + SEPARATOR);
            }
        }
        writer.append(warpLine + "\n");
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
                for (Warp warp : warps) {
                    writeWarp(warp, writer);
                }
                writer.close();
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

    @Override
    public void updateCreator(Warp warp) {
        List<Warp> warps = this.getWarps();
        Warp updated = warps.get(warps.indexOf(warp));
        updated.creator = warp.creator;
        this.writeWarps(warps);
    }

    @Override
    public void updateName(Warp warp) {
        List<Warp> warps = this.getWarps();
        Warp updated = warps.get(warps.indexOf(warp));
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
    // Not supported in hmod
    public void updateEditor(Warp warp, String name) {
        List<Warp> warps = this.getWarps();
        // Warp updated = warps.get(warps.indexOf(warp));
        warps.set(warps.indexOf(warp), warp);
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
}
