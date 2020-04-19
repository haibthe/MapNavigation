package com.hb.map.navigation.ui.v1.instruction;

import com.mapbox.api.directions.v5.models.BannerComponents;

interface NodeVerifier {
    boolean isNodeType(BannerComponents bannerComponents);
}
