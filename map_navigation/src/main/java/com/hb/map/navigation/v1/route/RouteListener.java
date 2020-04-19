package com.hb.map.navigation.v1.route;


import androidx.annotation.Nullable;

import com.hb.map.navigation.v1.routeprogress.RouteProgress;
import com.mapbox.api.directions.v5.models.DirectionsResponse;

/**
 * Will fire when either a successful / failed response is received.
 */
public interface RouteListener {

    void onResponseReceived(DirectionsResponse response, @Nullable RouteProgress routeProgress);

    void onErrorReceived(Throwable throwable);
}
