package com.hb.map.navigation.app

import androidx.multidex.MultiDexApplication
import com.mapbox.mapboxsdk.Mapbox

class MapNavigationApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
    }
}