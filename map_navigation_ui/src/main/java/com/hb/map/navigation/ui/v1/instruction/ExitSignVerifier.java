package com.hb.map.navigation.ui.v1.instruction;

import com.mapbox.api.directions.v5.models.BannerComponents;

class ExitSignVerifier implements NodeVerifier {

    @Override
    public boolean isNodeType(BannerComponents bannerComponents) {
        return bannerComponents.type().equals("exit") || bannerComponents.type().equals("exit-number");
    }
}
