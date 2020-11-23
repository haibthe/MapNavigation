package com.hb.map.navigation.vbd.service

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.auto.value.AutoValue
import com.hb.map.navigation.vbd.entities.VbdRouteResponse
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import okhttp3.EventListener
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@AutoValue
abstract class VBDDirections : BaseVBDService<VbdRouteResponse, DirectionsService>(
    DirectionsService::class.java
) {

    companion object {
        @JvmStatic
        fun formatCoordinates(coords: List<Point>): String {
            return PolylineUtils.encode(coords, PRECISION_6)
        }

        @JvmStatic
        fun builder(): Builder {
            return AutoValue_VBDDirections.Builder()
//            return AutoValue_T4CHDirections.Builder()
        }
    }

    @NonNull
    abstract fun coordinates(): List<Point>

    @NonNull
    abstract fun steps(): Int

    @NonNull
    abstract fun vehicle(): Int

    @Nullable
    abstract fun interceptor(): Interceptor?

    @Nullable
    abstract fun eventListener(): EventListener?


    override fun initializeCall(): Call<VbdRouteResponse> {
        return getService().getRoute(
            locs = formatCoordinates(coordinates()),
            step = steps(),
            veh = vehicle()
        )
    }

    override fun enqueueCall(callback: Callback<VbdRouteResponse>?) {
        getCall().enqueue(object : Callback<VbdRouteResponse> {
            override fun onFailure(call: Call<VbdRouteResponse>, t: Throwable) {
                callback?.onFailure(call, t)
            }

            override fun onResponse(call: Call<VbdRouteResponse>, response: Response<VbdRouteResponse>) {
                callback?.onResponse(call, response)
            }
        })
    }


    override fun getOkHttpClient(): OkHttpClient {
        if (_okHttpClient == null) {
            val client = OkHttpClient.Builder()
            if (isEnableDebug()) {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
                client.addInterceptor(logging)
            }
            val interceptor = interceptor()
            if (interceptor != null) {
                client.addInterceptor(interceptor)
            }
            val eventListener = eventListener()
            if (eventListener != null) {
                client.eventListener(eventListener)
            }
            _okHttpClient = client.build()
        }
        return _okHttpClient!!
    }


    @AutoValue.Builder
    abstract class Builder {
        private val coordinates: MutableList<Point> = ArrayList()
        private var destination: Point? = null
        private var origin: Point? = null

        open fun origin(@NonNull origin: Point?): Builder {
            this.origin = origin
            return this
        }

        open fun destination(@NonNull destination: Point?): Builder {
            this.destination = destination
            return this
        }

        open fun addWaypoint(@NonNull waypoint: Point?): Builder {
            coordinates.add(waypoint!!)
            return this
        }

        abstract fun baseUrl(baseUrl: String): Builder

        abstract fun steps(steps: Int): Builder

        abstract fun vehicle(veh: Int): Builder

        abstract fun interceptor(interceptor: Interceptor?): Builder?

        abstract fun eventListener(eventListener: EventListener?): Builder?

        abstract fun coordinates(@NonNull coordinates: List<Point?>?): Builder?

        abstract fun autoBuild(): VBDDirections

        fun build(): VBDDirections {
            if (origin != null) {
                coordinates.add(0, origin!!)
            }
            if (destination != null) {
                coordinates.add(destination!!)
            }

            coordinates(coordinates)
//            steps(1)
//            vehicle(3)
//            baseUrl("http://web.c4i2.net/")

            return autoBuild()
        }
    }


}