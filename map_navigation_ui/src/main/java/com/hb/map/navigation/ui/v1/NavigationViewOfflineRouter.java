package com.hb.map.navigation.ui.v1;

import com.hb.map.navigation.v1.navigation.MapboxOfflineRouter;
import com.hb.map.navigation.v1.navigation.NavigationRoute;
import com.hb.map.navigation.v1.navigation.OfflineRoute;

import timber.log.Timber;

class NavigationViewOfflineRouter {

    private final MapboxOfflineRouter offlineRouter;
    private final NavigationViewRouter router;
    private boolean isConfigured;
    private String tileVersion;

    NavigationViewOfflineRouter(MapboxOfflineRouter offlineRouter, NavigationViewRouter router) {
        this.offlineRouter = offlineRouter;
        this.router = router;
    }

    void configure(String tileVersion) {
        if (!isConfigured || isNew(tileVersion)) {
            offlineRouter.configure(tileVersion, new OfflineRouterConfiguredCallback(this));
        }
        this.tileVersion = tileVersion;
    }

    void setIsConfigured(boolean isConfigured) {
        this.isConfigured = isConfigured;
    }

    boolean isConfigured() {
        return isConfigured;
    }

    void findRouteWith(NavigationRoute.Builder builder) {
        if (!isConfigured) {
            Timber.e("Cannot find route - offline router is not configured");
            return;
        }

        OfflineRoute offlineRoute = OfflineRoute.builder(builder).build();
        offlineRouter.findRoute(offlineRoute, new OfflineRouteFoundCallback(router));
    }

    private boolean isNew(String tileVersion) {
        return !this.tileVersion.equals(tileVersion);
    }
}