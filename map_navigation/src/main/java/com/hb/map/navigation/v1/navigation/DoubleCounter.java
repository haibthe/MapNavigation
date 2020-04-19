package com.hb.map.navigation.v1.navigation;


import androidx.annotation.Keep;

@Keep
class DoubleCounter extends Counter<Double> {

    DoubleCounter(String name, Double value) {
        super(name, value);
    }
}
