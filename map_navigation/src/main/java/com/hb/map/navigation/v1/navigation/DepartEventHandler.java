package com.hb.map.navigation.v1.navigation;

import android.content.Context;

import com.hb.map.navigation.v1.location.MetricsLocation;
import com.hb.map.navigation.v1.navigation.metrics.SessionState;
import com.hb.map.navigation.v1.routeprogress.MetricsRouteProgress;

class DepartEventHandler {

    private final Context applicationContext;

    DepartEventHandler(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    void send(SessionState sessionState, MetricsRouteProgress routeProgress, MetricsLocation location) {
        NavigationMetricsWrapper.departEvent(sessionState, routeProgress, location.getLocation(), applicationContext);
    }
}