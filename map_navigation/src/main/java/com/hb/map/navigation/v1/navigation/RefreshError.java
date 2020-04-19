package com.hb.map.navigation.v1.navigation;

public class RefreshError {
    private final String message;

    RefreshError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
