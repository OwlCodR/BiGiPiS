package com.bigipis.bigipis.source;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyMarker implements ClusterItem {
    private LatLng latLng;
    private String title;
    private String snippet;
    private int iconId;

    public MyMarker(LatLng latLng, String title, String snippet, int iconId) {
        this.latLng = latLng;
        this.title = title;
        this.snippet = snippet;
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
