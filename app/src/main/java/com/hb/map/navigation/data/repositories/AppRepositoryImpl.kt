package com.hb.map.navigation.data.repositories

import com.hb.map.navigation.data.store.AppDataSource
import com.hb.map.navigation.domain.repositories.AppRepository
import com.hb.map.navigation.vbd.features.t4ch.T4CHConverter
import com.hb.map.navigation.vbd.entities.VbdRouteResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import io.reactivex.Observable


class AppRepositoryImpl(
    private val local: AppDataSource.Local,
    private val service: AppDataSource.Service
) : AppRepository {

    private var mStartPoint: Point? = null

    override fun findRoute(start: LatLng, end: LatLng): Observable<DirectionsRoute?> {
        mStartPoint = Point.fromLngLat(start.longitude, start.latitude)
        return local.getRouteFromAssets("vbd_route.json")
            .map(this::mapToDirectionRoute)
    }

    private fun mapToDirectionRoute(res: VbdRouteResponse): DirectionsRoute? {
        val converter = T4CHConverter("", "", mStartPoint)
        return converter.convert(res)
    }


}