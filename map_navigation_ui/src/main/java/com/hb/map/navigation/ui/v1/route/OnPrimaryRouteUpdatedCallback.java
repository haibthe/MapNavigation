package com.hb.map.navigation.ui.v1.route;

import com.mapbox.geojson.FeatureCollection;

import java.util.List;

interface OnPrimaryRouteUpdatedCallback {
    void onPrimaryRouteUpdated(List<FeatureCollection> updatedRouteCollections);
}
