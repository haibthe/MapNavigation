package com.hb.map.navigation.ui.v1;

interface OfflineDatabaseLoadedCallback {

    void onComplete();

    void onError(String error);
}