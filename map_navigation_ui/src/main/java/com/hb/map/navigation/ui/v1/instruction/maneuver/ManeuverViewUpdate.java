package com.hb.map.navigation.ui.v1.instruction.maneuver;

import android.graphics.Canvas;
import android.graphics.PointF;

interface ManeuverViewUpdate {
    void updateManeuverView(Canvas canvas, int primaryColor, int secondaryColor, PointF size, float roundaboutAngle);
}
