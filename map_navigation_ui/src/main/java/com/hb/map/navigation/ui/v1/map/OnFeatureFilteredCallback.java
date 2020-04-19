package com.hb.map.navigation.ui.v1.map;


import androidx.annotation.NonNull;

import com.mapbox.geojson.Feature;

interface OnFeatureFilteredCallback {
    void onFeatureFiltered(@NonNull Feature feature);
}
