package com.hb.map.navigation.v1.navigation.metrics.audio;

import android.content.Context;
import android.media.AudioManager;

class BluetoothAudioType implements AudioTypeResolver {
    private static final String BLUETOOTH = "bluetooth";
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
        return audioManager.isBluetoothScoOn() ? BLUETOOTH : chain.obtainAudioType(context);
    }
}