package de.xzise.xwarp.lister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.Warp;


public class ListSection implements Iterable<Warp> {

    public final String title;
    private final List<Warp> warps;

    public ListSection(String title, int maximumLength) {
        this.title = title;
        this.warps = new ArrayList<Warp>(maximumLength);
    }

    public ListSection(String title) {
        this(title, MinecraftUtil.PLAYER_LINES_COUNT);
    }

    public void addWarp(Warp warp) {
        this.warps.add(warp);
    }

    public void addWarps(Collection<Warp> warp) {
        this.warps.addAll(warp);
    }

    @Override
    public Iterator<Warp> iterator() {
        return this.warps.iterator();
    }

}
