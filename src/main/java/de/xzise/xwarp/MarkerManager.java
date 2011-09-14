package de.xzise.xwarp;

import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import de.xzise.xwarp.Warp.Visibility;

public class MarkerManager {

    private MarkerAPI markerAPI;
    private MarkerSet markerSet;
    private MarkerIcon markerIcon;
    private final PluginProperties properties;

    public MarkerManager(PluginProperties properties) {
        this.properties = properties;
    }

    public MarkerSet getMarkerSet() {
        return this.markerSet;
    }

    public void setMarkerSet(MarkerSet markerSet) {
        if (this.markerSet != markerSet) {
            if (this.markerSet != null) {
                this.markerSet.deleteMarkerSet();
            }
            this.markerSet = markerSet;
        }
    }

    public MarkerIcon getMarkerIcon() {
        return this.markerIcon;
    }

    public void setMarkerIcon(MarkerIcon markerIcon) {
        if (this.markerIcon != markerIcon) {
            this.markerIcon = markerIcon;
        }
    }

    public MarkerAPI getMarkerAPI() {
        return this.markerAPI;
    }

    public void setMarkerAPI(MarkerAPI markerAPI) {
        this.markerAPI = markerAPI;
        if (this.markerAPI == null) {
            this.setMarkerSet(null);
            this.setMarkerIcon(null);
        }
    }

    public ImmutableList<Visibility> getMarkerVisibilities() {
        Builder<Visibility> builder = ImmutableList.builder();
        for (String string : this.properties.getMarkerVisibilities()) {
            Visibility visibility = Visibility.parseString(string);
            if (visibility != null) {
                builder.add(visibility);
            }
        }
        return builder.build();
    }
}
