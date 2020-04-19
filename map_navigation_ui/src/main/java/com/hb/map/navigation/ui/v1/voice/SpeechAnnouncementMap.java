package com.hb.map.navigation.ui.v1.voice;

import androidx.core.util.Pair;

import java.util.HashMap;

class SpeechAnnouncementMap extends HashMap<Boolean, SpeechAnnouncementUpdate> {

    private static final String SSML_TEXT_TYPE = "ssml";
    private static final String TEXT_TYPE = "text";

    SpeechAnnouncementMap() {
        super(2);
        put(true, speechAnnouncement -> new Pair<>(speechAnnouncement.ssmlAnnouncement(), SSML_TEXT_TYPE));
        put(false, speechAnnouncement -> new Pair<>(speechAnnouncement.announcement(), TEXT_TYPE));
    }
}
