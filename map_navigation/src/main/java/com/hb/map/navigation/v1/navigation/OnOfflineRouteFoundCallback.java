package com.hb.map.navigation.v1.navigation;


import androidx.annotation.NonNull;

import com.mapbox.api.directions.v5.models.DirectionsRoute;

/**
 * Callback used for finding offline routes.
 */
public interface OnOfflineRouteFoundCallback {

    /**
     * Called when an offline route is found.
     *
     * @param route offline route
     */
    void onRouteFound(@NonNull DirectionsRoute route);

    /**
     * Called when there was an error fetching the offline route.
     *
     * @param error with message explanation
     */
    void onError(@NonNull OfflineError error);
}
