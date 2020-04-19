package com.hb.map.navigation.ui.v1;

import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;

class CreateOfflineRegionCallback implements OfflineManager.CreateOfflineRegionCallback {

    private final OfflineRegionDownloadCallback callback;

    CreateOfflineRegionCallback(OfflineRegionDownloadCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(OfflineRegion offlineRegion) {
        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
        offlineRegion.setObserver(new OfflineRegionObserver(callback));
    }

    @Override
    public void onError(String error) {
        callback.onError(error);
    }
}
