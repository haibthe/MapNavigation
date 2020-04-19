package com.hb.map.navigation.v1.navigation.metrics.audio;

import android.content.Context;

class UnknownAudioType implements AudioTypeResolver {
    private static final String UNKNOWN = "unknown";

    @Override
    public void nextChain(AudioTypeResolver chain) {
    }

    @Override
    public String obtainAudioType(Context context) {
        return UNKNOWN;
    }
}
