package com.hb.map.navigation.v1.navigation.metrics;

public interface TelemetryEvent {

    String getEventId();

    SessionState getSessionState();
}
