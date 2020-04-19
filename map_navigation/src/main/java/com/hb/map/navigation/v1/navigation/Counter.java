package com.hb.map.navigation.v1.navigation;


import androidx.annotation.Keep;

@Keep
abstract class Counter<N extends Number> {
    protected final String name;
    protected final N value;

    Counter(String name, N value) {
        this.name = name;
        this.value = value;
    }
}
