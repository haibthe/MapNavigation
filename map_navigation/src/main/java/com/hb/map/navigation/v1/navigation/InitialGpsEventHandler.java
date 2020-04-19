package com.hb.map.navigation.v1.navigation;

import static com.hb.map.navigation.v1.navigation.NavigationMetricsWrapper.sendInitialGpsEvent;

class InitialGpsEventHandler {

    void send(double elapsedTime, String sessionId) {
        sendInitialGpsEvent(elapsedTime, sessionId);
    }
}
