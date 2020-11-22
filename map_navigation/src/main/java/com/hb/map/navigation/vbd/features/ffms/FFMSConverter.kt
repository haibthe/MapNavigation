package com.hb.map.navigation.vbd.features.ffms

import com.google.gson.Gson
import com.hb.map.navigation.converter.BaseConverter
import com.hb.map.navigation.v1.navigation.NavigationConstants
import com.hb.map.navigation.vbd.entities.VbdRouteResponse
import com.mapbox.api.directions.v5.models.*
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class FFMSConverter
@JvmOverloads
constructor(
    baseUrl: String,
    token: String,
    private val startPoint: Point? = null
) : BaseConverter<VbdRouteResponse>(
    baseUrl, token
) {

    override fun convert(data: VbdRouteResponse): DirectionsRoute? {
        return mapToDirectionRoute(data)
    }

    private fun routeOptions(coords: List<DoubleArray>): RouteOptions {

        val p1 = coords[0]
        val p2 = coords[1]
        val builder = RouteOptions.builder()
        builder.accessToken(getToken())
            .baseUrl(getBaseUrl())
            .user("test")
            .bearings(";")
            .coordinates(
                listOf(
                    Point.fromLngLat(p1[1], p1[0]),
                    Point.fromLngLat(p2[1], p2[0])
                )
            )
            .profile("driving-traffic")
            .alternatives(false)
            .requestUuid(UUID.randomUUID().toString())
            .voiceUnits("metric")
            .voiceInstructions(true)
            .bannerInstructions(true)
            .steps(true)
            .overview("full")
            .geometries("polyline6")
            .continueStraight(true)
            .roundaboutExits(true)
            .language("vn")


        return builder.build()
    }

    private fun mapToDirectionRoute(res: VbdRouteResponse): DirectionsRoute? {
        val route = res.routes?.get(0) ?: return null

        val totalDistance = route.viaDistances[1].toDouble()
        val totalDuration = route.viaDurations[1].toDouble()
        val totalIndices = route.viaIndices[1]
        val builder = DirectionsRoute.builder()
            .distance(totalDistance)
            .duration(totalDuration)
            .geometry(route.geometry)
            .routeOptions(routeOptions(res.viaPoints))
            .weight(23.5)
            .weightName("routability")

        val p1 = res.viaPoints[0]

        val legs = ArrayList<RouteLeg>()
        builder.legs(legs)
        try {
            route.steps?.apply {
                val size = distances.size
                val path = PolylineUtils.decode(route.geometry, Constants.PRECISION_6)
                val steps = ArrayList<LegStep>()
                var idx = 0
                val bc = BannerComponents.builder().type("text")
                val bt = BannerText.builder()
                val banner = BannerInstructions.builder()
                for (i in 0 until size - 1) {
                    val point = path[indices[i]]
                    val subPath = arrayListOf<Point>()
                    if (i == 0) {
                        val dis = if (startPoint == null) 100.0
                        else TurfMeasurement.distance(
                            startPoint,
                            Point.fromLngLat(p1[1], p1[0]),
                            TurfConstants.UNIT_METERS
                        )

                        if (dis > 10.0) {
                            val dx = path[0].longitude() - path[1].longitude()
                            val dy = path[0].latitude() - path[1].latitude()
                            subPath.add(Point.fromLngLat(p1[1] + dx, p1[0] + dy))
                        } else {
                            subPath.add(startPoint!!)
                            subPath.add(Point.fromLngLat(p1[1], p1[0]))
                        }
                    }
                    for (j in idx..indices[i]) {
                        subPath.add(path[j])
                    }
                    idx = indices[i]

                    val guide = getGuide(turns[i], true)
                    val bannerList = ArrayList<BannerInstructions>()
                    banner.distanceAlongGeometry(distances[i].toDouble())
                    var name = names[i].replace("Đường", "")
                    bc.text(name)
                    bt.type(guide.first)
                        .modifier(guide.second)
                        .text(name)!!
                        .components(listOf(bc.build()))
                    banner.primary(bt.build())

                    val guideNext = getGuide(turns[i + 1], true)
                    name = names[i + 1].replace("Đường", "")
                    bc.text(name)
                    bt.type(guideNext.first)
                        .modifier(guideNext.second)
                        .text(name)!!
                        .components(listOf(bc.build()))
                    banner.sub(bt.build())

                    bannerList.add(banner.build())
                    val intersection = StepIntersection.builder()
                        .rawLocation(doubleArrayOf(point.longitude(), point.latitude()))
                        .bearings(listOf(0))
                        .entry(listOf(true))
                        .out(0)
                        .build()

                    val guideInManeuver = getGuide(turns[i], false)
                    val maneuver = StepManeuver.builder()
                        .bearingBefore(0.0)
                        .bearingAfter(0.0)
                        .rawLocation(doubleArrayOf(point.longitude(), point.latitude()))
                        .type(guideInManeuver.first)
                        .modifier(guideInManeuver.second)
                        .instruction(names[i])
                        .build()

                    val stepBuilder = LegStep.builder()
                        .distance(distances[i].toDouble())
                        .duration(durations[i].toDouble())
                        .geometry(PolylineUtils.encode(subPath, Constants.PRECISION_6))
                        .name(names[i])
                        .drivingSide("right")
                        .mode("driving")
                        .intersections(listOf(intersection))
                        .maneuver(maneuver)
                        .weight(0.0)
                        .voiceInstructions(listOf())
                        .bannerInstructions(bannerList)

                    if (turns[i] == 11) {
                        stepBuilder.rotaryName(names[i])
                    }

                    steps.add(stepBuilder.build())
                }

                val idxBefore = indices[size - 2]
                idx = indices[size - 1]
                val subPath = arrayListOf<Point>()
                for (j in idxBefore..idx) {
                    subPath.add(path[j])
                }
                subPath.reversed()


                steps.add(generateStepBeforeFinalStep(subPath))
                val point = path.last()
                steps.add(generateFinalStep(point))

                val leg = RouteLeg.builder()
                    .distance(totalDistance)
                    .duration(totalDuration)
                    .summary("")
                    .steps(steps)
                    .build()
                legs.add(leg)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        val result = builder.build()

        val temp = Gson().toJson(result)
        Timber.i("DirectionsRoute: $temp")

        return result
    }

    private fun generateStepBeforeFinalStep(subPath: List<Point>): LegStep {
        val ls = LineString.fromLngLats(subPath)
        val point = TurfMeasurement.along(ls, 15.0, TurfConstants.UNIT_METERS)

        val bannerList = ArrayList<BannerInstructions>()
        val guide = getGuide(1, true)

        val bc = BannerComponents.builder().type("text")
        val bt = BannerText.builder()
        val banner = BannerInstructions.builder()

        val text = "Đến nơi"
        bc.text(text)
        bt.type(guide.first)
            .modifier(guide.second)
            .text(text)!!
            .components(listOf(bc.build()))

        banner.distanceAlongGeometry(15.0)
            .primary(bt.build())
        bannerList.add(banner.build())

        val guideInManeuver = getGuide(1, false)
        val maneuver = StepManeuver.builder()
            .bearingBefore(0.0)
            .bearingAfter(0.0)
            .rawLocation(doubleArrayOf(point.longitude(), point.latitude()))
            .type(guideInManeuver.first)
            .modifier(guideInManeuver.second)
            .instruction(text)
            .build()
        val intersection = StepIntersection.builder()
            .rawLocation(doubleArrayOf(point.longitude(), point.latitude()))
            .bearings(listOf(0))
            .entry(listOf(true))
            .out(0)
            .build()

        val stepBuilder = LegStep.builder()
            .distance(0.0)
            .duration(0.0)
            .geometry(PolylineUtils.encode(subPath, Constants.PRECISION_6))
            .name(text)
            .drivingSide("right")
            .mode("driving")
            .intersections(listOf(intersection))
            .maneuver(maneuver)
            .weight(0.0)
            .voiceInstructions(listOf())
            .bannerInstructions(bannerList)

        return stepBuilder.build()
    }

    private fun generateFinalStep(point: Point): LegStep {
        val guideInManeuver = getGuide(15, false)
        val maneuver = StepManeuver.builder()
            .bearingBefore(0.0)
            .bearingAfter(0.0)
            .rawLocation(doubleArrayOf(point.longitude(), point.latitude()))
            .type(guideInManeuver.first)
            .modifier(guideInManeuver.second)
            .instruction("Đến nơi")
            .build()
        val intersection = StepIntersection.builder()
            .rawLocation(doubleArrayOf(point.longitude(), point.latitude()))
            .bearings(listOf(0))
            .entry(listOf(true))
            .out(0)
            .build()

        val stepBuilder = LegStep.builder()
            .distance(0.0)
            .duration(0.0)
            .geometry(PolylineUtils.encode(listOf(point), Constants.PRECISION_6))
            .name("Đến nơi")
            .drivingSide("right")
            .mode("driving")
            .intersections(listOf(intersection))
            .maneuver(maneuver)
            .weight(0.0)
            .voiceInstructions(listOf())
            .bannerInstructions(listOf())
        return stepBuilder.build()
    }

    private fun getGuide(turn: Int, isBanner: Boolean): Pair<String, String> {

        if (turn == 11) {
            return if (!isBanner)
                (StepManeuver.ROTARY to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
            else
                (StepManeuver.ROUNDABOUT to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
        }

        if (turn == 12 && isBanner) {
            return (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
        }

        return when (turn) {
            1 -> (StepManeuver.CONTINUE to NavigationConstants.STEP_MANEUVER_MODIFIER_STRAIGHT)
            2 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT)
            3 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
            5 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_UTURN)
            7 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_SLIGHT_LEFT)
            8 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_LEFT)
            10 -> (StepManeuver.DEPART to NavigationConstants.STEP_MANEUVER_MODIFIER_STRAIGHT)
            12 -> (StepManeuver.EXIT_ROTARY to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
            15 -> (StepManeuver.ARRIVE to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
            else -> (StepManeuver.CONTINUE to NavigationConstants.STEP_MANEUVER_MODIFIER_STRAIGHT)
        }
    }
}