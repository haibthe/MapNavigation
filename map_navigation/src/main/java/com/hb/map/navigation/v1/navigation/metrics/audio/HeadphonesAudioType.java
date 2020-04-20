package com.hb.map.navigation.v1.navigation.metrics.audio;

import android.content.Context;
import android.media.AudioManager;

class HeadphonesAudioType implements AudioTypeResolver {
    private static final String HEADPHONES = "headphones";
    private AudioTypeResolver chain;

    @Override
    public void nextChain(AudioTypeResolver chain) {
        this.chain = chain;
    }

    @Override
    public String obtainAudioType(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            return "unknown";
        }
        return audioManager.isWiredHeadsetOn() ? HEADPHONES : chain.obtainAudioType(context);
    }
}