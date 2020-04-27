package com.hb.map.navigation.converter

import com.mapbox.api.directions.v5.models.DirectionsRoute

interface IConverter<D> {

    fun convert(data: D) : DirectionsRoute?
}