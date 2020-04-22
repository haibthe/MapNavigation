package com.hb.map.navigation.data.repositories

import com.google.gson.Gson
import com.hb.map.navigation.data.entities.VbdRouteResponse
import com.hb.map.navigation.data.store.AppDataSource
import com.hb.map.navigation.domain.repositories.AppRepository
import com.hb.map.navigation.v1.navigation.NavigationConstants
import com.mapbox.api.directions.v5.models.*
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.turf.TurfMeasurement
import io.reactivex.Observable
import timber.log.Timber


class AppRepositoryImpl(
    private val local: AppDataSource.Local,
    private val service: AppDataSource.Service
) : AppRepository {


    override fun findRoute(start: LatLng, end: LatLng): Observable<DirectionsRoute?> {
        return local.getRouteFromAssets("vbd_route.json")
            .map(this::mapToDirectionRoute)
    }

    val TEMP = "{\n" +
            "    \"baseUrl\": \"https://api.mapbox.com\",\n" +
            "    \"user\": \"mapbox\",\n" +
            "    \"profile\": \"driving-traffic\",\n" +
            "    \"coordinates\": [\n" +
            "      [106.6920881, 10.789958],\n" +
            "      [106.6841719, 10.7767865]\n" +
            "    ],\n" +
            "    \"alternatives\": true,\n" +
            "    \"language\": \"en\",\n" +
            "    \"bearings\": \";\",\n" +
            "    \"continue_straight\": true,\n" +
            "    \"roundabout_exits\": true,\n" +
            "    \"geometries\": \"polyline6\",\n" +
            "    \"overview\": \"full\",\n" +
            "    \"steps\": true,\n" +
            "    \"annotations\": \"congestion,distance\",\n" +
            "    \"voice_instructions\": true,\n" +
            "    \"banner_instructions\": true,\n" +
            "    \"voice_units\": \"metric\",\n" +
            "    \"access_token\": \"pk.eyJ1Ijoibm9uMjM1IiwiYSI6ImNrOGFnZHYzcDAxdDMzb3BpNmthdW1lMHAifQ.BmGzpCy_5Pl2XxIS7XCYbA\",\n" +
            "    \"uuid\": \"ck953h0pk00an5qqnkl9zcca5\"\n" +
            "  }";

    private fun routeOptions(): RouteOptions {
        return RouteOptions.fromJson(TEMP)
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
            .routeOptions(routeOptions())
            .weight(23.5)
            .weightName("routability")

        val legs = ArrayList<RouteLeg>()
        builder.legs(legs)
        try {
            route.steps?.apply {

                val size = distances.size
                val path = PolylineUtils.decode(route.geometry, PRECISION_6)
                val steps = ArrayList<LegStep>()
                var idx = 0
                for (i in 0 until size) {

                    val pair = (idx to indices[i])
                    idx = indices[i]
                    val point = path[indices[i]]
                    val beforeIndex = if (indices[i] == 0) 0 else indices[i] - 1
                    val afterIndex =
                        if (indices[i] == totalIndices) totalIndices else indices[i] + 1
                    val beforeBearing =
                        kotlin.math.abs(TurfMeasurement.bearing(point, path[beforeIndex]))
                    val bearing = kotlin.math.abs(TurfMeasurement.bearing(point, path[afterIndex]))
                    val subPath = arrayListOf<Point>()
                    if (i == 0) {
                        subPath.add(Point.fromLngLat(106.6923, 10.7901))
                    }
                    for (j in pair.first..pair.second) {
                        subPath.add(path[j])
                    }

                    val bannerList = ArrayList<BannerInstructions>()
                    if (turns[i] != 15) {
                        val guideNext = getGuide(turns[i])
                        val banner = BannerInstructions.builder()
                            .distanceAlongGeometry(distances[i].toDouble())
                            .primary(
                                BannerText.builder()
                                    .type(guideNext.first)
                                    .modifier(guideNext.second)
                                    .components(
                                        listOf(
                                            BannerComponents.builder()
                                                .text(
                                                    if (turns[i] == 15) "Đến nơi" else names[i + 1]
                                                )
                                                .type("text")
                                                .build()
                                        )
                                    )!!
                                    .text(
                                        if (turns[i] == 15) "Đến nơi" else names[i + 1]
                                    )!!
                                    .build()
                            ).build()
                        bannerList.add(banner)
                    }

                    val intersection = StepIntersection.builder()
                        .rawLocation(doubleArrayOf(point.longitude(), point.latitude()))
                        .bearings(listOf(bearing.toInt()))
                        .entry(listOf(true))
                        .out(0)
                        .build()

                    val guide = getGuide(turns[i])
                    val maneuver = StepManeuver.builder()
                        .bearingBefore(beforeBearing)
                        .bearingAfter(bearing)
                        .rawLocation(doubleArrayOf(point.longitude(), point.latitude()))
                        .type(guide.first)
                        .modifier(guide.second)
                        .instruction(if (turns[i] == 15) "Đến nơi" else names[i])
                        .build()

                    val stepBuilder = LegStep.builder()
                        .distance(distances[i].toDouble())
                        .duration(durations[i].toDouble())
                        .geometry(PolylineUtils.encode(subPath, PRECISION_6))
                        .name(if (turns[i] == 15) "Đến nơi" else names[i])
                        .drivingSide("right")
                        .mode("driving")
                        .intersections(listOf(intersection))
                        .maneuver(maneuver)
                        .weight(0.0)
                        .voiceInstructions(listOf())
                        .bannerInstructions(bannerList)


                    if (turns[i] == 11) {
                        stepBuilder.rotaryName(StepManeuver.ROTARY)
                    }

                    steps.add(stepBuilder.build())
                }

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

        val temp = Gson().toJson(result);

        return result
    }

    private fun getGuide(turn: Int): Pair<String, String> {
        return when (turn) {
            1 -> (StepManeuver.CONTINUE to NavigationConstants.STEP_MANEUVER_MODIFIER_STRAIGHT)
            2 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT)
            3 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
            5 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_LEFT)
            7 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_SLIGHT_LEFT)
            8 -> (StepManeuver.TURN to NavigationConstants.STEP_MANEUVER_MODIFIER_LEFT)
            10 -> (StepManeuver.DEPART to NavigationConstants.STEP_MANEUVER_MODIFIER_STRAIGHT)
            11 -> (StepManeuver.ROUNDABOUT to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
            12 -> (StepManeuver.EXIT_ROUNDABOUT to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
            15 -> (StepManeuver.ARRIVE to NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT)
            else -> (StepManeuver.CONTINUE to NavigationConstants.STEP_MANEUVER_MODIFIER_STRAIGHT)
        }
    }


}