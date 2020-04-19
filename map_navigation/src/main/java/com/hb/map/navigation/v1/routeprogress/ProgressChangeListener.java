package com.hb.map.navigation.v1.routeprogress;

import android.location.Location;

public interface ProgressChangeListener {
    void onProgressChange(Location location, RouteProgress routeProgress);
}
