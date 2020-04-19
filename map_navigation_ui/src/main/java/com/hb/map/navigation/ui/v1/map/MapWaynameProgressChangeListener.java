package com.hb.map.navigation.ui.v1.map;

import android.location.Location;

import com.hb.map.navigation.v1.routeprogress.ProgressChangeListener;
import com.hb.map.navigation.v1.routeprogress.RouteProgress;

class MapWaynameProgressChangeListener implements ProgressChangeListener {

    private final MapWayName mapWayName;

    MapWaynameProgressChangeListener(MapWayName mapWayName) {
        this.mapWayName = mapWayName;
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        mapWayName.updateProgress(location, routeProgress.currentStepPoints());
    }
}
