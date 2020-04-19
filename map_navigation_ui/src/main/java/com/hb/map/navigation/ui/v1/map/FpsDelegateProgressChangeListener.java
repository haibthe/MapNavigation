package com.hb.map.navigation.ui.v1.map;

import android.location.Location;

import com.hb.map.navigation.v1.routeprogress.ProgressChangeListener;
import com.hb.map.navigation.v1.routeprogress.RouteProgress;

class FpsDelegateProgressChangeListener implements ProgressChangeListener {

    private final MapFpsDelegate fpsDelegate;

    FpsDelegateProgressChangeListener(MapFpsDelegate fpsDelegate) {
        this.fpsDelegate = fpsDelegate;
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        fpsDelegate.adjustFpsFor(routeProgress);
    }
}
