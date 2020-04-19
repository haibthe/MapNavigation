package com.hb.map.navigation.ui.v1.voice;


import androidx.core.util.Pair;

interface SpeechAnnouncementUpdate {

    Pair<String, String> buildTextAndTypeFrom(SpeechAnnouncement speechAnnouncement);
}
