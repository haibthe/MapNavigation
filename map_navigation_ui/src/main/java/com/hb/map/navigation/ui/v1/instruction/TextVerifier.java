package com.hb.map.navigation.ui.v1.instruction;

import com.mapbox.api.directions.v5.models.BannerComponents;

class TextVerifier implements NodeVerifier {
    @Override
    public boolean isNodeType(BannerComponents bannerComponents) {
        return bannerComponents.text() != null && !bannerComponents.text().isEmpty();
    }
}
