package com.hb.map.navigation.ui.v1;

import androidx.annotation.NonNull;

import com.hb.map.navigation.v1.navigation.OfflineError;
import com.hb.map.navigation.v1.navigation.OnOfflineRouteFoundCallback;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

class OfflineRouteFoundCallback implements OnOfflineRouteFoundCallback {

    private final NavigationViewRouter router;

    OfflineRouteFoundCallback(NavigationViewRouter router) {
        this.router = router;
    }

    @Override
    public void onRouteFound(@NonNull DirectionsRoute offlineRoute) {
        router.updateCurrentRoute(offlineRoute);
        router.updateCallStatusReceived();
    }

    @Override
    public void onError(@NonNull OfflineError error) {
        router.onRequestError(error.getMessage());
        router.updateCallStatusReceived();
    }
}
