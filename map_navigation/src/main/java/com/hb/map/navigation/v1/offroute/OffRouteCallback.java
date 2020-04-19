package com.hb.map.navigation.v1.offroute;


import com.hb.map.navigation.v1.routeprogress.RouteProgress;

public interface OffRouteCallback {
    /**
     * This callback will fire when the {@link OffRouteDetector} determines that the user
     * location is close enough to the upcoming {@link com.mapbox.api.directions.v5.models.LegStep}.
     * <p>
     * In this case, the step index needs to be increased for the next {@link RouteProgress} generation.
     */
    void onShouldIncreaseIndex();
}
