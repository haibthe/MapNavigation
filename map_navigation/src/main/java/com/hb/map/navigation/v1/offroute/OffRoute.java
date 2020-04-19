package com.hb.map.navigation.v1.offroute;

import android.location.Location;

import com.hb.map.navigation.v1.navigation.MapboxNavigationOptions;
import com.hb.map.navigation.v1.routeprogress.RouteProgress;


public abstract class OffRoute {

    public abstract boolean isUserOffRoute(Location location, RouteProgress routeProgress,
                                           MapboxNavigationOptions options);
}
