package com.hb.map.navigation.ui.v1;

class RegionDownloadCallback implements OfflineRegionDownloadCallback {

    private static final Boolean DISCONNECT_STATE = false;
    private final MapConnectivityController connectivityController;

    RegionDownloadCallback(MapConnectivityController connectivityController) {
        this.connectivityController = connectivityController;
    }

    @Override
    public void onComplete() {
        connectivityController.assign(DISCONNECT_STATE);
    }

    @Override
    public void onError(String error) {
        connectivityController.assign(DISCONNECT_STATE);
    }
}
