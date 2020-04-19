package com.hb.map.navigation.ui.v1;

import com.hb.map.navigation.v1.navigation.MapboxNavigation;

import timber.log.Timber;

class NavigationOfflineDatabaseCallback implements OfflineDatabaseLoadedCallback {

    private final MapboxNavigation navigation;
    private final MapOfflineManager mapOfflineManager;

    NavigationOfflineDatabaseCallback(MapboxNavigation navigation, MapOfflineManager mapOfflineManager) {
        this.navigation = navigation;
        this.mapOfflineManager = mapOfflineManager;
    }

    @Override
    public void onComplete() {
        navigation.addProgressChangeListener(mapOfflineManager);
    }

    @Override
    public void onError(String error) {
        Timber.e(error);
    }

    void onDestroy() {
        mapOfflineManager.onDestroy();
    }
}