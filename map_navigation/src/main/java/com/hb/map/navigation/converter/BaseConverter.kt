package com.hb.map.navigation.converter

abstract class BaseConverter<D>(
    private val baseUrl: String,
    private val token: String
) : IConverter<D> {

    fun getToken(): String {
        return token
    }

    fun getBaseUrl() : String {
        return baseUrl
    }
}