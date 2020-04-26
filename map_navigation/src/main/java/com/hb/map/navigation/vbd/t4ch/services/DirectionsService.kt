package com.hb.map.navigation.vbd.t4ch.services

import com.hb.map.navigation.vbd.t4ch.entities.VbdRouteResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsService {

    @GET("http://web.c4i2.net/api/viaroute")
    fun getRoute(
        @Query("locs") locs: String,
        @Query("veh") veh: Int = 3,
        @Query("steps") step: Int = 1,
        @Query("geom") geom: Int = 1,
        @Query("user") user: String = "undefinded"
    ) : Call<VbdRouteResponse>
}