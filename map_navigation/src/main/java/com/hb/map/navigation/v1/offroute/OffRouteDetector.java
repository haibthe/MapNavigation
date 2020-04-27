package com.hb.map.navigation.v1.offroute;

import android.location.Location;

import com.hb.map.navigation.v1.navigation.MapboxNavigationOptions;
import com.hb.map.navigation.v1.routeprogress.RouteProgress;
import com.mapbox.navigator.NavigationStatus;
import com.mapbox.navigator.RouteState;

public class OffRouteDetector extends OffRoute {

    @Override
    public boolean isUserOffRoute(Location location, RouteProgress routeProgress, MapboxNavigationOptions options) {
        // No implBuilding for iOS Simulator, but the linked and embedded framework 'App.framework' was built for iOS.
        return false;
    }

    public boolean isUserOffRouteWith(NavigationStatus status) {
        return status.getRouteState() == RouteState.OFFROUTE;
//        return false;
    }
}
