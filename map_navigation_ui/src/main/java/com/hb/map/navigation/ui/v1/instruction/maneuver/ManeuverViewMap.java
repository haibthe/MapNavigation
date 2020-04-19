package com.hb.map.navigation.ui.v1.instruction.maneuver;

import androidx.core.util.Pair;

import java.util.HashMap;

import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_LEFT;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_SHARP_LEFT;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_SHARP_RIGHT;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_SLIGHT_LEFT;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_STRAIGHT;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_UTURN;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_TYPE_ARRIVE;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_TYPE_EXIT_ROTARY;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_TYPE_EXIT_ROUNDABOUT;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_TYPE_FORK;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_TYPE_MERGE;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_TYPE_OFF_RAMP;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_TYPE_ROTARY;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_TYPE_ROUNDABOUT;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.STEP_MANEUVER_TYPE_ROUNDABOUT_TURN;

class ManeuverViewMap extends HashMap<Pair<String, String>, ManeuverViewUpdate> {

    ManeuverViewMap() {
        put(new Pair<>(STEP_MANEUVER_TYPE_MERGE, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawMerge(canvas, primaryColor, secondaryColor, size));
        put(new Pair<>(STEP_MANEUVER_TYPE_OFF_RAMP, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawOffRamp(canvas, primaryColor, secondaryColor, size));
        put(new Pair<>(STEP_MANEUVER_TYPE_FORK, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawFork(canvas, primaryColor, secondaryColor, size));
        put(new Pair<>(STEP_MANEUVER_TYPE_ROUNDABOUT, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawRoundabout(canvas, primaryColor, secondaryColor, size, roundaboutAngle));
        put(new Pair<>(STEP_MANEUVER_TYPE_ROUNDABOUT_TURN, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawRoundabout(canvas, primaryColor, secondaryColor, size, roundaboutAngle));
        put(new Pair<>(STEP_MANEUVER_TYPE_EXIT_ROUNDABOUT, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawRoundabout(canvas, primaryColor, secondaryColor, size, roundaboutAngle));
        put(new Pair<>(STEP_MANEUVER_TYPE_ROTARY, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawRoundabout(canvas, primaryColor, secondaryColor, size, roundaboutAngle));
        put(new Pair<>(STEP_MANEUVER_TYPE_EXIT_ROTARY, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawRoundabout(canvas, primaryColor, secondaryColor, size, roundaboutAngle));
        put(new Pair<>(STEP_MANEUVER_TYPE_ARRIVE, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrive(canvas, primaryColor, size));
        put(new Pair<>(STEP_MANEUVER_TYPE_ARRIVE, STEP_MANEUVER_MODIFIER_STRAIGHT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrive(canvas, primaryColor, size));
        put(new Pair<>(STEP_MANEUVER_TYPE_ARRIVE, STEP_MANEUVER_MODIFIER_RIGHT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArriveRight(canvas, primaryColor, size));
        put(new Pair<>(STEP_MANEUVER_TYPE_ARRIVE, STEP_MANEUVER_MODIFIER_LEFT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArriveRight(canvas, primaryColor, size));
        put(new Pair<>(null, STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrowSlightRight(canvas, primaryColor, size));
        put(new Pair<>(null, STEP_MANEUVER_MODIFIER_RIGHT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrowRight(canvas, primaryColor, size));
        put(new Pair<>(null, STEP_MANEUVER_MODIFIER_SHARP_RIGHT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrowSharpRight(canvas, primaryColor, size));
        put(new Pair<>(null, STEP_MANEUVER_MODIFIER_SLIGHT_LEFT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrowSlightRight(canvas, primaryColor, size));
        put(new Pair<>(null, STEP_MANEUVER_MODIFIER_LEFT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrowRight(canvas, primaryColor, size));
        put(new Pair<>(null, STEP_MANEUVER_MODIFIER_SHARP_LEFT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrowSharpRight(canvas, primaryColor, size));
        put(new Pair<>(null, STEP_MANEUVER_MODIFIER_UTURN), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrow180Right(canvas, primaryColor, size));
        put(new Pair<>(null, STEP_MANEUVER_MODIFIER_STRAIGHT), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrowStraight(canvas, primaryColor, size));
        put(new Pair<>(null, null), (canvas, primaryColor, secondaryColor, size, roundaboutAngle) -> ManeuversStyleKit.drawArrowStraight(canvas, primaryColor, size));
    }
}
