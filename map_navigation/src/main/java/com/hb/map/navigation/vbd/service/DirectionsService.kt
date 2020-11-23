package com.hb.map.navigation.vbd.service

import com.hb.map.navigation.vbd.entities.VbdRouteResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsService {

    @GET("api/viaroute")
    fun getRoute(
        @Query("locs") locs: String,
        @Query("veh") veh: Int = 3,
        @Query("steps") step: Int = 1,
        @Query("geom") geom: Int = 1,
        @Query("crit") crit: Int = 0,
        @Query("barr") barr: Int = 0,
        @Query("user") user: String = "undefinded"
    ) : Call<VbdRouteResponse>
}