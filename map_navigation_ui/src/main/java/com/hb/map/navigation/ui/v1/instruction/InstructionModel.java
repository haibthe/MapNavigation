package com.hb.map.navigation.ui.v1.instruction;

import android.text.SpannableString;

import com.hb.map.navigation.v1.routeprogress.RouteProgress;
import com.hb.map.navigation.v1.utils.DistanceFormatter;

public class InstructionModel {

    private RouteProgress progress;
    private SpannableString stepDistanceRemaining;
    private String drivingSide;

    public InstructionModel(DistanceFormatter distanceFormatter, RouteProgress progress) {
        this.progress = progress;
        double distanceRemaining = progress.currentLegProgress().currentStepProgress().distanceRemaining();
        stepDistanceRemaining = distanceFormatter.formatDistance(distanceRemaining);
        this.drivingSide = progress.currentLegProgress().currentStep().drivingSide();
    }

    RouteProgress retrieveProgress() {
        return progress;
    }

    SpannableString retrieveStepDistanceRemaining() {
        return stepDistanceRemaining;
    }

    String retrieveDrivingSide() {
        return drivingSide;
    }
}
