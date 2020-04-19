package com.hb.map.navigation.v1.navigation;

public class OfflineError {

    private final String message;

    OfflineError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
