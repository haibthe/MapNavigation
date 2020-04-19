package com.hb.map.navigation.v1.navigation.metrics;

import android.location.Location;

import com.hb.map.navigation.v1.routeprogress.RouteProgress;

public interface NavigationMetricListener {

    void onRouteProgressUpdate(RouteProgress routeProgress);

    void onOffRouteEvent(Location offRouteLocation);

    void onArrival(RouteProgress routeProgress);
}
