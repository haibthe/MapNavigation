package com.hb.map.navigation.v1.navigation.metrics.audio;

import android.content.Context;

public interface AudioTypeResolver {
    void nextChain(AudioTypeResolver chain);

    String obtainAudioType(Context context);
}