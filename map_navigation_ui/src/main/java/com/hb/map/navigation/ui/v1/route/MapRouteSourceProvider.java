package com.hb.map.navigation.ui.v1.route;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

class MapRouteSourceProvider {

    GeoJsonSource build(String id, FeatureCollection featureCollection, GeoJsonOptions options) {
        return new GeoJsonSource(id, featureCollection, options);
    }
}
