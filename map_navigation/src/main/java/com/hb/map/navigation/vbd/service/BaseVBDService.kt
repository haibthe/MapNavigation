package com.hb.map.navigation.vbd.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class BaseVBDService<T, S>(serviceType: Class<S>) {

    private val serviceType: Class<S> = serviceType
    private var enableDebug: Boolean = false
    protected var _okHttpClient: OkHttpClient? = null
    private var callFactory: okhttp3.Call.Factory? = null
    private var retrofit: Retrofit? = null
    private var call: Call<T>? = null
    private var service: S? = null

    abstract fun baseUrl(): String

    protected abstract fun initializeCall(): Call<T>

    protected fun getCall(): Call<T> {
        this.call = this.call ?: initializeCall()
        return call!!
    }

    open fun executeCall() : Response<T> {
        return getCall().execute()
    }

    open fun enqueueCall(callback: Callback<T>?) {
        getCall().enqueue(callback)
    }

    open fun cloneCall(): Call<T> {
        return getCall().clone()
    }

    protected open fun getService(): S {
        if (service != null) {
            return service!!
        }
        val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(baseUrl())
            .addConverterFactory(GsonConverterFactory.create(getGsonBuilder().create()))
        val callFactory = getCallFactory()
        if (callFactory != null) {
            retrofitBuilder.callFactory(callFactory)
        } else {
            retrofitBuilder.client(getOkHttpClient())
        }
        retrofit = retrofitBuilder.build()
        service = retrofit!!.create(serviceType) as S
        return service!!
    }

    open fun getRetrofit(): Retrofit? {
        return retrofit
    }

    protected fun getGsonBuilder(): GsonBuilder {
        return GsonBuilder()
    }

    open fun getCallFactory(): okhttp3.Call.Factory? {
        return callFactory
    }

    open fun setCallFactory(callFactory: okhttp3.Call.Factory?) {
        this.callFactory = callFactory
    }

    open fun isEnableDebug(): Boolean {
        return enableDebug
    }

    @Synchronized
    protected open fun getOkHttpClient(): OkHttpClient {
        if (_okHttpClient == null) {
            _okHttpClient = if (isEnableDebug()) {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(logging)
                httpClient.build()
            } else {
                OkHttpClient()
            }
        }
        return _okHttpClient!!
    }
}