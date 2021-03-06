package com.hb.map.navigation.ui.v1;


import androidx.annotation.NonNull;

import com.hb.map.navigation.v1.navigation.OfflineError;
import com.hb.map.navigation.v1.navigation.OnOfflineTilesConfiguredCallback;

import timber.log.Timber;

class OfflineRouterConfiguredCallback implements OnOfflineTilesConfiguredCallback {

    private final NavigationViewOfflineRouter offlineRouter;

    OfflineRouterConfiguredCallback(NavigationViewOfflineRouter offlineRouter) {
        this.offlineRouter = offlineRouter;
    }

    @Override
    public void onConfigured(int numberOfTiles) {
        offlineRouter.setIsConfigured(true);
    }

    @Override
    public void onConfigurationError(@NonNull OfflineError error) {
        Timber.e(error.getMessage());
    }
}