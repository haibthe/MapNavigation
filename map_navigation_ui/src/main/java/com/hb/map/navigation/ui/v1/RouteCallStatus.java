package com.hb.map.navigation.ui.v1;

import java.util.Date;

class RouteCallStatus {

    private static final int FIVE_SECONDS_IN_MILLISECONDS = 5000;
    private final Date callDate;
    private boolean responseReceived;

    RouteCallStatus(Date callDate) {
        this.callDate = callDate;
    }

    void setResponseReceived() {
        this.responseReceived = true;
    }

    boolean isRouting(Date currentDate) {
        if (responseReceived) {
            return false;
        }
        return diffInMilliseconds(callDate, currentDate) < FIVE_SECONDS_IN_MILLISECONDS;
    }

    private long diffInMilliseconds(Date callDate, Date currentDate) {
        return currentDate.getTime() - callDate.getTime();
    }
}