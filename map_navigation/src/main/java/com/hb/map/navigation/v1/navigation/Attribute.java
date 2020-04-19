package com.hb.map.navigation.v1.navigation;


import androidx.annotation.Keep;

@Keep
class Attribute {
    private final String name;
    private final String value;

    Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
