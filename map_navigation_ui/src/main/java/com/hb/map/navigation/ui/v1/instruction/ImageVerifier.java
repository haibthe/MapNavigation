package com.hb.map.navigation.ui.v1.instruction;

import android.text.TextUtils;

import com.mapbox.api.directions.v5.models.BannerComponents;

class ImageVerifier implements NodeVerifier {

    @Override
    public boolean isNodeType(BannerComponents bannerComponents) {
        return hasImageUrl(bannerComponents);
    }

    boolean hasImageUrl(BannerComponents components) {
        return !TextUtils.isEmpty(components.imageBaseUrl());
    }
}
