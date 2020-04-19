package com.hb.map.navigation.ui.v1.route;

class RouteConstants {
    static final String MAPBOX_LOCATION_ID = "mapbox-location";
    static final String CONGESTION_KEY = "congestion";
    static final String ROUTE_SOURCE_ID = "mapbox-navigation-route-source";
    static final String ROUTE_LAYER_ID = "mapbox-navigation-route-layer";
    static final String ROUTE_SHIELD_LAYER_ID = "mapbox-navigation-route-shield-layer";
    static final String WAYPOINT_SOURCE_ID = "mapbox-navigation-waypoint-source";
    static final String WAYPOINT_LAYER_ID = "mapbox-navigation-waypoint-layer";
    static final int TWO_POINTS = 2;
    static final int THIRTY = 30;
    static final String ARROW_BEARING = "mapbox-navigation-arrow-bearing";
    static final String ARROW_SHAFT_SOURCE_ID = "mapbox-navigation-arrow-shaft-source";
    static final String ARROW_HEAD_SOURCE_ID = "mapbox-navigation-arrow-head-source";
    static final String ARROW_SHAFT_CASING_LINE_LAYER_ID = "mapbox-navigation-arrow-shaft-casing-layer";
    static final String ARROW_SHAFT_LINE_LAYER_ID = "mapbox-navigation-arrow-shaft-layer";
    static final String ARROW_HEAD_ICON = "mapbox-navigation-arrow-head-icon";
    static final String ARROW_HEAD_ICON_CASING = "mapbox-navigation-arrow-head-icon-casing";
    static final int MAX_DEGREES = 360;
    static final String ARROW_HEAD_CASING_LAYER_ID = "mapbox-navigation-arrow-head-casing-layer";
    static final Float[] ARROW_HEAD_CASING_OFFSET = {0f, -7f};
    static final String ARROW_HEAD_LAYER_ID = "mapbox-navigation-arrow-head-layer";
    static final Float[] ARROW_HEAD_OFFSET = {0f, -7f};
    static final int MIN_ARROW_ZOOM = 10;
    static final int MAX_ARROW_ZOOM = 22;
    static final float MIN_ZOOM_ARROW_SHAFT_SCALE = 2.6f;
    static final float MAX_ZOOM_ARROW_SHAFT_SCALE = 13.0f;
    static final float MIN_ZOOM_ARROW_SHAFT_CASING_SCALE = 3.4f;
    static final float MAX_ZOOM_ARROW_SHAFT_CASING_SCALE = 17.0f;
    static final float MIN_ZOOM_ARROW_HEAD_SCALE = 0.2f;
    static final float MAX_ZOOM_ARROW_HEAD_SCALE = 0.8f;
    static final float MIN_ZOOM_ARROW_HEAD_CASING_SCALE = 0.2f;
    static final float MAX_ZOOM_ARROW_HEAD_CASING_SCALE = 0.8f;
    static final float OPAQUE = 0.0f;
    static final int ARROW_HIDDEN_ZOOM_LEVEL = 14;
    static final float TRANSPARENT = 1.0f;
    static final String LAYER_ABOVE_UPCOMING_MANEUVER_ARROW = "com.mapbox.annotations.points";
    static final int FIRST_COLLECTION_INDEX = 0;
    static final String WAYPOINT_PROPERTY_KEY = "wayPoint";
    static final String WAYPOINT_ORIGIN_VALUE = "origin";
    static final String WAYPOINT_DESTINATION_VALUE = "destination";
    static final String PRIMARY_ROUTE_PROPERTY_KEY = "primary-route";
    static final String MODERATE_CONGESTION_VALUE = "moderate";
    static final String HEAVY_CONGESTION_VALUE = "heavy";
    static final String SEVERE_CONGESTION_VALUE = "severe";
    static final String ORIGIN_MARKER_NAME = "originMarker";
    static final String DESTINATION_MARKER_NAME = "destinationMarker";
}
