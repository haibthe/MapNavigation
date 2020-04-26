package com.hb.map.navigation.data.store

import android.content.Context
import com.google.gson.Gson
import com.hb.map.navigation.app.R
import com.hb.map.navigation.utils.AppUtils
import com.hb.map.navigation.vbd.t4ch.entities.VbdRouteResponse
import io.reactivex.Observable

interface AppDataSource {

    interface Local {
        fun getRouteFromAssets(fileName: String) : Observable<VbdRouteResponse>

        fun getAccessToken(): String
    }

    interface Service {

    }
}

class AppLocalDataSource(
    private val context: Context
) : AppDataSource.Local {

    override fun getRouteFromAssets(fileName: String): Observable<VbdRouteResponse> {
        return Observable.just(
            AppUtils.loadStringFromAssets(context, fileName)
        ).map {
            Gson().fromJson(it, VbdRouteResponse::class.java)
        }
    }

    override fun getAccessToken(): String {
        return context.getString(R.string.mapbox_access_token)
    }
}