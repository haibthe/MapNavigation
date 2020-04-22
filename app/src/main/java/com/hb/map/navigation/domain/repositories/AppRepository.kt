package com.hb.map.navigation.domain.repositories

import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.mapboxsdk.geometry.LatLng
import io.reactivex.Observable

interface AppRepository {
    fun findRoute(start: LatLng, end: LatLng) : Observable<DirectionsRoute?>
}