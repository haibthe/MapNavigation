package com.hb.map.navigation.ui.v1;

import com.mapbox.mapboxsdk.Mapbox;

class MapConnectivityController {

    void assign(Boolean state) {
        Mapbox.setConnected(state);
    }
}
