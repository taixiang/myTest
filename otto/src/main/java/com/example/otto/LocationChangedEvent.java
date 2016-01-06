package com.example.otto;

/**
 * Created by taixiang on 2015/10/22.
 */
public class LocationChangedEvent {

    public final float lat;
    public final float lon;

    public LocationChangedEvent(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override public String toString() {
        return new StringBuilder("(") //
                .append(lat) //
                .append(", ") //
                .append(lon) //
                .append(")") //
                .toString();
    }
}
