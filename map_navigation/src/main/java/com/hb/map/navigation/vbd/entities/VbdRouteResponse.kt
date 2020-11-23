package com.hb.map.navigation.vbd.entities

import com.google.gson.annotations.SerializedName


data class VbdRouteResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("routes")
    val routes: List<Route>?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("via_points")
    val viaPoints: List<DoubleArray>
)

data class Route(
    @SerializedName("geometry")
    val geometry: String,
    @SerializedName("steps")
    val steps: Steps?,
    @SerializedName("via_distances")
    val viaDistances: List<Int>,
    @SerializedName("via_durations")
    val viaDurations: List<Int>,
    @SerializedName("via_indices")
    val viaIndices: List<Int>
) {
    data class Steps(
        @SerializedName("distances")
        val distances: List<Int>,
        @SerializedName("durations")
        val durations: List<Int>,
        @SerializedName("indices")
        val indices: List<Int>,
        @SerializedName("names")
        val names: List<String>,
        @SerializedName("turns")
        val turns: List<Int>
    )
}