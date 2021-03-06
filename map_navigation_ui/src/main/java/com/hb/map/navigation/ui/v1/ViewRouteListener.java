package com.hb.map.navigation.ui.v1;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;

interface ViewRouteListener {

    void onRouteUpdate(DirectionsRoute directionsRoute);

    void onRouteRequestError(String errorMessage);

    void onDestinationSet(Point destination);
}
