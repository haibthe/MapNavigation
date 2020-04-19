package com.hb.map.navigation.v1.snap;

import android.location.Location;

import com.hb.map.navigation.v1.routeprogress.RouteProgress;


public abstract class Snap {

    public abstract Location getSnappedLocation(Location location, RouteProgress routeProgress);
}
