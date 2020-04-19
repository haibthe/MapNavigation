package com.hb.map.navigation.ui.v1.instruction;


import androidx.annotation.Nullable;

import com.hb.map.navigation.v1.routeprogress.RouteProgress;
import com.hb.map.navigation.v1.utils.DistanceFormatter;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.BannerText;

public class BannerInstructionModel extends InstructionModel {

    private final BannerText primaryBannerText;
    private final BannerText secondaryBannerText;
    private final BannerText subBannerText;

    public BannerInstructionModel(DistanceFormatter distanceFormatter, RouteProgress progress,
                                  BannerInstructions instructions) {
        super(distanceFormatter, progress);
        primaryBannerText = instructions.primary();
        secondaryBannerText = instructions.secondary();
        subBannerText = instructions.sub();
    }

    BannerText retrievePrimaryBannerText() {
        return primaryBannerText;
    }

    BannerText retrieveSecondaryBannerText() {
        return secondaryBannerText;
    }

    BannerText retrieveSubBannerText() {
        return subBannerText;
    }

    String retrievePrimaryManeuverType() {
        return primaryBannerText.type();
    }

    String retrievePrimaryManeuverModifier() {
        return primaryBannerText.modifier();
    }

    @Nullable
    Double retrievePrimaryRoundaboutAngle() {
        return primaryBannerText.degrees();
    }
}
