package com.hb.map.navigation.v1.navigation.metrics;

import android.annotation.SuppressLint;

import androidx.annotation.Keep;

@Keep
@SuppressLint("ParcelCreator")
class NavigationArriveEvent extends NavigationEvent {
    private static final String NAVIGATION_ARRIVE = "navigation.arrive";

    NavigationArriveEvent(PhoneState phoneState) {
        super(phoneState);
    }

    @Override
    protected String getEventName() {
        return NAVIGATION_ARRIVE;
    }
}
