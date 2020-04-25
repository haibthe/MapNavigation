package com.hb.map.navigation.data.repositories

import com.google.gson.Gson
import com.hb.map.navigation.data.entities.VbdRouteResponse
import com.hb.map.navigation.data.store.AppDataSource
import com.hb.map.navigation.domain.repositories.AppRepository
import com.hb.map.navigation.v1.navigation.NavigationConstants.*
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

    val TEMP = "{" +
            "    \"baseUrl\": \"\"," +
            "    \"user\": \"mapbox\"," +
            "    \"profile\": \"driving-traffic\"," +
            "    \"coordinates\": [" +
            "      [106.691777, 10.789965]," +
            "      [106.677222, 10.774447]" +
            "    ]," +
            "    \"alternatives\": faolse," +
            "    \"language\": \"en\"," +
            "    \"bearings\": \";\"," +
            "    \"continue_straight\": true," +
            "    \"roundabout_exits\": true," +
            "    \"geometries\": \"polyline6\"," +
            "    \"overview\": \"full\"," +
            "    \"steps\": true," +
            "    \"annotations\": \"congestion,distance\"," +
            "    \"voice_instructions\": true," +
            "    \"banner_instructions\": true," +
            "    \"voice_units\": \"metric\"," +
            "    \"access_token\": \"pk.eyJ1Ijoibm9uMjM1IiwiYSI6ImNrOGFnZHYzcDAxdDMzb3BpNmthdW1lMHAifQ.BmGzpCy_5Pl2XxIS7XCYbA\"," +
            "    \"uuid\": \"ck953h0pk00an5qqnkl9zcca5\"" +
            "  }"

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
                val bc = BannerComponents.builder().type("text")
                val bt = BannerText.builder()
                val banner = BannerInstructions.builder()
                for (i in 0 until size) {

                    val pair = (idx to indices[i])
                    idx = indices[i]
                    val point = path[indices[i]]
//                    val beforeIndex = if (indices[i] == 0) 0 else indices[i] - 1
//                    val afterIndex = if (indices[i] == totalIndices) totalIndices else indices[i] + 1
                    val beforeBearing = 0.0
//                    kotlin.math.abs(TurfMeasurement.bearing(point, path[beforeIndex]))
                    val bearing = 0.0
//                    kotlin.math.abs(TurfMeasurement.bearing(point, path[afterIndex]))
                    val subPath = arrayListOf<Point>()
                    if (i == 0) {
                        subPath.add(Point.fromLngLat(106.6923, 10.7901))
                    }
                    for (j in pair.first..pair.second) {
                        subPath.add(path[j])
                    }
                    val guide = getGuide(turns[i])
                    val bannerList = ArrayList<BannerInstructions>()
                    if (turns[i] != 15) {
                        banner.distanceAlongGeometry(distances[i].toDouble())
                        var name = names[i].replace("Đường", "")
                        bc.text(name)
                        bt.type(guide.first)
                            .modifier(guide.second)
                            .text(name)!!
                            .components(listOf(bc.build()))
                        banner.primary(bt.build())


                        val guideNext = getGuide(turns[i + 1])
                        name = names[i + 1].replace("Đường", "")
                        bc.text(name)
                        bt.type(guideNext.first)
                            .modifier(guideNext.second)
                            .text(name)!!
                            .components(listOf(bc.build()))
                        banner.sub(bt.build())

                        bannerList.add(banner.build())
                    } else {
                        val text = "Đến nơi" + names[i]
                        bc.text(text)
                        bt.type(guide.first)
                            .modifier(guide.second)
                            .text(text)!!
                            .components(listOf(bc.build()))

                        banner.distanceAlongGeometry(distances[i].toDouble())
                            .primary(bt.build())
                        bannerList.add(banner.build())
                    }

                    val intersection = StepIntersection.builder()
                        .rawLocation(doubleArrayOf(point.longitude(), point.latitude()))
                        .bearings(listOf(bearing.toInt()))
                        .entry(listOf(true))
                        .out(0)
                        .build()


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


                    if (turns[i] in 11..12) {
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

        val temp = Gson().toJson(result)
        Timber.i("DirectionsRoute: $temp")

        return result
    }

    private fun getGuide(turn: Int): Pair<String, String> {
        return when (turn) {
            1 -> (StepManeuver.CONTINUE to STEP_MANEUVER_MODIFIER_STRAIGHT)
            2 -> (StepManeuver.TURN to STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT)
            3 -> (StepManeuver.TURN to STEP_MANEUVER_MODIFIER_RIGHT)
            5 -> (StepManeuver.TURN to STEP_MANEUVER_MODIFIER_UTURN)
            7 -> (StepManeuver.TURN to STEP_MANEUVER_MODIFIER_SLIGHT_LEFT)
            8 -> (StepManeuver.TURN to STEP_MANEUVER_MODIFIER_LEFT)
            10 -> (StepManeuver.DEPART to STEP_MANEUVER_MODIFIER_STRAIGHT)
            11 -> (StepManeuver.ROUNDABOUT to STEP_MANEUVER_TYPE_ROUNDABOUT)
            12 -> (StepManeuver.TURN to STEP_MANEUVER_MODIFIER_RIGHT)
            15 -> (StepManeuver.ARRIVE to STEP_MANEUVER_MODIFIER_RIGHT)
            else -> (StepManeuver.CONTINUE to STEP_MANEUVER_MODIFIER_STRAIGHT)
        }
    }
}