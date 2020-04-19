package com.hb.map.navigation.ui.v1.voice;

interface SpeechListener {

    void onStart();

    void onDone();

    void onError(String errorText, SpeechAnnouncement speechAnnouncement);
}
