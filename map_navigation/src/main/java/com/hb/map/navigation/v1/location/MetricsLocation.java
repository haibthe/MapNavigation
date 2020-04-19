package com.hb.map.navigation.v1.location;

import android.location.Location;

import androidx.annotation.Keep;

@Keep
public class MetricsLocation {
    private Location location;

    public MetricsLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        if (location != null) {
            return location;
        }

        Location metricLocation = new Location("MetricsLocation");
        metricLocation.setLatitude(0.0);
        metricLocation.setLongitude(0.0);

        return metricLocation;
    }
}
